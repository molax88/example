package io.hpb.contract.entity;

public class CacheParam extends BaseEntity<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4554142304731925587L;
	private String proccessId;

	public String getProccessId() {
		return proccessId;
	}

	public void setProccessId(String proccessId) {
		this.proccessId = proccessId;
	}
	
}
