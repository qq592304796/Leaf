package com.sankuai.inf.leaf.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author jiangxinjun
 * @date 2019/11/12
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ExecutorProperties {

    /**
     * 核心线程数
     */
    int corePoolSize = 20;

    /**
     * 最大线程数
     */
    int maximumPoolSize = 50;

    /**
     * 线程空闲时间（秒）
     */
    long keepAliveTime = 60;

    /**
     * 队列数量
     */
    int queueCount = 100;

}
