package com.creativitystudios.textmeserver;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

@ServerEndpoint(value = "/")
public class WebServerEndpoint {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    ArrayList<TextMeWebUsers> users = new ArrayList<TextMeWebUsers>();
    protected TextMeServerNetworkManager netS;
    @OnOpen
    public void onOpen(Session session) {

    }

    @OnMessage
    public void onMessage(String message, Session session) {
        for(Session sess: session.getOpenSessions()) {
            try {
                sess.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @OnError
    public void onError(Throwable error, Session session) {

    }

    @OnClose
    public void onClose(Session session) {

    }
}
