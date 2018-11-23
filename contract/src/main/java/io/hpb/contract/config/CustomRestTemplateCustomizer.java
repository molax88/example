package io.hpb.contract.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

@Component  
public class CustomRestTemplateCustomizer implements RestTemplateCustomizer {  
    @Override  
    public void customize(RestTemplate restTemplate) {  
        new RestTemplateBuilder()  
                .detectRequestFactory(false)  
                .basicAuthorization("username", "password")  
                .uriTemplateHandler((UriTemplateHandler) new OkHttp3ClientHttpRequestFactory())  
                .errorHandler(new CustomErrorHandler())  
                .configure(restTemplate);  
    }  
}  
