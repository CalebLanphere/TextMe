package com.creativitystudios.textmeserver;

import javax.websocket.Session;

public class TextMeWebUsers {
    private Session userSession;

    public void setUserSession(Session s) {
        userSession = s;
    }

    public Session getUserSession() {
        return userSession;
    }
}
