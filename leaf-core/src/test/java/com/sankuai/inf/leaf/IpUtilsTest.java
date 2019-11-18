package com.sankuai.inf.leaf;

import com.sankuai.inf.leaf.common.IpUtils;
import com.sankuai.inf.leaf.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author jiangxinjun
 * @date 2019/11/18
 */
@Slf4j
public class IpUtilsTest {

    @Test
    public void testGetIp() {
        log.info("IpUtils ==> ip:{}, host:{}", IpUtils.getIp(), IpUtils.getHostName());
        log.info("Utils ==> ip:{}", Utils.getIp());
    }

}
