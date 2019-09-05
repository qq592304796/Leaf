package com.sankuai.inf.leaf.segment;

import com.alibaba.druid.pool.DruidDataSource;
import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.LeafAutoConfiguration;
import com.sankuai.inf.leaf.TestBootstrap;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

/**
 * @author jiangxinjun
 * @date 2019/09/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootstrap.class)
public class IDGenServiceTest {

    @Autowired
    @Qualifier(Constants.LEAF_SEGMENT_ID_GEN_IMPL_NAME)
    private IDGen idGen;

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

}
