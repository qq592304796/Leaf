package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.properties.LeafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * leaf自动配置类
 * @author jiangxinjun
 * @date 2019/09/03
 */
@EnableConfigurationProperties(LeafProperties.class)
@Import({SegmentIDGenConfiguration.class, SnowflakeIDGenConfiguration.class})
@Configuration
public class LeafAutoConfiguration {

}
