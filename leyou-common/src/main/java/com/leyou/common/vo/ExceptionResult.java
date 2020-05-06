package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

@Data
public class ExceptionResult {

    private Integer status;
    private String msg;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum exceptionEnum){
        this.status = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
