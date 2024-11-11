package com.zero.websocket.tcp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.*;

@SpringBootTest
class ZeroWebsocketTcpApplicationTests {

    @Test
    void testSend() throws IOException {
        // 1.创建客户端的Socket对象
        Socket s = new Socket("127.0.0.1", 10086);
        // 2.获取输出流对象，写数据
        OutputStream os = s.getOutputStream();
        os.write("Hello,World!".getBytes());
        // 3.释放资源
        s.close();
    }

    @Test
    void testReceive() throws IOException {
        // 1.创建服务端的ServerSocket对象
        ServerSocket ss = new ServerSocket(10086);
        // 2.侦听要连接到此套接字并接受它
        Socket s = ss.accept();
        // 3.获取输入流对象，读数据
        InputStream is = s.getInputStream();
        byte[] bys = new byte[1024];
        int len = is.read(bys);
        String data = new String(bys, 0, len);
        System.out.println("数据是：" + data);
        // 4.释放资源
        s.close();
        ss.close();
    }

    @Test
    void testClient() throws IOException {
        Socket s = new Socket("127.0.0.1", 10086);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            if ("886".equals(line)) {
                break;
            }
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        s.close();
    }

    @Test
    void testServer() throws IOException {
        ServerSocket ss = new ServerSocket(10086);
        Socket s = ss.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        ss.close();
    }

}
