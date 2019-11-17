package com.sankuai.inf.leaf.server.service;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.ZeroIDGen;
import com.sankuai.inf.leaf.server.exception.InitException;
import com.sankuai.inf.leaf.snowflake.SnowflakeIDGenImpl;
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

    @Resource(name = Constants.LEAF_SNOWFLAKE_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    Result getId(String key) {
        return idGen.get(key);
    }
}
