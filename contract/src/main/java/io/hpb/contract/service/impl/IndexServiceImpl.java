package io.hpb.contract.service.impl;

import io.hpb.contract.common.BcConstant;
import io.hpb.contract.service.IndexService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("index")
public class IndexServiceImpl extends BaseServiceImpl implements IndexService {
	private static final Logger log = LoggerFactory.getLogger(IndexServiceImpl.class);
	@Override
	public Map<String, Object> poccess(Map<String, Object> param) throws Exception {
		String  serviceType = MapUtils.getString(param, BcConstant.SERVICE_TYPE);
		param.clear();
		if("init".equals(serviceType)) {
			Calendar instance = Calendar.getInstance();
			instance.add(Calendar.MONTH, 0);
			instance.set(Calendar.DAY_OF_MONTH,1);
			instance.set(Calendar.HOUR_OF_DAY, 0);
			instance.set(Calendar.MINUTE, 0);
			instance.set(Calendar.SECOND, 0);
			instance.set(Calendar.MILLISECOND, 0);
			Map<String, Object> example = new HashMap<>();
			example.put("startTime", instance.getTime());
			List<Map<String,Object>> countByDay = new ArrayList<>();
			long tradeTotal = 0;
			param.put("tradeTotal", tradeTotal);
			Map<String, Object> trade = convert(countByDay);
			param.put("trade", trade);
			example.put("type", "0");
			long userTotal = 0L;
			param.put("userTotal", userTotal);
			Map<String, Object> users = new HashMap<>();
			param.put("users", users);
			example.put("type", "1");
			long organTotal = 1L;
			param.put("organTotal", organTotal);
			Map<String,Object> organ = new HashMap<>();
			param.put("organ", organ);
			return param;
		}
		return param; 
	}
	
	private Map<String,Object> convert(List<Map<String, Object>> countMap) {
		Map<String,Object> data = new TreeMap<>();
		if(countMap ==null || countMap.size()<=0) {
			return data;
		}
		Map<String,Object> param = new TreeMap<>();
		for (Map<String, Object> map : countMap) {
			param.put(MapUtils.getString(map, "days"), map.get("COUNT"));
		}
		List<Integer> dataList = new ArrayList<>();
		List<Integer> timeList = new ArrayList<>();
		for(int i=1;i<32;i++) {
			String index = String.format("%02d", i);
			String value = MapUtils.getString(param, index);
			if(StringUtils.isBlank(value)){
				dataList.add(0);
				timeList.add(Integer.valueOf(index));
			}else {
				dataList.add(Integer.valueOf(value));
				timeList.add(Integer.valueOf(index));
			}
		}
		data.put("data", dataList);
		data.put("timeType", timeList);
		return data;
	}

}
