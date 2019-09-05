package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Service("SnowflakeService")
public class SnowflakeService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Resource(name = Constants.LEAF_SNOWFLAKE_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    @SuppressWarnings("WeakerAccess")
    public Result getId(String key) {
        return idGen.get(key);
    }
}
