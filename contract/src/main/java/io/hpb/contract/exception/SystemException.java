package io.hpb.contract.exception;

public class SystemException extends AbstractGenericException {

	private static final long serialVersionUID = -4820281743481175724L;

	public SystemException(String errorCode) {
        super(errorCode);
    }

    public SystemException(String errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(String errorCode, String message, Throwable throwable) {
        super(errorCode, message, throwable);
    }

    public SystemException(String errorCode, Object[] arguments) {
        super(errorCode, arguments);
    }
}
