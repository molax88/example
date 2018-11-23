package io.hpb.contract.entity.result;

public enum ResultCode {
	/** 成功 */
	SUCCESS("000000", "成功"),
	/** 失败 */
	FAIL("999999", "失败"),
	
	/** 网络异常，请稍后重试 */
	WARN("-1", "网络异常，请稍后重试"),

	/** 没有登录 */
	NOT_LOGIN("400", "没有登录"),

	/** 发生异常 */
	EXCEPTION("401", "发生异常"),
	
	/** 发生异常 */
	ERROR_EXCEPTION("40101", "获取区块链信息异常"),

	/** 系统错误 */
	SYS_ERROR("402", "系统错误"),

	/** 参数错误 */
	PARAMS_ERROR("403", "参数错误 "),
	
	/** 响应结果为空 */
	RESPONSE_EMPTY("404", "响应结果为空"),
	
	/** 响应结果为空 */
	RESPONSE_NULL("404", "响应结果为空"),

	/** 不支持或已经废弃 */
	NOT_SUPPORTED("410", "不支持或已经废弃"),

	/** AuthCode错误 */
	INVALID_AUTHCODE("444", "无效的AuthCode"),

	/** 太频繁的调用 */
	TOO_FREQUENT("445", "太频繁的调用"),

	/** 未知的错误 */
	UNKNOWN_ERROR("499", "未知错误"),
	/** 记录已经存在  add by huangshuidan 2018-08-03 */
	RECORD_EXIST("10102", "记录已经存在"),
	
	/** 参数错误 */
	PARAMETER_ERROR("10101", "参数错误");
	
	

	private ResultCode(String value, String msg) {
		this.val = value;
		this.msg = msg;
	}

	public String val() {
		return val;
	}

	public String msg() {
		return msg;
	}

	private String val;
	private String msg;
}
