package com.sankuai.inf.leaf.server.model;

/**
 * @author jiangxinjun
 * @date 2019/07/04
 */
public class SegmentDTO {

    /**
     * 最大ID
     */
    private Long maxId;

    /**
     * 步长
     */
    private Integer step;

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
