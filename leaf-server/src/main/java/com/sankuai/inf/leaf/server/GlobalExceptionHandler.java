package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.server.exception.LeafServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * mvc 统一错误处理
 *
 * @author yqj
 */
@RestController
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {LeafServerException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result leafServerException(LeafServerException e) {
        log.error("系统错误", e);
        return e.getResult();
    }

}
