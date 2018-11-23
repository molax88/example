package io.hpb.contract.exception;

public class BusinessException extends AbstractGenericException {
	private static final long serialVersionUID = 4466336188070448773L;

	public BusinessException(String errorCode) {
        super(errorCode);
    }

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    public BusinessException(String errorCode, String message, Throwable throwable) {
        super(errorCode, message, throwable);
    }

    public BusinessException(String errorCode, Object[] arguments) {
        super(errorCode, arguments);
    }
}
