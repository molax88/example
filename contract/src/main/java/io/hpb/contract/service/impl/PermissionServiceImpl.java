package io.hpb.contract.service.impl;

import com.github.pagehelper.PageHelper;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthResource;
import io.hpb.contract.entity.AuthUser;
import io.hpb.contract.entity.AuthUserRole;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.example.AuthUserRoleExample;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.mapper.AuthResourceMapper;
import io.hpb.contract.mapper.AuthUserMapper;
import io.hpb.contract.mapper.AuthUserRoleMapper;
import io.hpb.contract.service.PermissionService;
import io.hpb.contract.utils.AppObjectUtil;
import io.hpb.contract.utils.JSONUtils;
import io.hpb.contract.utils.PageInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("permission")
public class PermissionServiceImpl extends BaseServiceImpl implements PermissionService {
	private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Autowired
	private AuthUserMapper userMapper;
	@Autowired
	private AuthResourceMapper authResourceMapper;
	@Autowired
	private AuthUserRoleMapper authUserRoleMapper;
	
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String serviceType = MapUtils.getString(param, BcConstant.SERVICE_TYPE);
		String processId = MapUtils.getString(param, "processId");
		log.info("processId:{},调用{},请求{}",processId,serviceType,JSONUtils.obj2json(param));
		@SuppressWarnings("unchecked")
		Map<String,Object> map = MapUtils.getMap(param, "param");
	    if("resource".equals(serviceType)) {
	    	String sessionId=MapUtils.getString(param, BcConstant.ACCESS_TOKEN);
	    	CacheSession session = cacheSessionConfiguration.findBySessionId(sessionId);
	    	int userId = session.getAuthUser().getUserId();
	    	List<AuthResource> resource = resource(userId);
	    	param.clear();
	    	param.put("msg", resource);
	    	return param;
	    }else if("queryUser".equals(serviceType)) {
	    	log.info(AppObjectUtil.toJson(param));
	    	return queryUser(map);
	    }else if("update".equals(serviceType)) {
	    	log.info(AppObjectUtil.toJson(param));
	    	return update(map);
	    }
		return param;
	}

	/**
	 * 修改用户信息
	 * @param map
	 * @return
	 */
	@Transactional
	public Map<String, Object> update( Map<String, Object> map) {
		Integer userId = MapUtils.getInteger(map, "userId");
		String username = MapUtils.getString(map, "username");
		String mobile = MapUtils.getString(map, "mobile");
		String email = MapUtils.getString(map, "email");
		String publicKey = MapUtils.getString(map, "publicKey");
		Integer roleId = MapUtils.getInteger(map, "roleId");
		if(roleId!=null){
			AuthUserRoleExample authUserRoleExample = new AuthUserRoleExample();
			authUserRoleExample.createCriteria().andUserIdEqualTo(userId);
			List<AuthUserRole> authUserRoles = authUserRoleMapper.selectByExample(authUserRoleExample);
			AuthUserRole authUserRole;
			if (authUserRoles != null) {
				authUserRole = authUserRoles.get(0);
				authUserRole.setRoleId(roleId);
				authUserRoleMapper.updateByPrimaryKeySelective(authUserRole);
			} else {
				throw new BusinessException("40000", "账户不合法");
			}
		}
		return map;
	}
	

	/**
	 * 查询用户信息
	 * @param map
	 * @return
	 */
	private Map<String, Object> queryUser( Map<String, Object> map) {
		int pageSize = MapUtils.getIntValue(map, "pageSize", 10);
		int pageNum = MapUtils.getIntValue(map, "pageNum", 1);
		PageHelper.startPage(pageNum,pageSize);
		List<AuthUser> rows = userMapper.selectByCondition(map);
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
	/**
	 * 获取菜单资源
	 * @param userId
	 * @return
	 */
	private List<AuthResource> resource(int userId) {
		List<AuthResource> resourceList = authResourceMapper.selectByUserId(userId);
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
		return resourceList;
	}
	private int calPageNum(int pageNum,int pageSize) {
		if(pageNum<=0) {
			return pageNum;
		}
		return (pageNum - 1) * pageSize;
	}

}
