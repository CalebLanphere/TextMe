/**
 * @author Caleb Lanphere
 *
 * TextMe Application Server Client User Template
 *
 * Copyright 2025 | Caleb Lanphere | All Rights Reserved
 *
 */

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
    private boolean readyForMessages = false;

    /**
     *Initializes the connected user with the instantly required values
     *
     * @param clientSocket Socket - Socket to identify the user by
     * @param userIndex int - Index of the user
     */
    public TextMeClientUser(Socket clientSocket, int userIndex) {
        this.userIndex = userIndex;
        this.getUserSocket = clientSocket;
    }

    /**
     * Gets if the user is ready for new messages sent by other users
     *
     * @return if ready for messages
     */
    public boolean getReadyForMessages() {
        return readyForMessages;
    }

    /**
     * Sets if readyForMessages is true or false
     *
     * @param bool boolean - Value to set readyForMessages
     */
    public void setReadyForMessages(boolean bool) {
        readyForMessages = bool;
    }

    /**
     * Gets the user's socket
     *
     * @return Socket - Socket associated to the user
     */
    public Socket getClientSocket() {
        return getUserSocket;
    }

    /**
     * Gets the BuffferedReader associated to the user
     *
     * @return BufferedReader - BufferedReader of the user
     */
    public BufferedReader getUserBufferedReader() {
        return userIn;
    }

    /**
     * Gets the PrintStream associated to the user
     *
     * @return PrintStream - PrintStream of the user
     */
    public PrintStream getUserPrintStream() {
        return userOut;
    }

    /**
     * Gets the username of the user
     *
     * @return String - Username associated with the user
     */
    public String getUserUsername() {
        return userUsername;
    }

    /**
     * Sets the users BufferedReader
     *
     * @param br BufferedReader - BufferedReader to associate with the users InputStream
     */
    public void setUserBufferedReader(BufferedReader br) {
        userIn = br;
    }

    /**
     * Sets the users PrintStream
     *
     * @param ps PrintStream - PrintStream to associate with the users OutputStream
     */
    public void setUserPrintStream(PrintStream ps) {
        userOut = ps;
    }

    /**
     * Sets the username associated to the user
     *
     * @param username String - username to change to
     */
    public void setUserUsername(String username) {
        userUsername = username;
    }

    /**
     * Gets the encryption class from the user
     *
     * @return TextMeServerEncryption - Encryption class for the user
     */
    public TextMeServerEncryption getEncryption() {
        return encryption;
    }

    /**
     * Gets the encryption method the user has set
     *
     * @return TextMeServerEncryption.EncryptionStatuses - Encryption status of the user
     */
    public TextMeServerEncryption.EncryptionStatuses getCurrentEncryptionMethod() {
        return encryption.encryptionStatus;
    }

    /**
     * Sets the encryption method the user has
     *
     * @param encStatus TextMeServerEncryption.EncryptionStatuses - Encryption status to set for the user
     */
    public void setCurrentEncryptionMethod(TextMeServerEncryption.EncryptionStatuses encStatus) {
        encryption.encryptionStatus = encStatus;
    }

}
