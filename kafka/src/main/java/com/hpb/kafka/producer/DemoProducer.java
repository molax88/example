package com.hpb.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class DemoProducer {
    public static void main(String[] args) {
        long events = 10;
        Random rnd = new Random();

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.71.135:29092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<String, String>("test001", msg);
            producer.send(data);
            System.out.println(nEvents);
        }
        producer.close();
    }
}