package com.hpb.oversea.controller;

import com.hpb.oversea.utils.HttpUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
@EnableAutoConfiguration
public class CommonController{
	@RequestMapping(value = "/hpb/price", method = RequestMethod.GET)
	public Map<String,Object> hpbPrice(){
		return HttpUtil.getInstantPriceOfHPB();
	}
}
