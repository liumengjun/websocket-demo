package com.example.wsdemo.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;

public class MyHandler extends TextWebSocketHandler implements EnvironmentAware {

    private Environment environment;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.printf("Session[%s] sent msg (len=%d): %s\n", session.getId(), message.getPayloadLength(), message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Session[" + session.getId() + "] is open...");
        if (session instanceof AbstractSockJsSession) {
            SockJsServiceConfig cfg = ((AbstractSockJsSession) session).getSockJsServiceConfig();
            TaskScheduler taskScheduler = cfg.getTaskScheduler();
            NTimesHello task = new NTimesHello(session);
            ScheduledFuture future = taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(1));
            task.setFuture(future);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Session[" + session.getId() + "] is closed.");
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    public static class NTimesHello implements Runnable {

        static final int MAX_TIMES = 5;

        private int count;

        private WebSocketSession session;

        private ScheduledFuture future;

        public NTimesHello(WebSocketSession session) {
            this.session = session;
        }

        public void setFuture(ScheduledFuture future) {
            this.future = future;
        }

        @Override
        public void run() {
            System.out.println("NTimesHello: " + count);
            if (count >= MAX_TIMES || session == null || !session.isOpen()) {
                future.cancel(false); // 自取消
                return;
            }
            try {
                session.sendMessage(new TextMessage(new Date().toString() + " rand " + new Random().nextInt(100)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            count++;
        }
    }
}
