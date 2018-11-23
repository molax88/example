package io.hpb.contract.config;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class CustomErrorHandler extends DefaultResponseErrorHandler {  
	  
    @Override  
    public void handleError(ClientHttpResponse response) throws IOException {  
  
    }  
  
}  