package com.creativitystudios.textmeserver;

import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.Socket;

public class TextMeClientUser {
    private int userIndex = -1;
    private Socket getUserSocket;
    private BufferedReader userIn;
    private PrintStream userOut;
    private String userUsername;
    private TextMeServerEncryption encryption = new TextMeServerEncryption();
    private boolean readyForEncryption = false;

    public TextMeClientUser(Socket clientSocket, int userIndex) {
        this.userIndex = userIndex;
        this.getUserSocket = clientSocket;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public Socket getClientSocket() {
        return getUserSocket;
    }

    public BufferedReader getUserBufferedReader() {
        return userIn;
    }

    public PrintStream getUserPrintStream() {
        return userOut;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserBufferedReader(BufferedReader br) {
        userIn = br;
    }

    public void setUserPrintStream(PrintStream ps) {
        userOut = ps;
    }

    public void setUserUsername(String username) {
        userUsername = username;
    }

    public TextMeServerEncryption getEncryption() {
        return encryption;
    }

    public TextMeServerEncryption.EncryptionStatuses getCurrentEncryptionMethod() {
        return encryption.encryptionStatus;
    }

    public void setCurrentEncryptionMethod(TextMeServerEncryption.EncryptionStatuses encStatus) {
        encryption.encryptionStatus = encStatus;
    }

}
