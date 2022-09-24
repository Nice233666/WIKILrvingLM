package com.lrvinglm.miki.exception;

public class BusinessException extends RuntimeException{

    private BusinessExceptionCode code;

    public BusinessException (BusinessExceptionCode code) {
        super(code.getDesc());
        this.code = code;
    }

    public BusinessExceptionCode getCode() {
        return code;
    }

    public void setCode(BusinessExceptionCode code) {
        this.code = code;
    }

    /**
     * 不写入堆栈信息，提高性能
     *  即每次报错提示的 谁调用谁 的信息
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
