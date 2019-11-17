package com.sankuai.inf.leaf.segment;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.TestBootstrap;
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
import java.util.concurrent.*;

/**
 * @author jiangxinjun
 * @date 2019/09/04
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootstrap.class, properties = {"leaf.segment.enable=true", "leaf.snowflake.enable:false"})
public class IDGenServiceTest {

    @Autowired
    @Qualifier(Constants.LEAF_SEGMENT_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    private ExecutorService executorService = new ThreadPoolExecutor(50, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    @Test
    public void testGetId() {
        for (int i = 0; i < 100; ++i) {
            Result r = idGen.get("leaf-segment-test");
            System.out.println(r);
        }
    }

    @Test
    public void testPerformance() {
        StopWatch sw = new StopWatch();
        sw.start();
        int count  = 5000;
        for (int i = 1; i < count; ++i) {
            Result result = idGen.get("leaf-segment-test1");
            if (Status.EXCEPTION == result.getStatus()) {
                System.out.println(result);
            }
        }
        sw.stop();
        System.out.println(sw.prettyPrint());
        // 计算并发
        double concurrent = count / sw.getTotalTimeSeconds();
        System.out.println("concurrent:" + concurrent);
    }

    @Test
    public void testSegmentGetFromCachePerformance() throws InterruptedException {
        org.perf4j.StopWatch sw = new Slf4JStopWatch();
        int count = 20000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        sw.start();
        for (int i = 0; i < count; ++i) {
            executorService.submit(() -> {
                Result result = idGen.get("leaf-segment-test2");
                if (Status.EXCEPTION == result.getStatus()) {
                    log.error("get exception:{}", result);
                }
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
