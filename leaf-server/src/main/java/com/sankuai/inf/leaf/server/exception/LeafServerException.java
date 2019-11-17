package com.sankuai.inf.leaf.server.exception;

import com.sankuai.inf.leaf.common.Result;

/**
 * @author jiangxinjun
 * @date 2019/11/12
 */
public class LeafServerException extends RuntimeException {

    private Result result;

    public LeafServerException(Result result) {
        super(result.toString());
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
