package io.hpb.contract.entity.result;

import java.util.Arrays;
import java.util.List;


public class Result<T> {
	
	/* 错误码 */
	private String code;
	/* 错误消息 */
	private String msg;
	/* 具体内容 */
	private T data;
	
	public Result() {}
	
	public Result(ResultCode resultCode) {
		this.code = resultCode.val();
		this.msg = resultCode.msg();
	}
	
	public Result(ResultCode respCode, T data) {
		this(respCode);
		this.data = data;
	}
	
	public Result(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public Result(T data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Object> SUCCESS(){
		this.code = ResultCode.SUCCESS.val();
		this.msg = ResultCode.SUCCESS.msg();
		return result(this.code,this.msg,this.data);
	}
	
	public List<Object> FAIL(){
		this.code = ResultCode.FAIL.val();
		this.msg = ResultCode.FAIL.msg();
		return result(this.code,this.msg,null);
	}
	
	public List<Object> exception(Exception exception){
		this.code = ResultCode.FAIL.val();
		this.msg = exception.getMessage();
		return result(this.code,this.msg,null);
	}
	
	public List<Object> result(String code,String msg,T data){
		this.code = code;
		this.msg = msg;
		this.data = data;
		return Arrays.asList(this.code,this.msg,this.data);
	}
	
	public List<Object> result(){
		return result(this.code,this.msg,this.data);
	}
	
}
