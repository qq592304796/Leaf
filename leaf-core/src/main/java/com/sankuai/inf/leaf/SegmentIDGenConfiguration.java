package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.ZeroIDGen;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Properties;

/**
 * SegmentIDGen自动配置类
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Import(MybatisConfiguration.class)
@Configuration
public class SegmentIDGenConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SegmentIDGenConfiguration.class);

    @Bean(name = Constants.LEAF_SEGMENT_ID_GEN_IMPL_NAME)
    public IDGen segmentIdGenImpl(Environment environment) {
        Binder binder = Binder.get(environment);
        BindResult<Properties> bindResult = binder.bind("", Properties.class);
        Properties properties = bindResult.get();
        boolean flag = Boolean.parseBoolean(properties.getProperty(Constants.LEAF_SEGMENT_ENABLE, "true"));
        IDGen idGen;
        if (flag) {
            // Config ID Gen‘
            idGen = new SegmentIDGenImpl();
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
        return idGen;
    }


}
