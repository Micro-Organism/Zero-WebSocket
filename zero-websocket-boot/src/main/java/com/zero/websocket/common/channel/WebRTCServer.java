package com.zero.websocket.common.channel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/webrtc/{fromUser}")
public class WebRTCServer {

    /**
     * 当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 用来存放每个客户端对应的 WebSocketServer 对象
     */
    private static ConcurrentHashMap<String, WebRTCServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收 fromUser
     */
    private String fromUser = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("fromUser") String fromUser) {
        this.session = session;
        this.fromUser = fromUser;
        if (webSocketMap.containsKey(fromUser)) {
            webSocketMap.remove(fromUser);
            webSocketMap.put(fromUser, this);
        } else {
            webSocketMap.put(fromUser, this);
            addOnlineCount();
        }
        log.info("用户连接:" + fromUser + ",当前在线人数为:" + getOnlineCount());
        try {
            sendMessage("连接成功！");
        } catch (IOException e) {
            log.error("用户:" + fromUser + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(fromUser)) {
            webSocketMap.remove(fromUser);
            subOnlineCount();
        }
        log.info("用户退出:" + fromUser + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + fromUser + ",报文:" + message);
        if (!StringUtils.isEmpty(message)) {
            try {
                JSONObject jsonObject = JSON.parseObject(message);
                jsonObject.put("fromUser", this.fromUser);
                String toUser = jsonObject.getString("toUser");
                if (!StringUtils.isEmpty(toUser) && webSocketMap.containsKey(toUser)) {
                    webSocketMap.get(toUser).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的 fromUser:" + toUser + "不在该服务器上");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.fromUser + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized AtomicInteger getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebRTCServer.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        WebRTCServer.onlineCount.getAndDecrement();
    }
}