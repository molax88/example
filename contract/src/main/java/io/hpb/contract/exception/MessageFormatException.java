package io.hpb.contract.exception;

public class MessageFormatException extends MessageException {

	private static final long serialVersionUID = -2770634731709311605L;

	public MessageFormatException(Exception e) {
		super(e);
	}

	public MessageFormatException(String msg) {
		super(msg);
	}

}
