package com.creativitystudios.textmewebtranslator;

//import com.creativitystudios.textmeserver.TextMeClientUser;
//import com.creativitystudios.textmeserver.TextMeServerController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.scene.layout.Pane;

import javax.naming.Context;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class TextMeWebTranslatorNetworkManager {

    private static ArrayList<TextMeWebTranslatorClientUser> userArrayList = new ArrayList<TextMeWebTranslatorClientUser>();
    private static HttpServer serSocket; // Server socket
    private static boolean newUsersAllowed = true; // Determines if new users can connect to the server
    private static final int MAX_USERS = 2147000000; // Max amount of users allowed on the server
    private static TextMeWebTranslatorController uiController; // UI Controller
    private static int usersOnServer = 0; // Number of users on the server

    public TextMeWebTranslatorNetworkManager(Pane ui, TextMeWebTranslatorController controller) {
        //appUI = ui;
        uiController = controller;
        //setupCommandHashMap();
    }

    /**
     * Attempts to start the server
     *
     * @return boolean if starting the server is successful
     */
    public void startServer(int port, String serverName) {
        String ip = "";
        try { // Tries to start a new ServerSocket at the localhost address with port "0"

            // Creates and sets inetA to a InetAddress bounded to the local host IP of the hosting server
            InetSocketAddress inetA;
            inetA = new InetSocketAddress(port);

            // Creates the server
            serSocket = HttpServer.create(inetA, 2);
            serSocket.createContext("/index", new HttpHandler() {
                @Override
                // Code below sends HTML code to user
                public void handle(HttpExchange exchange) throws IOException {
                    String message = exchange.getRequestMethod();
                    InputStream is = exchange.getRequestBody();
                    throwMessage(message + is.read(), true);
                    String response = "";
                    String line = "";
                    BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/com/creativitystudios/textmewebtranslator/index.html")));
                    while((line = fileReader.readLine()) != null) {
                        response += line;
                    }
                    fileReader.close();
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.flush();
                    os.close();
                }
            });
            serSocket.setExecutor(null);
            serSocket.start();
            uiController.setConnectionInfo(serSocket.getAddress().getHostName(), serSocket.getAddress().getPort());
            watchForMessages();
        } catch (Exception e) {
            throwMessage("Exception at socket creation" + " " + e.getMessage(), true); // ServerSocket could not be set
        }
    }

    /**
     * Check constantly for new messages sent to the server
     */
    public void watchForMessages() {
        Thread messageCheckLoop = new Thread(new Runnable() { // Creates a new Thread

            public void run() {
                while(true) { // Infinite loop
                    try {
                        for(int i = 0; i < userArrayList.size(); i++) { // Iterates through all instances inside "sockets"
                            if( userArrayList.get(i).getUserBufferedReader() != null) { // Checks to see if the bufferReader at "i" is valid
                                if(userArrayList.get(i).getUserBufferedReader().ready()) { // Checks to see if the message at BufferReader[i] is ready
                                    throwMessage((userArrayList.get(i).getUserBufferedReader().readLine()), false); // Sends message to parser
                                }
                            } else {

                            }
                        }
                    } catch (IOException IOE) {
                        throwMessage("IOE reading message", true);
                    }
                    // Make the infinite loop sleep for 50 milliseconds
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throwMessage("Sleep interrupted", true);
                    }
                }
            }
        });
        messageCheckLoop.start(); // Starts the message checker loop

    }

    /**
     * Outputs an error message to the user
     *
     * @param String Error message to print
     */
    private void throwMessage(String err, boolean isError) {
        uiController.throwMessage(err, isError);
    }
}
