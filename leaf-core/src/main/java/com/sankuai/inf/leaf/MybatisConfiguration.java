package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.mapper.IDAllocMapper;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis自动配置类
 * @author jiangxinjun
 * @date 2019/09/03
 */
@ConditionalOnProperty(value = "leaf.segment.enable", havingValue = "true")
@MapperScan(basePackages = "com.sankuai.inf.leaf.segment.dao.mapper")
@Configuration
public class MybatisConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Bean
    public IDAllocDao idAllocDao() {
        return new IDAllocDaoImpl();
    }

}
