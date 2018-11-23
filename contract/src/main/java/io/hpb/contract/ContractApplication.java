package io.hpb.contract;

import io.hpb.contract.common.SpringBootContext;
import io.hpb.contract.listener.HpbApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@Configuration
@EnableScheduling
@EnableAsync
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@ComponentScan(basePackages = "io.hpb.contract")
public class ContractApplication {

    private static final Logger logger = LoggerFactory.getLogger(ContractApplication.class);
    protected static String RELOAD_KEY = "spring.devtools.restart.enabled";
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ContractApplication.class);
    }
    public static void main(String[] args) {
        System.setProperty(RELOAD_KEY, "false");
        SpringApplication springApplication = new SpringApplication(ContractApplication.class);
        springApplication.addListeners(new HpbApplicationListener());
        springApplication.setBannerMode(Banner.Mode.OFF);
        ApplicationContext aplicationContext = springApplication.run(args);
        SpringBootContext.setAplicationContext(aplicationContext);
        logger.info("contract Application 启动完成");
    }
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            if (SpringBootContext.getAplicationContext() == null) {
                SpringBootContext.setAplicationContext(ctx);
            }
        };
    }
}
