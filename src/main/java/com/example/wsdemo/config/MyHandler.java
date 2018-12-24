package com.example.wsdemo.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler implements EnvironmentAware {

    private Environment environment;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // ...
        System.out.printf("Session[%s]: (len=%d)%s", session.getId(), message.getPayloadLength(), message.getPayload());
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
}
