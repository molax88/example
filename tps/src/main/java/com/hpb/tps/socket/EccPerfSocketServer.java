package com.hpb.tps.socket;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class EccPerfSocketServer {
	private static final Logger logger = LoggerFactory.getLogger(EccPerfSocketServer.class);
    private ServerSocket serverSocket;

    int port = 28081;

    static List<Integer> soft = new ArrayList<>(10);
    static List<Integer> hard = new ArrayList<>(10);
//    static int softIndex=0,hardIndex=0,i =0,softTotal=0,eccSoftTotal=0,hardTotal=0,eccHardTotal=0;
    static AtomicInteger softIndex=new AtomicInteger(0),
        hardIndex=new AtomicInteger(0),
//        i =new AtomicInteger(0),
        softTotal=new AtomicInteger(0),
        eccSoftTotal=new AtomicInteger(0),
        hardTotal=new AtomicInteger(0),
        eccHardTotal=new AtomicInteger(0);
    int i =0;
    static String softCpu="0",hardCpu="0";
//    static List<Double> softCpu = new ArrayList<>(10);
//    static List<Double> hardCpu = new ArrayList<>(10);

    public EccPerfSocketServer(){
    	logger.info("---------------EccPerfSocketServer init---------------------");
        try{  
            serverSocket = new ServerSocket(port);  
        }catch(IOException e){  
            e.printStackTrace();  
        }  
        //创建新的监听主线程，这个线程创建ServerSocket监听  
        new Thread(new Runnable(){  
            public void run(){  
                while(true){
                    Socket socket = null;  
                    try{  
                        socket = serverSocket.accept();  
                        logger.info("建立新的连接");
                        logger.info("连接主机ip："+socket.getInetAddress().getHostName());
                        SocketHandler socketHandler = new SocketHandler(socket);
						new Thread(socketHandler).start();  
                        logger.info("建立新的连接成功");
                    }catch(Exception e){  
                        e.printStackTrace();
                        logger.error("建立连接失败", e);
                    }  
                }  
            }  
        }).start();  
    }
    
    public static void start() {
    	
    }
      
    class SocketHandler implements Runnable{  
        private static final int CAPACITY = 1024*10;
		private Socket socket;  
        private BufferedInputStream reader;  
        private StringBuilder sb = new StringBuilder(CAPACITY);
        SocketHandler(Socket socket){  
            try{  
                this.socket = socket;  
                reader = new BufferedInputStream(this.socket.getInputStream()); 
                logger.info("获取输入流");
            }catch(IOException e){  
                e.printStackTrace(); 
                logger.error("读取流失败", e);
            }  
              
        }  
        //这里是覆盖实现接口Runnable里的run（）
        public /*synchronized*/ void run(){
            try{  
            	logger.info("开始处理数据");
            	BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String origin;
                while((origin = reader.readLine()) != null){
                    System.out.println("-----------------第"+ ++i +"次导入开始----------------------");
                    System.out.println(i+"原始数据："+origin);
                    Map<String, Object> map = new HashMap<>();
                    try {
                        map = JSON.parseObject(origin, Map.class);
                    }catch (Exception e){
                        System.out.println(i+"json解析失败。。。。。。");
                    }
                    String ishard = MapUtils.getString(map,"ishard");
                    int total = MapUtils.getInteger(map,"total",100000);
                    Integer vcntps = MapUtils.getInteger(map,"vcntps");
                    String cupusage = MapUtils.getString(map,"cupusage");
                    if ("0".equals(ishard)) {//软件tps
                        softTotal = new AtomicInteger(total);
                        eccSoftTotal.addAndGet(vcntps);
                        softCpu=cupusage;
                        System.out.println(i+"softCpu==" + softCpu);
                        /*softTotal = total;
                        eccSoftTotal+=vcntps;
                        if(soft.size()==10) {
                            soft.set(softIndex, vcntps);
                            softCpu.set(softIndex, cupusage);
                        }else{
                            soft.add(vcntps);
                            softCpu.add(cupusage);
                        }
                        softIndex++;
                        if(softIndex>=10)softIndex=0;*/
                    }else if("1".equals(ishard)){//硬件tps
                        hardTotal = new AtomicInteger(total);
                        eccHardTotal.addAndGet(vcntps);
                        hardCpu=cupusage;
                        /*hardTotal = total;
                        eccHardTotal+=vcntps;
                        if(hard.size()==10) {
                            hard.set(hardIndex, vcntps);
                            hardCpu.set(hardIndex, cupusage);
                        }else{
                            hard.add(vcntps);
                            hardCpu.add(cupusage);
                        }
                        hardIndex++;
                        if(hardIndex>=10)hardIndex=0;*/
                    }
                    /*OptionalDouble save = soft.stream().mapToInt(Integer::intValue).average();
                    OptionalDouble have = hard.stream().mapToInt(Integer::intValue).average();
                    OptionalDouble scpuave = softCpu.stream().mapToDouble(Double::doubleValue).average();
                    OptionalDouble hcpuave = hardCpu.stream().mapToDouble(Double::doubleValue).average();
                    System.out.println(i+"软件验签平均值："+(save.isPresent()?save.getAsDouble():0));
                    System.out.println(i+"硬件验签平均值："+(have.isPresent()?have.getAsDouble():0));
                    System.out.println(i+"软件cpu平均值："+(scpuave.isPresent()?scpuave.getAsDouble():0));
                    System.out.println(i+"硬件cpu平均值："+(hcpuave.isPresent()?hcpuave.getAsDouble():0));*/
                    System.out.println(i+"软件累计验签总数："+eccSoftTotal);
                    System.out.println(i+"硬件累计验签总数："+eccHardTotal);
                    System.out.println("-----------------第"+ i +"次导入结束----------------------");
                }
//            	socket.getOutputStream().write(new String("OK").getBytes(StandardCharsets.UTF_8));
            	logger.info("解析文件入库成功!");
            }catch(IOException e){
            	e.printStackTrace();
            	 logger.error("解析扫描文件失败", e);
            }finally{  
                //最后要关闭Socket  
                try{
                    if(socket!=null)socket.close();
                    if(reader!=null)reader.close();
                }catch(IOException e){
                    e.printStackTrace();
                    logger.error("解析扫描文件失败，关闭流失败", e);
                }  
            }  
        }  
    }  
    
    public static void main(String[] args) throws Exception{
//    	new EccPerfSocketServer();
        Instant softStart = Instant.now();
        Thread.sleep(10330);
        Instant end = Instant.now();
        long tim = softStart.until(end,ChronoUnit.MILLIS);
        System.out.println(tim);
	}
}  
