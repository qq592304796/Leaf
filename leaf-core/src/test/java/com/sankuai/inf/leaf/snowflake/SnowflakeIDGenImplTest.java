package com.sankuai.inf.leaf.snowflake;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.TestBootstrap;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootstrap.class)
public class SnowflakeIDGenImplTest {

    @Autowired
    @Qualifier(Constants.LEAF_SNOWFLAKE_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    @Test
    public void testGetId() {
        Properties properties = PropertyFactory.getProperties();

        IDGen idGen = new SnowflakeIDGenImpl(properties.getProperty("leaf.zk.list"), 8080, properties.getProperty("leaf.name"));
        for (int i = 1; i < 1000; ++i) {
            Result r = idGen.get("a");
            System.out.println(r);
        }
    }

    @Test
    public void testResolveId() {
        long id = 1146367380083966026L;
        String binaryStr = Long.toBinaryString(id);
        // 如果小于64位进行补0
        if (binaryStr.length() < 64) {
            String zore = "";
            for (int i = 0; i < 64 - binaryStr.length(); i++) {
                zore+="0";
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
        long maxTime = -1L ^ (-1L << 41);
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
        List<Long> caches = new ArrayList<>(10000);
        sw.start();
        for (int i = 1; i < 10000; ++i) {
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
    }

}
