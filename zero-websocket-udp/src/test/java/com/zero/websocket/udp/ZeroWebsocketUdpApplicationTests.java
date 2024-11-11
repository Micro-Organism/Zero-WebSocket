package com.zero.websocket.udp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@SpringBootTest
class ZeroWebsocketUdpApplicationTests {

    /**
     * 发送数据
     */
    @Test
    void testSend() throws IOException {
        // 1.创建发送端套接字对象
        DatagramSocket ds = new DatagramSocket();
        // 2.创建数据并把数据打包
        byte[] bys = "Hello,World!".getBytes();
        DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 10086);
        // 3.从此套接字发送数据包
        ds.send(dp);
        // 4.关闭此数据包的套接字
        ds.close();
    }

    /**
     * 接收数据
     */
    @Test
    void testReceive() throws IOException {
        // 1.创建接收端套接字对象
        DatagramSocket ds = new DatagramSocket(10086);
        // 2.创建数据包用于接收数据
        byte[] bys = new byte[1024];
        DatagramPacket dp = new DatagramPacket(bys, bys.length);
        ds.receive(dp);
        // 3.解析数据包并把数据输出
        System.out.println("数据是：" + new String(dp.getData(), 0, dp.getLength()));
        // 4.关闭此数据包的套接字
        ds.close();
    }

    @Test
    void testSendClient() throws IOException {
        DatagramSocket ds = new DatagramSocket();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = br.readLine()) != null) {
            if ("886".equals(line)) {
                break;
            }
            byte[] bys = line.getBytes();
            DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName("127.0.0.1"), 10086);
            ds.send(dp);
        }
        ds.close();
    }

    @Test
    void testReceiveServer() throws IOException {
        DatagramSocket ds = new DatagramSocket(10086);
        while (true) {
            byte[] bys = new byte[1024];
            DatagramPacket dp = new DatagramPacket(bys, bys.length);
            ds.receive(dp);
            System.out.println("数据是：" + new String(dp.getData(), 0, dp.getLength()));
        }
    }

}
