package io.hpb.contract.entity;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseEntity<K, V> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5160771530467230735L;
	private Map<K, V> map;
	
	public Map<K, V> getMap() {
		
		if(this.map==null) {
			this.map=new ConcurrentHashMap<K, V>();
		}
		
		return map;
	}

	public void setMap(Map<K, V> map) {
		this.map = map;
	}
	
	public V put(K k,V v) {
		return getMap().put(k, v);
	}
	
	public V get(K k) {
		return getMap().get(k);
	}
	
}
