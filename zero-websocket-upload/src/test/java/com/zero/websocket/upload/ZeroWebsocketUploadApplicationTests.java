package com.zero.websocket.upload;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

@SpringBootTest
class ZeroWebsocketUploadApplicationTests {

    @Test
    void testSingleThreadClient() throws IOException {
        // 创建客户端Socket对象
        Socket s = new Socket("127.0.0.1", 10086);
        // 封装文本文件的数据
        BufferedReader br = new BufferedReader(new FileReader("src\\ClientDemo.java"));
        // 封装输出流写出数据
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        // 告诉服务器上传结束
        s.shutdownOutput();
        // 接收服务器端的反馈
        BufferedReader brClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String data = brClient.readLine();
        System.out.println("服务器反馈：" + data);
        // 释放资源
        br.close();
        s.close();
    }

    @Test
    void testSingleThreadServer() throws IOException {
        // 创建服务器Socket对象
        ServerSocket ss = new ServerSocket(10086);
        // 监听客户端的连接对象
        Socket s = ss.accept();
        // 获取上传文件随机名称
        String fileName = "src\\" + System.currentTimeMillis() + new Random().nextInt(999999) + ".java";
        // 保存客户端上传的数据
        BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
        byte[] bys = new byte[1024];
        int len;
        while ((len = bis.read(bys)) != -1) {
            bos.write(bys, 0, len);
        }
        // 给客户端发出反馈信息
        BufferedWriter bwServer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        bwServer.write("文件上传成功");
        bwServer.newLine();
        bwServer.flush();
        // 释放资源
        bos.close();
        ss.close();
    }

    @Test
    void testMultiThreadClient() throws IOException {
        // 创建客户端Socket对象
        Socket s = new Socket("127.0.0.1", 10086);
        // 打开本地文件准备上传
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("src\\ClientDemo.java"));
        BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
        byte[] bys = new byte[1024];
        int len;
        while ((len = bis.read(bys)) != -1) {
            bos.write(bys, 0, len);
            bos.flush();
        }
        // 告诉服务器上传结束
        s.shutdownOutput();
        // 接收服务器端的反馈
        BufferedReader brClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String data = brClient.readLine();
        System.out.println("服务器反馈：" + data);
        // 释放资源
        bis.close();
        s.close();
    }

    @Test
    void testMultiThreadServer() throws IOException {
        ServerSocket ss = new ServerSocket(10086);
        while (true) {
            Socket s = ss.accept();
            ServerThread serverThread = new ServerThread(s);
            new Thread(serverThread).start();
        }
    }

    static class ServerThread implements Runnable {
        private final Socket s;

        public ServerThread(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            try {
                // 获取上传文件随机名称
                String fileName = "src\\" + System.currentTimeMillis() + new Random().nextInt(999999) + ".java";
                // 保存客户端上传的数据
                BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
                byte[] bys = new byte[1024];
                int len;
                while ((len = bis.read(bys)) != -1) {
                    bos.write(bys, 0, len);
                }
                // 给客户端发出反馈信息
                BufferedWriter bwServer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                bwServer.write("文件上传成功");
                bwServer.newLine();
                bwServer.flush();
                // 释放资源
                bos.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
