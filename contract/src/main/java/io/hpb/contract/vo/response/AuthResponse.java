package io.hpb.contract.vo.response;

import io.hpb.contract.exception.IMessage;
import io.hpb.contract.vo.IBaseVo;


public class AuthResponse<T> implements IBaseVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5662742232042747341L;
	
	private String code;
	
	private String status;
	
	private String processId;
	
	private String encodeKey;
	
	private T msg;
	
	private String accessToken;
	
	public AuthResponse() {}
	
	public AuthResponse(IMessage message) {
		super();
		this.code = message.getCode();
		this.status = message.getStatus();
	}
	
	public AuthResponse(String code, String status, T msg) {
		super();
		this.code = code;
		this.status = status;
		this.msg = msg;
	}
	public AuthResponse(String code, String status, String processId,T msg) {
		super();
		this.code = code;
		this.status = status;
		this.msg = msg;
		this.processId = processId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getEncodeKey() {
		return encodeKey;
	}

	public void setEncodeKey(String encodeKey) {
		this.encodeKey = encodeKey;
	}

	public T getMsg() {
		return msg;
	}

	public void setMsg(T msg) {
		this.msg = msg;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String toString() {
		return "AuthResponse [code=" + code + ", status=" + status + ", processId=" + processId + ", encodeKey="
				+ encodeKey + ", msg=" + msg + ", accessToken=" + accessToken + "]";
	}
}
