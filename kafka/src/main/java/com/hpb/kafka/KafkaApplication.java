package com.hpb.kafka;

import com.hpb.kafka.producer.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplication.class, args);
    }

    @Autowired
    private Sender sender;

    @Override
    public void run(String... strings) throws Exception {
        sender.send("Spring Kafka Producer and Consumer Example");
    }
}
