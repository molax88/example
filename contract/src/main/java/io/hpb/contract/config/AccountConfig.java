package io.hpb.contract.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "web3.account")
public class AccountConfig {
	private String globalAccount;
	private String globalAccountPassword;
	private String initAccountValue;
	private String initAccountValueUnit;
	private String keyFilePath;
	private String publicKey;
	private String privateKey;
	/**
	 * 默认gas
	 */
	private String initGas;
	/**
	 * 默认gasLimit
	 */
	private String intGasLimit;
	public String getKeyFilePath() {
		return keyFilePath;
	}
	public void setKeyFilePath(String keyFilePath) {
		this.keyFilePath = keyFilePath;
	}
	public String getGlobalAccount() {
		return globalAccount;
	}
	public void setGlobalAccount(String globalAccount) {
		this.globalAccount = globalAccount;
	}
	public String getGlobalAccountPassword() {
		return globalAccountPassword;
	}
	public void setGlobalAccountPassword(String globalAccountPassword) {
		this.globalAccountPassword = globalAccountPassword;
	}
	public String getInitAccountValue() {
		return initAccountValue;
	}
	public void setInitAccountValue(String initAccountValue) {
		this.initAccountValue = initAccountValue;
	}
	public String getInitAccountValueUnit() {
		return initAccountValueUnit;
	}
	public void setInitAccountValueUnit(String initAccountValueUnit) {
		this.initAccountValueUnit = initAccountValueUnit;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getInitGas() {
		return initGas;
	}
	public void setInitGas(String initGas) {
		this.initGas = initGas;
	}
	public String getIntGasLimit() {
		return intGasLimit;
	}
	public void setIntGasLimit(String intGasLimit) {
		this.intGasLimit = intGasLimit;
	}
}
