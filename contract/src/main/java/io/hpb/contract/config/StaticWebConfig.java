package io.hpb.contract.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "static.page")
public class StaticWebConfig {
	private String blockChainCenter;
	private String consumer;
	private String provider;
	public String getBlockChainCenter() {
		return blockChainCenter;
	}
	public void setBlockChainCenter(String blockChainCenter) {
		this.blockChainCenter = blockChainCenter;
	}
	public String getConsumer() {
		return consumer;
	}
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}

}
