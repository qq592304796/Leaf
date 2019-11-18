package com.sankuai.inf.leaf.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 *
 * @author jiangxinjun
 * @date 2019-05-12
 */
@RefreshScope
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@ToString(callSuper = true)
@ConfigurationProperties(prefix = "wjh.leaf")
public class LeafProperties {

    /**
     * 号段配置
     */
    @NestedConfigurationProperty
    SegmentProperties segment = new SegmentProperties();
}
