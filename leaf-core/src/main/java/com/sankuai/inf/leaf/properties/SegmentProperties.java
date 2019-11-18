package com.sankuai.inf.leaf.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author jiangxinjun
 * @date 2019/11/12
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(callSuper = true)
@Data
public class SegmentProperties {

    /**
     * 最大步长
     */
    int maxStep = 1_000_000;

    /**
     * 最大的重试次数
     */
    int maxRetryTime = 3;

    /**
     * 加载下一个号段阈值(当剩余空闲的号段<(步长*阈值))
     */
    double loadingNewThreshold = 0.9;

    /**
     * 号段递增倍数
     */
    int stepIncrementMultiple = 2;

    /**
     * 时间范围倍数（号段维持时间范围的倍数，如果超过这个时间（SEGMENT_DURATION*TIME_RANGE_MULTIPLE）号段递增）
     */
    int timeRangeMultiple = 2;

    /**
     * 单个Segment维持时间（默认15分钟，单位毫秒）
     */
    long segmentDuration = 15 * 60 * 1000L;

    /**
     * 等待号段加载超时时间（-1等于不超时）
     */
    long waitTimeout = -1;

    /**
     * 线程池配置
     */
    @NestedConfigurationProperty
    ExecutorProperties executor = new ExecutorProperties();

}
