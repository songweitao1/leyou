package com.leyou.common.exception;

import com.leyou.common.emuns.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class  LyException extends RuntimeException {
    private ExceptionEnum exceptionEnum;
}
