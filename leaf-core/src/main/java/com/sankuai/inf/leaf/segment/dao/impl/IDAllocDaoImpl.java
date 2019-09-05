package com.sankuai.inf.leaf.segment.dao.impl;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.mapper.IDAllocMapper;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiangxinjun
 * @date 2019/09/03
 */
public class IDAllocDaoImpl implements IDAllocDao {

    @Resource
    private IDAllocMapper idAllocMapper;

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        return idAllocMapper.getAllLeafAllocs();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        idAllocMapper.updateMaxId(tag);
        return idAllocMapper.getLeafAlloc(tag);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        idAllocMapper.updateMaxIdByCustomStep(leafAlloc);
        return idAllocMapper.getLeafAlloc(leafAlloc.getKey());
    }

    @Override
    public List<String> getAllTags() {
        return idAllocMapper.getAllTags();
    }
}
