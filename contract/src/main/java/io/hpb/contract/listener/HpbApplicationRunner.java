package io.hpb.contract.listener;

import io.hpb.contract.mapper.AuthSessionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class HpbApplicationRunner implements ApplicationRunner{
    @Autowired
    private AuthSessionMapper sessionMapper;
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        sessionMapper.init();
    }
}
