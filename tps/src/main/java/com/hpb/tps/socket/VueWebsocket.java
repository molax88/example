package com.hpb.tps.socket;


import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/***
 *
 * Created by 80071482 on 2018/2/23.
 * Author (Bony)
 ***/
@ServerEndpoint(value = "/websocket")
@Component
public class VueWebsocket {

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("有一连接关闭");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception{
        System.out.println("来自客户端的消息:" + message);
        if("start".equals(message)) {
            EccPerfSocketServer.softTotal.set(0);
            EccPerfSocketServer.eccSoftTotal.set(0);
            EccPerfSocketServer.hardTotal.set(0);
            EccPerfSocketServer.eccHardTotal.set(0);
            Map<String, Object> map = new HashMap<String, Object>();
            boolean boo = true;
            Instant softStart = Instant.now();
            Long softTime = 0L;
            Instant hardStart = Instant.now();
            Long hardTime = 0L;
            new HardShell().start();
            new SoftShell().start();
            while (boo) {
                if((EccPerfSocketServer.softTotal.intValue()>0 &&
                        EccPerfSocketServer.eccSoftTotal.intValue()>=EccPerfSocketServer.softTotal.intValue()) &&
                        (EccPerfSocketServer.hardTotal.intValue()>0 &&
                        EccPerfSocketServer.eccHardTotal.intValue()>=EccPerfSocketServer.hardTotal.intValue()))
                    boo = false;
                if(EccPerfSocketServer.eccSoftTotal.intValue()>0){
                    if(EccPerfSocketServer.eccSoftTotal.intValue()<EccPerfSocketServer.softTotal.intValue()){
                        softTime = softStart.until(Instant.now(), ChronoUnit.MILLIS);
                    }
                }else{
                    softStart = Instant.now();
                }
                if(EccPerfSocketServer.eccHardTotal.intValue()>0){
                    if(EccPerfSocketServer.eccHardTotal.intValue()<EccPerfSocketServer.hardTotal.intValue()){
                        hardTime = hardStart.until(Instant.now(),ChronoUnit.MILLIS);
                    }
                }else{
                    hardStart = Instant.now();
                }
                map.clear();
                map.put("softTotal", EccPerfSocketServer.softTotal);
                map.put("eccSoftTotal", EccPerfSocketServer.eccSoftTotal);
                map.put("softCpu", EccPerfSocketServer.softCpu);
                map.put("softTime", softTime);
                map.put("hardTotal", EccPerfSocketServer.hardTotal);
                map.put("eccHardTotal", EccPerfSocketServer.eccHardTotal);
                map.put("hardCpu", EccPerfSocketServer.hardCpu);
                map.put("hardTime", hardTime);
                System.out.println(JSON.toJSONString(map));
                session.getBasicRemote().sendText(JSON.toJSONString(map));
                Thread.sleep(100);
            }
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    class SoftShell extends Thread{
        public void run(){
            try {
                String cmd = "sh soft_start.sh";
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class HardShell extends Thread{
        public void run(){
            try {
                String cmd = "sh hard_start.sh";
                Process process = Runtime.getRuntime().exec(cmd);
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}