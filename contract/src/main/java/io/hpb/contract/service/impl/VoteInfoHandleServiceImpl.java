package io.hpb.contract.service.impl;

import com.github.pagehelper.PageHelper;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.entity.HpbVoteInfo;
import io.hpb.contract.example.HpbVoteInfoExample;
import io.hpb.contract.example.HpbVoteInfoExample.Criteria;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.exception.ResponseMessage;
import io.hpb.contract.mapper.HpbVoteInfoMapper;
import io.hpb.contract.service.VoteInfoHandleService;
import io.hpb.contract.utils.PageInfo;
import io.hpb.contract.utils.UUIDGeneratorUtil;
import org.apache.commons.collections.MapUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("voteInfoHandle")
public class VoteInfoHandleServiceImpl extends BaseServiceImpl implements VoteInfoHandleService {
	private static final String QUERY = "queryInfo";
	private static final String DEL = "delVoteInfo";
	private static final String UPDATE = "updateVoteInfo";
	private static final String ADD = "addVoteInfo";
	@Autowired
	private HpbVoteInfoMapper hpbVoteInfoMapper;
	
	private static final Logger log = LoggerFactory.getLogger(VoteInfoHandleServiceImpl.class);
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String serviceType=MapUtils.getString(param,BcConstant.SERVICE_TYPE);
		if(QUERY.equalsIgnoreCase(serviceType)){
			return queryVoteInfo(param);
		}else if(DEL.equalsIgnoreCase(serviceType)){
			return delVoteInfo(param);
		}else if(UPDATE.equalsIgnoreCase(serviceType)){
			return updateVoteInfo(param);
		}else if (ADD.equalsIgnoreCase(serviceType)){
			return addVoteInfo(param);
		}else{
			log.error("ErrorCode:{},ErrorMessage:{}",ResponseMessage.ERROR_PARAME.getCode(), ResponseMessage.ERROR_PARAME.getMessage());
			throw new BusinessException(ResponseMessage.ERROR_PARAME.getCode(), ResponseMessage.ERROR_PARAME.getMessage());
		}
	}
	private Map<String, Object> addVoteInfo(Map<String, Object> param) throws Exception{
		Map map = MapUtils.getMap(param, "param");
		String coinbase = MapUtils.getString(map, "coinbase");
		String name = MapUtils.getString(map, "name");
		String address = MapUtils.getString(map, "address");
//		String count = MapUtils.getString(map, "count");
		Long operatorId = MapUtils.getLong(map, "operatorId");
		Integer state = MapUtils.getInteger(map, "state");
		Integer cate = MapUtils.getInteger(map, "cate");
		String description = MapUtils.getString(map, "description");
		String linkUrl = MapUtils.getString(map, "linkUrl");
		HpbVoteInfo hpbVoteInfo = new HpbVoteInfo();
		hpbVoteInfo.setId(UUIDGeneratorUtil.generate(hpbVoteInfo));
		hpbVoteInfo.setCoinbase(coinbase);
		hpbVoteInfo.setName(name);
		hpbVoteInfo.setAddress(address);
//		hpbVoteInfo.setCount(new BigDecimal(count));
		hpbVoteInfo.setOperatorId(operatorId);
		hpbVoteInfo.setState(state);
		hpbVoteInfo.setCate(cate);
		hpbVoteInfo.setDescription(description);
		hpbVoteInfo.setLinkUrl(linkUrl);
		hpbVoteInfo.setCreateTime(new Date().getTime());
		hpbVoteInfo.setUpdateTime(new Date().getTime());
		hpbVoteInfoMapper.insertSelective(hpbVoteInfo);
		return null;
	}
	private Map<String, Object> updateVoteInfo(Map<String, Object> param) throws Exception{
		Map map = MapUtils.getMap(param, "param");
		String id = MapUtils.getString(map, "id");
		String coinbase = MapUtils.getString(map, "coinbase");
		String name = MapUtils.getString(map, "name");
		String address = MapUtils.getString(map, "address");
//		BigDecimal count = MapUtils.get
		Long operatorId = MapUtils.getLong(map, "operatorId");
		Integer state = MapUtils.getInteger(map, "state");
		Integer cate = MapUtils.getInteger(map, "cate");
		String description = MapUtils.getString(map, "description");
		String linkUrl = MapUtils.getString(map, "linkUrl");
		HpbVoteInfo hpbVoteInfo = new HpbVoteInfo();
		hpbVoteInfo.setId(id);
		hpbVoteInfo.setCoinbase(coinbase);
		hpbVoteInfo.setName(name);
		hpbVoteInfo.setAddress(address);
//		hpbVoteInfo.setCount(count);
		hpbVoteInfo.setOperatorId(operatorId);
		hpbVoteInfo.setState(state);
		hpbVoteInfo.setCate(cate);
		hpbVoteInfo.setDescription(description);
		hpbVoteInfo.setLinkUrl(linkUrl);
		HpbVoteInfo voteInfo = hpbVoteInfoMapper.selectByPrimaryKey(id);
		if(ObjectUtils.isEmpty(voteInfo)){
			param.clear();
			param.put(BcConstant.RETURN_CODE, BcConstant.ERROR_CODE);
			param.put(BcConstant.RETURN_MSG, "没有该条记录");
			return param;
		}else{
			hpbVoteInfoMapper.updateByPrimaryKeySelective(hpbVoteInfo);
		}
		return null;
	}
	private Map<String, Object> delVoteInfo(Map<String, Object> param) throws Exception{
		Map map = MapUtils.getMap(param, "param");
		String id = MapUtils.getString(map, "id");
		hpbVoteInfoMapper.deleteByPrimaryKey(id);
		return null;
	}
	private Map<String, Object> queryVoteInfo(Map<String, Object> param) throws Exception{
		Integer pageNum = MapUtils.getInteger(param, "page");
		Integer pageSize = MapUtils.getInteger(param, "rows");
		if (pageNum == null)
			pageNum = 1;
		if (pageSize == null)
			pageSize = 10;
		Map map = MapUtils.getMap(param, "param");
		String name = MapUtils.getString(map, "forName");
		String coinbase = MapUtils.getString(map, "forCoinbase");
		HpbVoteInfoExample hpbVoteInfoExample = new HpbVoteInfoExample();
		Criteria criteria = hpbVoteInfoExample.createCriteria();
		if(!StringUtils.isBlank(name)){
			criteria.andNameLike("%"+name+"%");
		}
		if(!StringUtils.isBlank(coinbase)){
			criteria.andCoinbaseLike("%"+coinbase+"%");
		}
		PageHelper.startPage(pageNum,pageSize);
		List<HpbVoteInfo> list = hpbVoteInfoMapper.selectByExampleWithBLOBs(hpbVoteInfoExample);
		list.stream().forEach(hpbVoteInfo -> {
			Long createTime = hpbVoteInfo.getCreateTime();
			BigDecimal count = hpbVoteInfo.getCount();
			hpbVoteInfo.setCount(count.divide(new BigDecimal("1000000000000000000")));
			hpbVoteInfo.setCreateAtTime(new Date(createTime));
		});
		PageInfo pageInfo = new PageInfo(list);
		param.clear();
		param.put(BcConstant.TOTAL_SIZE,pageInfo.getTotal());
		param.put(BcConstant.PAGENUM,pageInfo.getPageNum());
		param.put(BcConstant.PAGESIZE,pageInfo.getPageSize());
		param.put("rows",list);
		return param;
	}
}
