package com.hpb.kafka.socket;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketClientDemo implements Runnable{  
    private Socket socket;  
    private BufferedReader reader;  
    private BufferedOutputStream writer;  
    public SocketClientDemo(){  
        try{  
            //127.0.0.1表示本机IP，10000为服务器Socket设置的端口  
            socket = new Socket("127.0.0.1", 8080);  
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));  
            writer = new BufferedOutputStream(socket.getOutputStream(), 1024*1024);  
            writer.write(new String("hello").getBytes(StandardCharsets.UTF_8)); 
        }catch(IOException e){  
            e.printStackTrace();  
        }  
    }  
    public void run(){  
        try{  
            //这里就可以读取所有行String  
            String line, buffer="";  
            while(!((line = reader.readLine())==null))  
                buffer+=line;  
                System.out.println(buffer);  
        }catch(IOException e){    
            e.printStackTrace();  
            System.out.println("problem");  
        }finally{  
            //最后关闭Socket  
            try{  
                if(socket!=null)socket.close();  
                if(reader!=null)reader.close();  
                if(writer!=null)writer.close();  
            }catch(IOException e){  
                e.printStackTrace();  
            }  
              
        }  
          
    }  
    public static void main(String[] args) throws UnknownHostException, IOException{  
//        new Thread(new SocketClientDemo()).start();  
        
//        Socket socket = new Socket("47.94.241.68", 28080);
//        Socket socket = new Socket("127.0.0.1", 28080);
//      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
//        OutputStream outputStream = socket.getOutputStream();
//        String filePath = "/tmp/testj";
//        File file = new File(filePath);
//        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
//        byte[] b = new byte[(int)file.length()];
//        int len;
//        StringBuffer str = new StringBuffer();
//        while((len = fis.read(b))>0){
//            str.append(new String(b,0,len,StandardCharsets.ISO_8859_1));
//            outputStream.write(b);
//        }
//        fis.close();
//        outputStream.write("EOF".getBytes());
//        outputStream.write(new String("\f*ÌÕB!K]@àXaQ?").getBytes(StandardCharsets.ISO_8859_1));
//        outputStream.close();
//        socket.close();

        //验签次数同步测试
        for(int i=0;i<10;i++){
        Socket socket = new Socket("127.0.0.1", 28081);
        OutputStream outputStream = socket.getOutputStream();

            int tps = 11013+i;
            String str = "{\"ishard\": \"1\",\"vcntps\": \""+ tps +"\",\"cupusage\": \"50\"}\n";
            System.out.println(str);
            outputStream.write(str.getBytes(StandardCharsets.UTF_8));

//        String str = "{\"ishard\": \"0\",\"vcntps\": \"9999\",\"cupusage\": \"15\"}\n";
//        outputStream.write(str.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        socket.close();}
    }  
  
}  