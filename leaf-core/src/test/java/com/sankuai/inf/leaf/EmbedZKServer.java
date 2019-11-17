package com.sankuai.inf.leaf;

import org.apache.curator.test.TestingServer;

import java.io.File;
import java.io.IOException;

/**
 * @author jiangxinjun
 * @date 2019/11/17
 */
public class EmbedZKServer {

    private static final int PORT = 2181;

    private static volatile TestingServer testingServer;

    public static void start() throws Exception {
        if (null != testingServer) {
            return;
        }
        testingServer = new TestingServer(PORT, new File(String.format("target/test_zk_data/%s/", System.nanoTime())));
    }

    /**
     * 不需要手动停止
     * @throws IOException .
     * @author jiangxinjun
     */
    public static void stop() throws IOException {
        testingServer.close();
    }
}
