package com.creativitystudios.textmeserver.UserClasses;

import org.java_websocket.WebSocket;

public class TextMeWebUsers {
    private WebSocket userWebSocket;
    private String username;

    public TextMeWebUsers(WebSocket webSocket) {
        userWebSocket = webSocket;
    }

    public WebSocket getUserWebSocket() {
        return userWebSocket;
    }

    public void closeWebSocket() {
        userWebSocket.close();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
