package io.hpb.contract.service.impl;

import com.github.pagehelper.PageHelper;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthResource;
import io.hpb.contract.entity.AuthRoleResource;
import io.hpb.contract.example.AuthRoleResourceExample;
import io.hpb.contract.mapper.AuthResourceMapper;
import io.hpb.contract.mapper.AuthRoleResourceMapper;
import io.hpb.contract.service.AuthResourceService;
import io.hpb.contract.utils.AppObjectUtil;
import io.hpb.contract.utils.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("authResource")
public class AuthResourceServiceImpl extends BaseServiceImpl implements AuthResourceService {
	private static final Logger log = LoggerFactory.getLogger(AuthResourceServiceImpl.class);
	
	@Autowired
	private AuthResourceMapper authResourceMapper;
	@Autowired
	private AuthRoleResourceMapper authRoleResourceMapper;
	
//	@Autowired
//	private HpbCmsCategoryService hpbCmsCategoryService;
	
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String serviceType=MapUtils.getString(param, BcConstant.SERVICE_TYPE);
		
		@SuppressWarnings("unchecked")
		Map<String,Object> map = MapUtils.getMap(param, "param");
		if("create".equals(serviceType)) {
			log.info("创建菜单{}",AppObjectUtil.toJson(map));
			return create(map);
		}else if("update".equals(serviceType)) {
			log.info("修改菜单{}",AppObjectUtil.toJson(map));
			return update(map);
		}else if("query".equals(serviceType)) {
			String pageNum = MapUtils.getString(param, "page");
			String pageSize = MapUtils.getString(param, "rows");
			map.put("pageNum", pageNum);
			map.put("pageSize", pageSize);
			log.info("查询菜单{}",AppObjectUtil.toJson(map));
			return query(map);
		}else if("queryActive".equals(serviceType)) {
			log.info("查询菜单{}",AppObjectUtil.toJson(map));
			return queryActive(map);
		}else if("getAllResource".equals(serviceType)) {
			log.info("查询菜单{}",AppObjectUtil.toJson(map));
			return getAllResource(param);
		}else if("getAllCateTree".equals(serviceType)) {//加载栏目树  add by huangshuidan 2018-08-03
			log.info("加载栏目树{}",AppObjectUtil.toJson(map));
//			return hpbCmsCategoryService.treeAllCategory(param);
		}else if("queryByRoleId".equals(serviceType)) {
			log.info("查询菜单{}ByRoleId",AppObjectUtil.toJson(map));
			return queryByRoleId(map);
		}
		log.warn("异常请求{}",AppObjectUtil.toJson(param));
		param.clear();
		return param;
	}

	private Map<String, Object> queryByRoleId(Map<String, Object> map) {
		Integer roleId = MapUtils.getInteger(map, "roleId");
		AuthRoleResourceExample example = new AuthRoleResourceExample();
		example.createCriteria().andRoleIdEqualTo(roleId);
		List<AuthRoleResource> selectByExample = authRoleResourceMapper.selectByExample(example);
		map.clear();
		map.put("rows", selectByExample);
		return map;
	}

	private Map<String, Object> queryActive(Map<String, Object> map) {
		map.put("status", 1);
		return query(map);
	}

	/**
	 * 创建菜单
	 * @param map
	 * @return
	 */
	private Map<String, Object> create(Map<String, Object> map) {
		if(MapUtils.getString(map,"pid")!=null){
			map.put("state","");
		}
		int result = authResourceMapper.insertByCondition(map );
		map.clear();
		map.put("result", result);
		return map;
	}
	/**
	 * 修改菜单
	 * @param map
	 * @return
	 */
	private Map<String, Object> update(Map<String, Object> map) {
		int result = authResourceMapper.updateByCondition(map );
		map.clear();
		map.put("result", result);
		return map;
	}

	/**
	 * 查询菜单
	 * @param map
	 * @return
	 */
	public Map<String, Object> query(Map<String, Object> map) {
		int pageSize = MapUtils.getIntValue(map, "pageSize", 10);
		int pageNum =   MapUtils.getIntValue(map, "pageNum", 1);
//		map.put("pageNum", calPageNum(pageNum, pageSize));
		PageHelper.startPage(pageNum,pageSize);
		List<AuthResource> rows = authResourceMapper.selectByPage(map);
		PageInfo pageInfo = new PageInfo(rows);
//		Long totalSize = authResourceMapper.count(map);
		Long totalSize = pageInfo.getTotal();
		if(CollectionUtils.isEmpty(rows)) {
			return map;
		}
		map.put("rows", rows);
		map.put("totalSize", totalSize);
		map.put("pageSize", pageSize);
		map.put("pageNum", pageNum);
		return map;
	}
	
	private int calPageNum(int pageNum,int pageSize) {
		if(pageNum<=0) {
			return pageNum;
		}
		return (pageNum - 1) * pageSize;
	}
	/**
	 * 获取所有菜单资源
	 * @param map
	 * @return
	 */
	private Map<String, Object> getAllResource(Map<String, Object> map) {
		List<AuthResource> resourceList = authResourceMapper.selectAllResource();
		if(resourceList==null || resourceList.isEmpty()) {
			return null;
		}
		for (AuthResource authResource : resourceList) {
			Integer pid = authResource.getPid();
			if(pid!=null) {
				for (AuthResource ar : resourceList) {
					Integer id = ar.getId();
					if(pid.equals(id)) {
						List<AuthResource> children = ar.getChildren();
						children.add(authResource);
						ar.setChildren(children);
					}
				}
			}
		}
		Iterator<AuthResource> iterator = resourceList.iterator();
		while(iterator.hasNext()) {
			AuthResource next = iterator.next();
			if(next.getChildren().isEmpty()) {
				iterator.remove();
			}
		}
		map.clear();
		map.put("rows", resourceList);
		return map;
	}
}
