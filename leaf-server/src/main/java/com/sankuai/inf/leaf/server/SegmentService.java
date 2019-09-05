package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.Constants;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import com.sankuai.inf.leaf.segment.model.SegmentStep;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jiangxinjun
 * @date 2019/09/03
 */
@Service("SegmentService")
public class SegmentService {

    @Resource(name = Constants.LEAF_SEGMENT_ID_GEN_IMPL_NAME)
    private IDGen idGen;

    @SuppressWarnings("WeakerAccess")
    public Result getId(String key) {
        return idGen.get(key);
    }

    @SuppressWarnings("WeakerAccess")
    public Result getIdWithStep(String key, int step) {
        return getIdGen().get(key, step);
    }

    @SuppressWarnings("WeakerAccess")
    public SegmentIDGenImpl getIdGen() {
        if (idGen instanceof SegmentIDGenImpl) {
            return (SegmentIDGenImpl) idGen;
        }
        return null;
    }

    public LeafAlloc getLeafAlloc() {
        return null;
    }
}
