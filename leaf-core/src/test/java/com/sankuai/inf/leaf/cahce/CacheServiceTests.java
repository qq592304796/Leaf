package com.sankuai.inf.leaf.cahce;

import com.sankuai.inf.leaf.EmbeddedRedis;
import com.sankuai.inf.leaf.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;

/**
 * @author jiangxinjun
 * @date 2019/11/17
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"leaf.segment.enable=false", "leaf.snowflake.enable:false"})
public class CacheServiceTests extends EmbeddedRedis {

    @Autowired
    private CacheService cacheService;

    private ExecutorService executorService = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Test
    public void testGetId() throws InterruptedException {
        StopWatch sw = new Slf4JStopWatch();
        int count = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        sw.start();
        for (int i = 0; i < count; ++i) {
            executorService.submit(() -> {
                Long id = cacheService.getId("TEST");
                log.debug("id:{}", id);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        sw.stop();
        System.out.println(sw.toString());
        // 计算并发
        BigDecimal concurrent = BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(sw.getElapsedTime()), 6, RoundingMode.DOWN);
        System.out.println("concurrent:" + concurrent);
    }

    @Test
    public void testGetIdWithExpired() throws InterruptedException {
        StopWatch sw = new Slf4JStopWatch();
        int count = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        sw.start();
        for (int i = 0; i < count; ++i) {
            executorService.submit(() -> {
                Long id = cacheService.getIdWithExpired("test");
                log.debug("id:{}", id);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        sw.stop();
        System.out.println(sw.toString());
        // 计算并发
        BigDecimal concurrent = BigDecimal.valueOf(count).multiply(BigDecimal.valueOf(1000)).divide(BigDecimal.valueOf(sw.getElapsedTime()), 6, RoundingMode.DOWN);
        System.out.println("concurrent:" + concurrent);
    }

}
