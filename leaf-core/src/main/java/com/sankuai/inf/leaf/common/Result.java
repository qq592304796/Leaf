package com.sankuai.inf.leaf.common;

import com.sankuai.inf.leaf.segment.model.Segment;
import com.sankuai.inf.leaf.segment.model.SegmentStep;

/**
 * @author jiangxinjun
 * @date 2019/09/04
 */
public class Result {

    private long id;

    private SegmentStep segment;

    private Status status;

    public Result() {

    }
    public Result(long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public Result(SegmentStep segment, Status status) {
        this.segment = segment;
        this.id = segment.getMaxId() - segment.getActualStep();
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SegmentStep getSegment() {
        return segment;
    }

    public void setSegment(SegmentStep segment) {
        this.segment = segment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("id=").append(id);
        if (segment != null) {
            sb.append(", segment={");
            sb.append("maxId=").append(segment.getMaxId());
            sb.append(", step=").append(segment.getStep());
            sb.append('}');
        }
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
