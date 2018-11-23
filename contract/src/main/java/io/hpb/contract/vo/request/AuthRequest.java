package io.hpb.contract.vo.request;

import io.hpb.contract.vo.IBaseVo;

public class AuthRequest<T> implements IBaseVo {

	private static final long serialVersionUID = -9038964904551739972L;
	
    private String serviceCode;
    private String serviceType;
    private String encodeKey;
    private String returnUrl;
    private String accessToken;
    private Boolean encode;
    private T param;
    
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
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public Boolean getEncode() {
		return encode;
	}
	public void setEncode(Boolean encode) {
		this.encode = encode;
	}
	public T getParam() {
		return param;
	}
	public void setParam(T param) {
		this.param = param;
	}
	@Override
	public String toString() {
		return "AuthRequest [serviceCode=" + serviceCode + ", serviceType=" + serviceType + ", encodeKey=" + encodeKey
				+ ", returnUrl=" + returnUrl + ", accessToken=" + accessToken + ", encode=" + encode + ", param="
				+ param + "]";
	}
    
}
