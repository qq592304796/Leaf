package com.sankuai.inf.leaf.segment;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.exception.InitException;
import com.sankuai.inf.leaf.properties.ExecutorProperties;
import com.sankuai.inf.leaf.properties.LeafProperties;
import com.sankuai.inf.leaf.properties.SegmentProperties;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import com.sankuai.inf.leaf.segment.model.Segment;
import com.sankuai.inf.leaf.segment.model.SegmentBuffer;
import com.sankuai.inf.leaf.segment.model.SegmentStep;
import lombok.extern.slf4j.Slf4j;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Slf4j
public class SegmentIDGenImpl implements IDGen, InitializingBean {

    /**
     * IDCache未初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_IDCACHE_INIT_FALSE = -1;
    /**
     * key不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_NOT_EXISTS = -2;
    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    private static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;

    private ExecutorService executorService;
    private volatile boolean initOk = false;
    private Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();

    @Resource
    private LeafProperties leafProperties;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired(required = false)
    private IDAllocDao dao;

    public static class UpdateThreadFactory implements ThreadFactory {

        private static AtomicInteger atomicInteger = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + atomicInteger.incrementAndGet());
        }
    }

    @Override
    public void afterPropertiesSet() throws InitException {
        ExecutorProperties executor = leafProperties.getSegment().getExecutor();
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(executor.getQueueCount());
        executorService = new ThreadPoolExecutor(executor.getCorePoolSize(), executor.getMaximumPoolSize(), executor.getKeepAliveTime(), TimeUnit.SECONDS, workQueue, new UpdateThreadFactory());
        if (init()) {
            log.info("Segment Service Init Successfully");
        } else {
            throw new InitException("Segment Service Init Fail");
        }
    }

    @Override
    public boolean init() {
        log.info("Init ...");
        // 确保加载到kv后才初始化成功
        updateCacheFromDb();
        initOk = true;
        updateCacheFromDbAtEveryMinute();
        return initOk;
    }

    private void updateCacheFromDbAtEveryMinute() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("check-idCache-thread");
            t.setDaemon(true);
            return t;
        });
        service.scheduleWithFixedDelay(this::updateCacheFromDb, 60, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        log.info("update cache from db");
        StopWatch sw = new Slf4JStopWatch();
        try {
            List<String> dbTags = dao.getAllTags();
            if (dbTags == null || dbTags.isEmpty()) {
                return;
            }
            List<String> cacheTags = new ArrayList<>(cache.keySet());
            List<String> insertTags = new ArrayList<>(dbTags);
            List<String> removeTags = new ArrayList<>(cacheTags);
            //db中新加的tags灌进cache
            insertTags.removeAll(cacheTags);
            for (String tag : insertTags) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                log.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }
            //cache中已失效的tags从cache删除
            removeTags.removeAll(dbTags);
            for (String tag : removeTags) {
                cache.remove(tag);
                log.info("Remove tag {} from IdCache", tag);
            }
        } catch (Exception e) {
            log.warn("update cache from db exception", e);
        } finally {
            sw.stop("updateCacheFromDb");
        }
    }

    @Override
    public Result get(final String key) {
        return get(key, 1);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public Result get(final String key, final int step) {
        if (!initOk) {
            return new Result(EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    if (!buffer.isInitOk()) {
                        try {
                            updateSegmentFromDb(key, buffer.getCurrent());
                            log.info("Init buffer. Update leafkey {} {} from db", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            log.warn("Init buffer {} exception", buffer.getCurrent(), e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key), step);
        }
        return new Result(EXCEPTION_ID_KEY_NOT_EXISTS, Status.EXCEPTION);
    }

    private void updateSegmentFromDb(String key, Segment segment) {
        SegmentProperties segmentProperties = leafProperties.getSegment();
        StopWatch sw = new Slf4JStopWatch();
        SegmentBuffer buffer = segment.getBuffer();
        LeafAlloc leafAlloc;
        if (!buffer.isInitOk()) {
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setStep(leafAlloc.getStep());
            // leafAlloc中的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        } else if (buffer.getUpdateTimestamp() == 0) {
            leafAlloc = dao.updateMaxIdAndGetLeafAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAlloc.getStep());
            buffer.setMinStep(leafAlloc.getStep());//leafAlloc中的step为DB中的step
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < segmentProperties.getSegmentDuration()) {
                if (nextStep * segmentProperties.getStepIncrementMultiple() <= segmentProperties.getMaxStep()) {
                    nextStep = nextStep * segmentProperties.getStepIncrementMultiple();
                }
            } else if (duration >= segmentProperties.getSegmentDuration() * segmentProperties.getTimeRangeMultiple()) {
                nextStep = nextStep / segmentProperties.getStepIncrementMultiple() >= buffer.getMinStep() ? nextStep / segmentProperties.getStepIncrementMultiple() : nextStep;
            }
            log.info("leafKey[{}], step[{}], duration[{}mins], nextStep[{}]", key, buffer.getStep(), String.format("%.2f",((double)duration / (1000 * 60))), nextStep);
            LeafAlloc temp = new LeafAlloc();
            temp.setKey(key);
            temp.setStep(nextStep);
            leafAlloc = dao.updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            //leafAlloc的step为DB中的step
            buffer.setMinStep(leafAlloc.getStep());
        }
        // must set value before set max
        long value = leafAlloc.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAlloc.getMaxId());
        segment.setStep(buffer.getStep());
        sw.stop("updateSegmentFromDb", key + " " + segment);
    }

    private Result getIdFromSegmentBuffer(final SegmentBuffer buffer, final int step) {
        SegmentProperties segmentProperties = leafProperties.getSegment();
        int retryTime = 0;
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < segmentProperties.getLoadingNewThreshold() * segment.getStep()) && buffer.getThreadRunning().compareAndSet(false, true)) {
                    buffer.setCountDownLatch(new CountDownLatch(1));
                    executorService.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            log.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            log.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                        } finally {
                            buffer.getCountDownLatch().countDown();
                            if (updateOk) {
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.wLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                Result result = getAndUpdate(segment, step);
                if (result != null) {
                    return result;
                }
            } finally {
                buffer.rLock().unlock();
            }
            if (!buffer.isNextReady()) {
                retryTime++;
                if (buffer.getThreadRunning().get()) {
                    final Segment segment = buffer.getCurrent();
                    // 判断是否已达切换条件
                    if (segment.getValue().get() >= segment.getMax() - 1) {
                        // 判断如果超过最大重试次数，等待号段加载
                        if (retryTime <= segmentProperties.getMaxRetryTime()) {
                            try {
                                if (segmentProperties.getWaitTimeout() >= 0) {
                                    // 等待号段加载完成，循环等待三次
                                    buffer.getCountDownLatch().await(segmentProperties.getWaitTimeout(), TimeUnit.MILLISECONDS);
                                } else {
                                    buffer.getCountDownLatch().await();
                                }
                            } catch (InterruptedException ignored) {
                                log.warn("Thread {} Interrupted", Thread.currentThread().getName());
                            }
                            continue;
                        }
                    }
                }
            }
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                Result result = getAndUpdate(segment, step);
                if (result != null) {
                    return result;
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    if (retryTime <= segmentProperties.getMaxRetryTime()) {
                        continue;
                    }
                    log.error("Both two segments in {} are not ready!, retryTime:{}", buffer, retryTime);
                    return new Result(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.EXCEPTION);
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private Result getAndUpdate(Segment segment, int step) {
        long value;
        int finalStep;
        // 如果当前没有步长，这是为当前步长的1/10
        int minClientStep = segment.getBuffer().getMinStep() / 10;
        int maxClientStep = segment.getBuffer().getStep() / 10;
        if (minClientStep < 0) {
            minClientStep = 1;
        }
        if (maxClientStep < 0) {
            maxClientStep = 1;
        }
        if (step <= 0) {
            // 客户端的初始步长为当前segment步长的1/10
            finalStep = maxClientStep;
        } else {
            finalStep = step;
        }
        if (step == 1) {
            value = segment.getValue().getAndIncrement();
        } else {
            value = segment.getValue().getAndUpdate(operand -> {
                long next = operand + 1;
                if (next >= segment.getMax()) {
                    return next;
                }
                next = operand + finalStep;
                if (next >= segment.getMax()) {
                    return segment.getMax();
                }
                return operand + finalStep;
            });
        }
        if (value < segment.getMax()) {
            SegmentStep segmentStep = new SegmentStep();
            segmentStep.setStep(minClientStep);
            segmentStep.setMaxId(segment.getValue().get());
            segmentStep.setActualStep((int) (segment.getValue().get() - value));
            return new Result(segmentStep, Status.SUCCESS);
        }
        return null;
    }

    public List<LeafAlloc> getAllLeafAllocs() {
        return dao.getAllLeafAllocs();
    }

    public Map<String, SegmentBuffer> getCache() {
        return cache;
    }
}
