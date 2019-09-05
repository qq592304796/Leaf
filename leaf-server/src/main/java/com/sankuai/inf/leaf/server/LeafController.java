package com.sankuai.inf.leaf.server;

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

import java.util.ArrayList;
import java.util.List;

@RestController
public class LeafController {
    private Logger logger = LoggerFactory.getLogger(LeafController.class);
    @Autowired
    SegmentService segmentService;
    @Autowired
    SnowflakeService snowflakeService;

    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "/api/segment/get/{key}")
    public String getSegmentID(@PathVariable("key") String key) {
        return get(key, segmentService.getId(key));
    }

    @RequestMapping(value = "/api/snowflake/get/{key}")
    public String getSnowflakeID(@PathVariable("key") String key) {
        return get(key, snowflakeService.getId(key));

    }

    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }

        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }

    @RequestMapping(value = "/api/segment/batchGet/{key}")
    public List<String> batchGetSegmentID(@PathVariable("key") String key, @RequestParam Integer batchSize) {
        List<String> segmentIds = new ArrayList<>(batchSize);
        for (int i = 0; i < batchSize; i++) {
            segmentIds.add(get(key, segmentService.getId(key)));
        }
        return segmentIds;
    }

    @RequestMapping(value = "/api/segment/get")
    public SegmentStep getSegment(@RequestParam String key, @RequestParam Integer step) {
        Result result = segmentService.getIdWithStep(key, step);
        // 与项目保持一致，如果出现异常抛出
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return result.getSegment();
    }

    @RequestMapping(value = "/api/cache/get")
    public Long getCacheID(@RequestParam String key) {
        return cacheService.getId(key);
    }

    @RequestMapping(value = "/api/cache/getWithExpired")
    public Long getCacheIDWithExpired(@RequestParam String key) {
        return cacheService.getIdWithExpired(key);
    }
}
