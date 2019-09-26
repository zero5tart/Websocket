package com.hello.web;

import com.hello.sys.util.WebSocketUtil;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Component
@ServerEndpoint(value = "/my-chat/{userNick}")
public class WebSocketController {

    private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 连接事件 加入注解
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userNick") String userNick, Session session) {
        String message = "有新游客[" + userNick + "]加入聊天室!";
        log.info(message);
        WebSocketUtil.addSession(userNick, session);
        //此时可向所有的在线通知 某某某登录了聊天室
        WebSocketUtil.sendMessageForAll(message);
    }

    @OnClose
    public void onClose(@PathParam(value = "userNick") String userNick,Session session) {
        String message = "游客[" + userNick + "]退出聊天室!";
        log.info(message);
        WebSocketUtil.remoteSession(userNick);
        //此时可向所有的在线通知 某某某登录了聊天室
        WebSocketUtil.sendMessageForAll(message);
    }

    @OnMessage
    public void OnMessage(@PathParam(value = "userNick") String userNick, String message) {
        //类似群发
        String info = "游客[" + userNick + "]：" + message;
        log.info(info);
        WebSocketUtil.sendMessageForAll(info);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("异常:", throwable);
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

}
