package com.sankuai.inf.leaf;

import org.junit.After;
import org.junit.Before;
import redis.embedded.RedisServer;

import java.io.IOException;

/**
 * 内嵌redis启动类
 * 需要redis服务的单元测试类可以继承该类
 * 类内共享一个redis，注意数据问题；类之间不共享redis
 *
 * @author Leach
 * @date 2017/10/11
 */
public class EmbeddedRedis {

    private static RedisServer redisServer = null;

    @Before
    public void start() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @After
    public void stop() {
        redisServer.stop();
        redisServer = null;
    }
}
