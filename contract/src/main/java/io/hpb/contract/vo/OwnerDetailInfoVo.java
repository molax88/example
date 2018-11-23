package io.hpb.contract.vo;

import io.hpb.contract.entity.BaseEntity;

public class OwnerDetailInfoVo extends BaseEntity<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1833771933856233426L;
	/**
	 * 用户名
	 */
	private String id;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 用户id
	 */
	private String ownerId;
	/**
	 * 账户id 区块链id
	 */
	private String accountId;
	/**
	 * 创建时间
	 */
	private String createAtTime;
	/**
	 * 简称
	 */
	private String shortName;
	/**
	 * 全称
	 */
	private String fullName;
	/**
	 * 用户类型
	 */
	private String type;
	/**
	 * 用户角色
	 */
	private String roleName;
	/**
	 * 角色状态
	 */
	private String roleState;
	/**
	 * 角色id
	 */
	private String roleId;
	/**
	 * 用户邮箱
	 */
	private String email;
	/**
	 * 用户邮箱
	 */
	private String mobile;
	/**
	 * 用户状态
	 */
	private String status;
	/**
	 * 是否主账户
	 */
	private String isMain;
	
	/**
	 * 公钥
	 */
	private String publicKey;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getCreateAtTime() {
		return createAtTime;
	}
	public void setCreateAtTime(String createAtTime) {
		this.createAtTime = createAtTime;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleState() {
		return roleState;
	}
	public void setRoleState(String roleState) {
		this.roleState = roleState;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsMain() {
		return isMain;
	}
	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	@Override
	public String toString() {
		return "OwnerDetailInfoVo [id=" + id + ", username=" + username + ", ownerId=" + ownerId + ", accountId="
				+ accountId + ", createAtTime=" + createAtTime + ", shortName=" + shortName + ", fullName=" + fullName
				+ ", type=" + type + ", roleName=" + roleName + ", roleState=" + roleState + ", roleId=" + roleId
				+ ", email=" + email + ", mobile=" + mobile + ", status=" + status + ", isMain=" + isMain
				+ ", publicKey=" + publicKey + "]";
	}
}
