package io.hpb.contract.exception;

public enum ResponseMessage implements IMessage {
	/**请求成功*/
	SUCCESS("200","success","请求成功"),
	/**请求失败*/
	FAIL("0","fail","请求失败或者系统繁忙，请稍后再试"),
    /**参数为空*/
    EMPTY_PARAME("41010","fail","缺少参数,参数为空"),
    /**参数错误*/
    ERROR_PARAME("40035","fail","参数错误,不合法的参数"),
	/**不合法的access_token参数*/
    SESSION_NOT_EXISTS("40040","fail","不合法的access_token参数"), 
    /**用户名或密码为空*/
    EMPTY_NAME_PASSWORD("40400","fail","用户名或密码为空"),
    /**用户未登录*/
    UN_LOGON("40000","fail","用户未登录或登录失败"),
	/**用户名或密码为空*/
    ERROR_NAME_PASSWORD("40404","fail","用户名或密码错误");
    
    private String code;
    
    private String status;
    
    private String message;
    
    private ResponseMessage(String code, String status,String message) {
        this.code = code;
        this.status = status;
        this.message = message;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}