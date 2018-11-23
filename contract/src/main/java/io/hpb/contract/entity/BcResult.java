package io.hpb.contract.entity;

import io.hpb.contract.common.BcConstant;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;


public class BcResult<T extends BaseEntity<K,V>, K,V>{
	public BcResult(Map<K,V> map) {
		BaseEntity<K,V> baseEntity=new BaseEntity<K,V>() {};
		//#todo:对传递的参数做一些操作
		baseEntity.setMap(map);
		this.result=baseEntity;
		this.code=MapUtils.getString(map, BcConstant.RETURN_CODE,BcConstant.SUCCESS_CODE);
		this.msg=MapUtils.getString(map, BcConstant.RETURN_MSG,BcConstant.SUCCESS_MSG);
		map.remove(BcConstant.RETURN_CODE);
		map.remove(BcConstant.RETURN_MSG);
	}
	public BcResult() {
	}
	private BaseEntity<K,V> result;
	private String code;
	private String msg;
	private List<T> listResult;
	public BaseEntity<K,V> getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<T> getListResult() {
		return listResult;
	}
	public void setListResult(List<T> listResult) {
		this.listResult = listResult;
	}
}
