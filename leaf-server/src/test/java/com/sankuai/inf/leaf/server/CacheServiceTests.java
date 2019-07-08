package com.sankuai.inf.leaf.server;

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

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheServiceTests {

    @Autowired
    private CacheService cacheService;

    private ExecutorService executorService = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @Test
    public void testGetId() throws InterruptedException {
        StopWatch sw = new Slf4JStopWatch();
        int count = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        sw.start();
        for (int i = 0; i < count; ++i) {
            executorService.submit(() -> {
                Long id = cacheService.getId("TEST");
                //System.out.println(id);
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
                //System.out.println(id);
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
