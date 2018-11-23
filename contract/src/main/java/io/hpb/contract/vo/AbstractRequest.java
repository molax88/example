package io.hpb.contract.vo;

import java.util.Collections;
import java.util.List;

public abstract class AbstractRequest implements IBaseVo {

	private static final long serialVersionUID = -2630193057632203583L;
	
	private String serviceCode;
	
	private String serviceType;
	
	private String encodeKey;
	
	private String accessToken;
	
	private String returnUrl;
	
	private String param;
	
	public void initParam(List<String> param) {
		List<String> synchronizedList = Collections.synchronizedList(param);
		if(!synchronizedList.isEmpty() && synchronizedList.size() == 6) {
			this.setServiceCode(synchronizedList.get(0));
			this.setServiceType(synchronizedList.get(1));
			this.setEncodeKey(synchronizedList.get(2));
			this.setAccessToken(synchronizedList.get(3));
			this.setReturnUrl(synchronizedList.get(4));
			this.setParam(synchronizedList.get(5));
		}
	}
	
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getEncodeKey() {
		return encodeKey;
	}
	public void setEncodeKey(String encodeKey) {
		this.encodeKey = encodeKey;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}
