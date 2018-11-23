package io.hpb.contract.vo.request;

import io.hpb.contract.vo.IBaseVo;

import java.util.Map;

public class AuthTradeVO implements IBaseVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4634240701523667010L;
	
	private String accountFrom;
	
	private String passwordFrom;
	
	private String accountTo;
	
	private Map<String,Object> input;
	
	private String value;
	
	public String getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}
	public String getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}
	public String getPasswordFrom() {
		return passwordFrom;
	}
	public void setPasswordFrom(String passwordFrom) {
		this.passwordFrom = passwordFrom;
	}
	public Map<String, Object> getInput() {
		return input;
	}
	public void setInput(Map<String, Object> input) {
		this.input = input;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	/*public String checkAccount(String account) {
		if(StringUtils.isBlank(account)) {
			return null;
		}
		account = account.trim();
		if(!Numeric.containsHexPrefix(account)) {
			account = "0x"+account;
		}
		if(account.length() != 42) {
			return null;
		}
		return account;
	}*/
}
