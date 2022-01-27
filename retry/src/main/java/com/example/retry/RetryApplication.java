package com.example.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.TimeoutException;

/**
 * @author molax
 * @date 2022/1/27 13:17
 */
@SpringBootApplication
@EnableRetry
@Controller("/")
public class RetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RetryApplication.class, args);
    }

    @GetMapping("/retry")
    @Retryable(maxAttempts = 5)
    public void test() throws Exception {
        System.err.println("error");
        throw new TimeoutException("sorry");
    }

    @Recover
    public void recover(){
        System.err.println("opps, it`s failed");
    }

}
