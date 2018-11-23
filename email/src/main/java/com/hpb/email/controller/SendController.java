package com.hpb.email.controller;

import com.hpb.email.service.EmailService;
import com.hpb.email.utils.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@EnableAutoConfiguration
public class SendController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/send/{sheetNum}")
    public Map<String,Object> send(@PathVariable int sheetNum){
        Map<String,Object> map = new HashMap<String,Object>();
        List<List<String>> list = POIUtil.readExcel("D:/backup/weitui.xls",sheetNum);
        for(int i=1;i<list.size();i++){
            List<String> r = list.get(i);
            System.out.println(r.get(0));
            String sendTo = r.get(0).trim();
            String title = "HPB芯链 | 奖励确认函";
            String address = r.get(1);
            String total = r.get(2);
            String bounty = r.get(3);
            Map<String,String> params=new HashMap<String,String>();
            params.put("address", address);
            params.put("total", total);
            params.put("bounty", bounty);
            emailService.sendHpbMessageMail(params, title, sendTo, "bounty_coin.ftl");
        };
        map.put("send","ok");
        return map;
    }
}
