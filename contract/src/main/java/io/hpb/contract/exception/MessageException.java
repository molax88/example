package io.hpb.contract.exception;

public class MessageException extends Exception {

	private static final long serialVersionUID = 7139217469867968052L;

	public MessageException(Exception e) {
		super(e);
	}

	public MessageException(String msg) {
		super(msg);
	}

}
