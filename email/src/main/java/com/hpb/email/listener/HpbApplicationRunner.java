package com.hpb.email.listener;

import com.hpb.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HpbApplicationRunner implements ApplicationRunner {
    @Autowired
    private EmailService emailService;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        System.out.println("启动了。。。");
        /*List<List<String>> list = POIUtil.readExcel("D:/backup/weitui.xls",2);
        list.stream().forEach(r -> {
            System.out.println(r.get(0));
            String sendTo = r.get(0);
            String title = "HPB未退币奖励通知测试";
            String address = r.get(1);
            String total = r.get(2);
            String bounty = r.get(3);
            Map<String,String> params=new HashMap<String,String>();
            params.put("address", address);
            params.put("total", total);
            params.put("bounty", bounty);
            emailService.sendHpbMessageMail(params, title, sendTo, "bounty_coin.ftl");
        });*/
    }
}
