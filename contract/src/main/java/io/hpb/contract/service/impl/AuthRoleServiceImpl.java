package io.hpb.contract.service.impl;

import com.github.pagehelper.PageHelper;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthRole;
import io.hpb.contract.entity.AuthRoleResource;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.example.AuthRoleResourceExample;
import io.hpb.contract.mapper.AuthRoleMapper;
import io.hpb.contract.mapper.AuthRoleResourceMapper;
import io.hpb.contract.service.AuthRoleService;
import io.hpb.contract.utils.AppObjectUtil;
import io.hpb.contract.utils.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("authRole")
public class AuthRoleServiceImpl extends BaseServiceImpl implements AuthRoleService {
	private static final Logger log = LoggerFactory.getLogger(AuthRoleServiceImpl.class);
	
	@Autowired
	private AuthRoleMapper authRoleMapper;
	@Autowired
	private AuthRoleResourceMapper authRoleResourceMapper;
	
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String serviceType=MapUtils.getString(param, BcConstant.SERVICE_TYPE);
		
		@SuppressWarnings("unchecked")
		Map<String,Object> map = MapUtils.getMap(param, "param");
		log.info("创建角色{}",AppObjectUtil.toJson(map));
		String sessionId=MapUtils.getString(param, BcConstant.ACCESS_TOKEN);
		CacheSession session = cacheSessionConfiguration.findBySessionId(sessionId);
		int userId = session.getAuthUser().getUserId();
		map.put("userId",userId);
		if("create".equals(serviceType)) {
			return create(map);
		}else if("update".equals(serviceType)) {
			log.info("修改角色{}",AppObjectUtil.toJson(map));
			return update(map);
		}else if("query".equals(serviceType)) {
			log.info("查询角色{}",AppObjectUtil.toJson(map));
			return query(map);
		}else if("queryActive".equals(serviceType)) {
			log.info("查询角色正常{}",AppObjectUtil.toJson(map));
			return queryActive(map);
		}
		log.warn("异常请求{}",AppObjectUtil.toJson(param));
		param.clear();
		return param;
	}
	private Map<String, Object> queryActive(Map<String, Object> map) {
		map.put("roleState", 1);
		return query(map);
	}

	/**
	 * 创建菜单
	 * @return
	 */
	private Map<String, Object> create(Map<String, Object> map) {
		AuthRole authRole = new AuthRole();
		authRole.setRoleDesc(MapUtils.getString(map, "roleDesc"));
		authRole.setRoleName(MapUtils.getString(map, "roleName"));
		authRole.setRoleState(MapUtils.getInteger(map, "roleState"));
		int row = authRoleMapper.save(authRole);
		if(row<=0) {
			return null;
		}
		Integer roleId = authRole.getId();
		ArrayList<Integer> resource =  (ArrayList<Integer>) MapUtils.getObject(map, "resource");
	    if(!CollectionUtils.isEmpty(resource)) {
	    	for (Integer recourceId : resource) {
	    		AuthRoleResource record = new AuthRoleResource();
	    		record.setRoleId(roleId);
	    		record.setResourceId(recourceId);
	    		record.setCreateAtTime(new Date());
	    		record.setCreateUserId( MapUtils.getString(map, "userId"));
	    		authRoleResourceMapper.insert(record );
	    	}
	    }
		map.clear();
		map.put("result", "success");
		return map;
	}
	/**
	 * 修改菜单
	 * @return
	 */
	@Transactional
	public Map<String, Object> update(Map<String, Object> map) {
		Integer roleId = MapUtils.getInteger(map, "id");
		if(roleId==null) {
			return null;
		}
		AuthRoleResourceExample example = new AuthRoleResourceExample();
		example.createCriteria().andRoleIdEqualTo(roleId);
		authRoleResourceMapper.deleteByExample(example);
	
		ArrayList<Integer> resource =  (ArrayList<Integer>) MapUtils.getObject(map, "resource");
	    if(!CollectionUtils.isEmpty(resource)) {
	    	for (Integer recourceId : resource) {
	    		AuthRoleResource record = new AuthRoleResource();
	    		record.setRoleId(roleId);
	    		record.setResourceId(recourceId);
	    		record.setCreateAtTime(new Date());
	    		record.setCreateUserId( MapUtils.getString(map, "userId"));
	    		authRoleResourceMapper.insert(record );
	    	}
	    }
		int result = authRoleMapper.updateByCondition(map );
		map.clear();
		map.put("result", result);
		return map;
	}

	/**
	 * 查询菜单
	 * @return
	 */
	private Map<String, Object> query(Map<String, Object> map) {
		int pageSize = MapUtils.getIntValue(map, "pageSize", 10);
		int pageNum = MapUtils.getIntValue(map, "pageNum", 1);
//		map.put("pageNum", calPageNum(pageNum, pageSize));
		PageHelper.startPage(pageNum,pageSize);
		List<AuthRole> rows = authRoleMapper.selectByPage(map);
//		Long totalSize = authRoleMapper.count(map);
		PageInfo pageInfo = new PageInfo(rows);
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
}
