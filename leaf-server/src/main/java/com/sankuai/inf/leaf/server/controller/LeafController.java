package com.sankuai.inf.leaf.server.controller;

import com.sankuai.inf.leaf.cache.CacheService;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.segment.model.SegmentStep;
import com.sankuai.inf.leaf.server.exception.LeafServerException;
import com.sankuai.inf.leaf.server.exception.NoKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangxinjun
 * @date 2019/11/12
 */
@RestController
public class LeafController {

    @Resource
    private SegmentService segmentService;

    @Resource
    private SnowflakeService snowflakeService;

    @Resource
    private CacheService cacheService;

    @RequestMapping(value = "/api/segment/get/{key}")
    public String getSegmentId(@PathVariable("key") String key) {
        return get(segmentService.getId(key));
    }

    @RequestMapping(value = "/api/snowflake/get/{key}")
    public String getSnowflakeId(@PathVariable("key") String key) {
        return get(snowflakeService.getId(key));
    }

    public String getSnowflakeId(@PathVariable("key") String key) {
        return get(snowflakeService.getId(key));

    }

    private String get(Result id) {
        if (id.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(id);
        }
        return String.valueOf(id.getId());
    }

    @RequestMapping(value = "/api/segment/batchGet/{key}")
    public List<String> batchGetSegmentId(@PathVariable("key") String key, @RequestParam Integer batchSize) {
        List<String> segmentIds = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            segmentIds.add(get(segmentService.getId(key)));
        }
        result = id;
        return segmentIds;
    }

    @RequestMapping(value = "/api/segment/get")
    public SegmentStep getSegment(@RequestParam String key, @RequestParam Integer step) {
        Result result = segmentService.getIdWithStep(key, step);
        // 与项目保持一致，如果出现异常抛出
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result);
        }
        return result.getSegment();
    }

    @RequestMapping(value = "/api/cache/get")
    public Long getCacheId(@RequestParam String key) {
        return cacheService.getId(key);
    }

    @RequestMapping(value = "/api/cache/getWithExpired")
    public Long getCacheIdWithExpired(@RequestParam String key) {
        return cacheService.getIdWithExpired(key);
    }
}
