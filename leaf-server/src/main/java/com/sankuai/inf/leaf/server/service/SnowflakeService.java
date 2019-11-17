package com.sankuai.inf.leaf.server.service;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
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

    public Result getId(String key) {
        return idGen.get(key);
    }
}
