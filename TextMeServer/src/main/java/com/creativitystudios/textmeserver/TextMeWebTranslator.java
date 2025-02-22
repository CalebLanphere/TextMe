package com.creativitystudios.textmeserver;

import com.creativitystudios.textmeserver.UserClasses.TextMeWebUsers;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class TextMeWebTranslator extends WebSocketServer {
    ArrayList<TextMeWebUsers> webUsersArrayList = new ArrayList<TextMeWebUsers>();
    TextMeServerNetworkManager networkManager;

    public TextMeWebTranslator(InetAddress ip, int port) {
        super(new InetSocketAddress(ip, port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        if(networkManager.newUsersAllowed) {
            if(webUsersArrayList.size() < TextMeServerNetworkManager.MAX_USERS) {
                webUsersArrayList.add(new TextMeWebUsers(webSocket));
                networkManager.increaseUserCount();
            } else {
                webUsersArrayList.add(new TextMeWebUsers(webSocket));
                preformCommand(0, webUsersArrayList.size() - 1, "");
                closeWebSocket(webUsersArrayList.size() - 1);
            }
        } else {
            webUsersArrayList.add(new TextMeWebUsers(webSocket));
            preformCommand(1, webUsersArrayList.size() - 1, "");
            closeWebSocket(webUsersArrayList.size() - 1);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        for(int j = 0; j < webUsersArrayList.size(); j++) {
            if(webUsersArrayList.get(j).getUserWebSocket().equals(webSocket)) {
                closeWebSocket(i);
                break;
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        int userIndex = -1;
        for(int i = 0; i < webUsersArrayList.size(); i++) {
            if(webUsersArrayList.get(i).getUserWebSocket().equals(webSocket)) {
                userIndex = i;
                break;
            }
        }
        int commandIndex;
        if((commandIndex = networkManager.parseMessageForCriticalCommandsWeb(s, userIndex)) == -1) {
            sendMessagesToWebUsers(s, false);
        } else {
           preformCommand(commandIndex, userIndex, s);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void setNetworkManager(TextMeServerNetworkManager netManager) {
        networkManager = netManager;
    }

    private void preformCommand(int commandIndex, int userIndex, String message) {
        switch(commandIndex) {
            case 0:
                sendMessageToWebUser("Server is full", userIndex);
                closeWebSocket(userIndex);
                break;
            case 1:
                sendMessageToWebUser("Server is not accepting new users", userIndex);
                closeWebSocket(userIndex);
                break;
            case 2:
                sendMessageToWebUser("Server is shutting down", userIndex);
                closeWebSocket(userIndex);
                break;
            case 4:
                String[] messages = networkManager.getMessageHistoryForWeb();
                for(int i = 0; i < messages.length; i++) {
                    sendMessageToWebUser(messages[i], userIndex);
                }
                sendMessageToWebUser(networkManager.CMD_MSG_MAP.get(7), userIndex);
                break;
            case 5:
                closeWebSocket(userIndex);
                break;
            case 8:
                sendMessagesToWebUsers(message.substring(0, message.indexOf(":") + 1) + " joined the chat", false);

                break;
            case 9:
                sendMessageToWebUser(networkManager.CMD_MSG_MAP.get(10) + networkManager.getServerName(), userIndex);
                break;
            case 11:
                sendMessagesToWebUsers(message.substring(message.indexOf(":") + 1) + "Changed their name to: " + message.substring(message.indexOf("o") + 2), false);
                break;
            case 12:
                webUsersArrayList.get(userIndex).setUsername(message.substring(message.indexOf("i") + 3));
                networkManager.fillUserControlBox();
                break;
            default:
                break;
        }
    }

    private void closeWebSocket(int userIndex) {
        webUsersArrayList.get(userIndex).closeWebSocket();
        webUsersArrayList.remove(userIndex);
        networkManager.decreasetUserCount();
    }

    protected void sendMessagesToWebUsers(String message, boolean receivedByNetworkManager) {
        for(int i = 0; i < webUsersArrayList.size(); i++) {
            if(webUsersArrayList.get(i).getUserWebSocket().isOpen()) {
                webUsersArrayList.get(i).getUserWebSocket().send(message);
            } else {
                closeWebSocket(i);
            } if (!receivedByNetworkManager) {
                networkManager.sendMessageNet(message, true);
            }
        }
    }

    private void sendMessageToWebUser(String message, int userIndex) {
        webUsersArrayList.get(userIndex).getUserWebSocket().send(message);
    }
}
