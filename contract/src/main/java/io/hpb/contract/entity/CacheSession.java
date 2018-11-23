package io.hpb.contract.entity;

import com.hpb.web3.protocol.admin.Admin;
import io.hpb.contract.common.SpringBootContext;
import io.hpb.contract.utils.UUIDGeneratorUtil;


public class CacheSession extends BaseEntity<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8757481237386779657L;
	private String processFromId;
	private String toAccountId;
	private String sessionId;
	private Admin admin;
	private AuthUser authUser;

	public String getProcessFromId() {
		return processFromId;
	}

	public void setProcessFromId(String processFromId) {
		this.processFromId = processFromId;
	}

	public String getToAccountId() {
		return toAccountId;
	}

	public void setToAccountId(String toAccountId) {
		this.toAccountId = toAccountId;
	}

	public AuthUser getAuthUser() {
		return authUser;
	}

	public void setAuthUser(AuthUser user) {
		this.authUser = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		if (sessionId == null) {
			this.sessionId = UUIDGeneratorUtil.generate(this);
		}
		this.sessionId = sessionId;
	}

	//一种得到本地windows下的以太坊连接，一种得到配置文件中绑定的admin对象
	public Admin getAdmin() {
//		Web3jService web3jService = new WindowsIpcService("\\\\\\\\.\\\\pipe\\\\geth.ipc");
//		admin=Admin.build(web3jService);
//		return admin;
		if (this.admin == null) {
			return SpringBootContext.getBean("admin", Admin.class);
		}
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
}