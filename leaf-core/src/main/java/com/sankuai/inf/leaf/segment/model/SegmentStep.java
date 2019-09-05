package com.sankuai.inf.leaf.segment.model;

/**
 * @author jiangxinjun
 * @date 2019/07/04
 */
public class SegmentStep {

    /**
     * 最大ID
     */
    private Long maxId;

    /**
     * 最小步长
     */
    private Integer step;

    /**
     * 实际步长
     */
    private Integer actualStep;

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

    public Integer getActualStep() {
        return actualStep;
    }

    public void setActualStep(Integer actualStep) {
        this.actualStep = actualStep;
    }
}
