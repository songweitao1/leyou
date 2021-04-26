package com.leyou.common.vo;

import com.leyou.common.emuns.ExceptionEnum;
import lombok.Data;

@Data

public class ExceptionRequest {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionRequest(ExceptionEnum exceptionEnum) {
        this.status = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
        this.timestamp = System.currentTimeMillis();
    }
}
