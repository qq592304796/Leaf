package com.sankuai.inf.leaf.exception;

import java.io.IOException;

/**
 * 网络主机异常.
 * @author jiangxinjun
 * @date 2019/11/18
 */
public final class HostException extends RuntimeException {

    private static final long serialVersionUID = 3589264847881174997L;

    public HostException(final IOException cause) {
        super(cause);
    }
}
