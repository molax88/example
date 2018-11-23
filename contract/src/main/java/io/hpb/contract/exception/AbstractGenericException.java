package io.hpb.contract.exception;

import java.io.Serializable;

public abstract class AbstractGenericException  extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -6785244690920409384L;
    protected String errorCode;
    protected String message;
    private Object[] arguments;

    public AbstractGenericException(String errorCode) {
        this.errorCode = errorCode;
    }

    public AbstractGenericException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public AbstractGenericException(String errorCode, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
    }

    public AbstractGenericException(String errorCode, Object[] arguments) {
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
