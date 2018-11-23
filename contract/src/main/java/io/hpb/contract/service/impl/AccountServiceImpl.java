package io.hpb.contract.service.impl;

import com.github.pagehelper.PageHelper;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.AuthUser;
import io.hpb.contract.entity.AuthUserRole;
import io.hpb.contract.entity.CacheSession;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.exception.ResponseMessage;
import io.hpb.contract.mapper.AuthUserMapper;
import io.hpb.contract.mapper.AuthUserRoleMapper;
import io.hpb.contract.service.AccountService;
import io.hpb.contract.utils.POIUtil;
import io.hpb.contract.utils.PageInfo;
import io.hpb.contract.utils.SecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Service("account")
public class AccountServiceImpl extends BaseServiceImpl implements AccountService {
	public static final int RETRY_TIMES =3;
	private static final String CREATE = "create";
	private static final String QUERY = "query";
	private static final String UPDATE = "update";
	private static final String DELETE = "delete";
	private static final String IMPORT = "import";
	private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
	@Autowired
    private AuthUserRoleMapper authUserRoleMapper;
	@Autowired
	private AuthUserMapper userMapper;
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String sessionId=MapUtils.getString(param, "accessToken");
		CacheSession session = cacheSessionConfiguration.findBySessionId(sessionId);
		String serviceType=MapUtils.getString(param,BcConstant.SERVICE_TYPE);
		param.putAll(MapUtils.getMap(param,"param",new HashMap()));
		if(CREATE.equalsIgnoreCase(serviceType)) {
			return createAccount(param,session,0);
		}else if(QUERY.equalsIgnoreCase(serviceType)) {
			return queryAccount(param,session);
		}else if(UPDATE.equalsIgnoreCase(serviceType)) {
			return updateAccount(param,session);
		}else if(DELETE.equalsIgnoreCase(serviceType)) {
			return deleteOrganAccount(param,session);
		}else if(IMPORT.equals(serviceType)){
		    return importOrganAccount(param);
        }else {
			log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.ERROR_PARAME.getCode(), ResponseMessage.ERROR_PARAME.getMessage());
			throw new BusinessException(ResponseMessage.ERROR_PARAME.getCode(), ResponseMessage.ERROR_PARAME.getMessage());
		}

	}

	/**
	 * 创建用户
	 * @param param
	 * @param session
	 * @return
	 */
	public Map<String, Object> createAccount(Map<String, Object> param, CacheSession session,int count) {
		if(count>RETRY_TIMES) {
			return null;
		}
		try {
			if(count>0) {
				log.info("第 {} 次尝试重新运行方法：{}",count,"createAccount(param,session,count)");
			}
			AuthUser user = userMapper.selectByUsername(MapUtils.getString(param,"userName"));
			if(user==null) {
				user = new AuthUser();
				copyProperties(user, param);
				user.setCreateUserid(session.getAuthUser().getUserId());
				user.setPasswd(SecurityUtils.encodeAESBySalt(user.getPasswd(),user.getUserName()));
				if(StringUtils.isEmpty(user.getState())){
					user.setState("0");
				}
				userMapper.insertSelective(user);
				AuthUserRole role = new AuthUserRole();
				role.setRoleId(MapUtils.getInteger(param,"roleId",2));
				role.setUserId(user.getUserId());
				role.setCreateUserId(session.getAuthUser().getUserId());
				role.setCreateAtTime(new Date());
				authUserRoleMapper.insertSelective(role);
				return param;
			}else{
				throw new BusinessException("用户名已存在");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			createAccount(param, session, ++count);
		}
		return null;
	}

	/**
	 * 创建用户
	 * @param param
	 * @param session
	 * @return
	 */
	public Map<String, Object> updateAccount(Map<String, Object> param, CacheSession session) {
		try {
			AuthUser user = userMapper.selectByUsername(MapUtils.getString(param,"userName"));
			if(user==null||(user.getUserId().intValue()==MapUtils.getInteger(param,"userId").intValue())) {
				user = new AuthUser();
				copyProperties(user, param);
				if(!StringUtils.isEmpty(user.getPasswd()))
					user.setPasswd(SecurityUtils.encodeAESBySalt(user.getPasswd(),user.getUserName()));
				if(StringUtils.isEmpty(user.getState())){
					user.setState("0");
				}
				userMapper.updateByPrimaryKeySelective(user);
				AuthUserRole role = authUserRoleMapper.selectByUserId(user.getUserId());
				role.setRoleId(MapUtils.getInteger(param,"roleId",2));
				role.setCreateAtTime(new Date());
				authUserRoleMapper.updateByPrimaryKeySelective(role);
				return param;
			}else{
				throw new BusinessException("用户名已存在");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
	public void updateSessonByUserId(CacheSession session, int userId) {
		AuthUser authUser = userMapper.selectByPrimaryKey(userId);
		session.setAuthUser(authUser);
		cacheSessionConfiguration.update(session);
	}
	
	
	
	private void removeIgnoreParam(Map<String, Object> result, CacheSession session) {
		if(result==null) {
			return;
		}
		Map<String, Object> origParams = new HashMap<String, Object>();
		origParams.putAll(session.getMap());
		@SuppressWarnings("unchecked")
		Map<String, Object> map = MapUtils.getMap(result, BcConstant.USER_INFO,new HashMap<String, Object>());
		Arrays.asList(BcConstant.PARAM_IGNORE).stream().forEach(param -> {
			map.remove(param);
			origParams.remove(param);
		});
		result.put(BcConstant.ORIG_PARAM,origParams);
	}

	private Map<String,Object> queryAccount(Map<String,Object> param, CacheSession session) {
		param.putAll(MapUtils.getMap(param,"param"));
		Integer pageNum=MapUtils.getInteger(param, "pageNum");
		Integer pageSize=MapUtils.getInteger(param, "pageSize");
		if(pageNum==null)pageNum=1;
		if(pageSize==null)pageSize=10;
		PageHelper.startPage(pageNum,pageSize);
		List<AuthUser> users= userMapper.selectByCondition(param);
		PageInfo pageInfo = new PageInfo(users);
		users.stream().forEach(user -> user.setPasswd(""));
		users.stream().forEach(user -> user.setRoleId(authUserRoleMapper.selectByUserId(user.getUserId()).getRoleId()));
		param.clear();
		param.put(BcConstant.TOTAL_SIZE,pageInfo.getTotal());
		param.put(BcConstant.PAGENUM,pageInfo.getPageNum());
		param.put(BcConstant.PAGESIZE,pageInfo.getPageSize());
		param.put("rows",users);
		return param;
	}


	private Map<String,Object> deleteOrganAccount(Map<String,Object> param, CacheSession session) {
		throw new BusinessException("4008","删除失败");
	}
	private Map<String,Object> importOrganAccount(Map<String,Object> param) {
		try {
			MultipartFile file = (MultipartFile) MapUtils.getObject(param, "file");
			if (null != file && !file.isEmpty()) {
				FileOutputStream fos = null;
				String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//				String path = "/data/ylzh/organ";
				String path = "/tmp/ylzh";
				File f = new File(path);
				if(!f.exists()){
					f.mkdirs();
				}
				suffix = "/" + Calendar.getInstance().getTimeInMillis() + suffix;
				path += suffix;
				try {
					fos = new FileOutputStream(path);
					fos.write(file.getBytes()); // 写入文件
				}catch (Exception e) {
						e.printStackTrace();
				}finally {
					fos.close();
				}
				System.out.print(file.getOriginalFilename());
				if(BcConstant.XLSX.equals(suffix)||BcConstant.XLS.equals(suffix)) {
					List<String[]> data = POIUtil.readExcel(file);
					data.forEach((String[] c) -> {
						for (String s : c) {
							System.out.print(s);
						}
						System.out.println();
					});
				}
			}
		}catch (Exception e){
			throw new BusinessException("导入出错");
		}finally {
			return null;
		}
	}

	private void copyProperties(AuthUser user,Map param){
		user.setUserId(MapUtils.getInteger(param,"userId"));
		user.setUserName(MapUtils.getString(param,"userName"));
		user.setRealName(MapUtils.getString(param,"realName"));
		user.setSexual(MapUtils.getString(param,"sexual"));
		user.setCellphone(MapUtils.getString(param,"cellphone"));
		user.setQq(MapUtils.getString(param,"qq"));
		user.setWechat(MapUtils.getString(param,"wechat"));
		user.setEmail(MapUtils.getString(param,"email"));
		user.setPasswd(MapUtils.getString(param,"passwd"));
		user.setIdCard(MapUtils.getString(param,"idCard"));
		user.setNickName(MapUtils.getString(param,"nickName"));
		user.setAddress(MapUtils.getString(param,"address"));
		user.setRemark(MapUtils.getString(param,"remark"));
	}
}