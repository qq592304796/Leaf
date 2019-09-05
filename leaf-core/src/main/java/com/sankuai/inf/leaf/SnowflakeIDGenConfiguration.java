package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.ZeroIDGen;
import com.sankuai.inf.leaf.exception.InitException;
import com.sankuai.inf.leaf.segment.dao.mapper.IDAllocMapper;
import com.sankuai.inf.leaf.snowflake.SnowflakeIDGenImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

/**
 * SegmentIDGen自动配置类
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Configuration
public class SnowflakeIDGenConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SnowflakeIDGenConfiguration.class);

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    @Bean(name = Constants.LEAF_SNOWFLAKE_ID_GEN_IMPL_NAME)
    public IDGen snowflakeIDGenImpl(Environment environment) throws InitException {
        Binder binder = Binder.get(environment);
        BindResult<Properties> bindResult = binder.bind("", Properties.class);
        Properties properties = bindResult.get();
        boolean flag = Boolean.parseBoolean(properties.getProperty(Constants.LEAF_SNOWFLAKE_ENABLE, "true"));
        IDGen idGen;
        if (flag) {
            String zkAddress = properties.getProperty(Constants.LEAF_SNOWFLAKE_ZK_ADDRESS);
            String name = properties.getProperty(Constants.LEAF_NAME);
            int port = Integer.parseInt(properties.getProperty(Constants.LEAF_SNOWFLAKE_PORT));
            idGen = new SnowflakeIDGenImpl(zkAddress, port, name);
            if (idGen.init()) {
                logger.info("Snowflake Service Init Successfully");
            } else {
                throw new InitException("Snowflake Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
        return idGen;
    }

}
