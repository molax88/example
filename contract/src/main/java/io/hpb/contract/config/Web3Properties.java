package io.hpb.contract.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * web3 property container.
 */
@ConfigurationProperties(prefix = Web3Properties.WEB3_PREFIX)
public class Web3Properties {

    public static final String WEB3_PREFIX = "web3";

    private String clientAddress;

    private Boolean adminClient;
    
    private String networkId;

    private Long httpTimeoutSeconds;

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public Boolean isAdminClient() {
        return adminClient;
    }

    public void setAdminClient(Boolean adminClient) {
        this.adminClient = adminClient;
    }
    
    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public Long getHttpTimeoutSeconds() {
        return httpTimeoutSeconds;
    }

    public void setHttpTimeoutSeconds(Long httpTimeoutSeconds) {
        this.httpTimeoutSeconds = httpTimeoutSeconds;
    }
    
}
