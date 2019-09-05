package com.sankuai.inf.leaf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * leaf自动配置类
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Import({SegmentIDGenConfiguration.class, SnowflakeIDGenConfiguration.class})
@Configuration
public class LeafAutoConfiguration {

}
