package com.sankuai.inf.leaf.snowflake;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.EmbedZKServer;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.TestBootstrap;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author jiangxinjun
 * @date 2019/11/17
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootstrap.class, properties = {"leaf.segment.enable=false", "leaf.snowflake.enable:true"})
public class SnowflakeIDGenImplTest extends EmbedZKServer {

    @Autowired
    @Qualifier(Constants.LEAF_SNOWFLAKE_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    private ExecutorService executorService = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Test
    public void testGetId() {
        Properties properties = PropertyFactory.getProperties();

        IDGen idGen = new SnowflakeIDGenImpl(properties.getProperty("leaf.zk.list"), 8080, properties.getProperty("leaf.name"));
        for (int i = 1; i < 1000; ++i) {
            Result r = idGen.get("a");
            System.out.println(r);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testResolveId() {
        long id = 1146367380083966026L;
        String binaryStr = Long.toBinaryString(id);
        // 如果小于64位进行补0
        if (binaryStr.length() < 64) {
            StringBuilder zore = new StringBuilder();
            for (int i = 0; i < 64 - binaryStr.length(); i++) {
                zore.append("0");
            }
            binaryStr = String.format(zore + "%s", binaryStr);
        }
        String symbol = binaryStr.substring(0, 1);
        String timeStr = binaryStr.substring(1, 42);
        String workerIdStr = binaryStr.substring(42, 52);
        String sequenceStr = binaryStr.substring(52);
        System.out.println("symbol:" + symbol);
        System.out.println("timeStr:" + timeStr);
        System.out.println("workerIdStr:" + workerIdStr);
        System.out.println("sequenceStr:" + sequenceStr);
    }

    @Test
    public void testMaximumId() {
        long maxId = Long.MAX_VALUE;
        System.out.println(maxId);
        long maxIdIncrease = maxId + 1;
        System.out.println(maxIdIncrease);
    }

    @Test
    public void testTimeDifference() {
        //long maxTime = -1L ^ (-1L << 41);
        long maxTime = ~(-1L << 41);
        long twepoch = -636817890353L;
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - twepoch;
        if (difference > maxTime) {
            System.out.println("difference is max:");
        }
        System.out.println("maxTime:" + maxTime);
        System.out.println("twepoch:" + twepoch);
        System.out.println("currentTime:" + currentTime);
        System.out.println("difference:" + difference);
        long year = difference / 1000 / 60 / 60 / 24 / 365;
        System.out.println("year:" + year);
        long maxYear = maxTime / 1000 / 60 / 60 / 24 / 365;
        System.out.println("maxYear:" + maxYear);
    }

    @Test
    public void testIncreasingTrend() {
        StopWatch sw = new StopWatch();
        int count  = 10000;
        List<Long> caches = new ArrayList<>(count);
        sw.start();
        for (int i = 1; i <= count; i++) {
            Result result = idGen.get("a");
            System.out.println(result);
            if (!caches.isEmpty()) {
                //boolean isMax = caches.stream().allMatch(l -> l.compareTo(result.getId()) < 0);
                boolean isMax = true;
                for (Long item : caches) {
                    if (item.compareTo(result.getId()) >= 0) {
                        isMax = false;
                        break;
                    }
                }
                if (!isMax) {
                    System.out.println("is not max:" + result);
                }
            }
            caches.add(result.getId());
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 计算并发
        double concurrent = count / sw.getTotalTimeSeconds();
        log.debug("concurrent:{}", concurrent);
    }

    @Test
    public void testSnowflakeIdPerformance() throws InterruptedException {
        org.perf4j.StopWatch sw = new Slf4JStopWatch();
        int count = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        sw.start();
        for (int i = 1; i <= count; i++) {
            executorService.submit(() -> {
                Result result = idGen.get("leaf-segment-test");
                if (Status.EXCEPTION == result.getStatus()) {
                    log.error("get exception:{}", result);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        sw.stop();
        log.info(sw.toString());
        // 计算并发
        BigDecimal concurrent = BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(sw.getElapsedTime()), 6, RoundingMode.DOWN);
        log.info("concurrent:{}", concurrent);
    }

}
