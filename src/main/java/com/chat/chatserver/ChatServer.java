package com.chat.chatserver;

import javafx.application.Platform;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {
    private int port;
    private int clientNo = 0;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    private ServerController controller;

    public ChatServer(int port, ServerController controller) {
        this.port = port;
        this.controller = controller;
    }

    public void execute() {
        new Thread( () -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                print("ChatServer started at " + new Date() + '\n');

                while (true) {
                    Socket socket = serverSocket.accept();

                    UserThread newUser = new UserThread(socket, this);
                    addUserThread(newUser);
                    newUser.start();

                    Platform.runLater( () -> {
                        InetAddress inetAddress = socket.getInetAddress();
                        print("Starting thread for Client " + ++clientNo + " at " + new Date() + '\n');
                        print("Client " + clientNo + "'s username is " + newUser.getUserName() + " (IP Address: " + inetAddress.getHostAddress() + ")\n");
                    });
                }
            } catch(IOException ex) {
                print(ex.getMessage());
            }
        }).start();
    }


    void broadcast(String message, UserThread excludeUser) throws IOException {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void print(String message) {
        controller.update(message);
    }

    void addUserName(String userName) {
        userNames.add(userName);
        controller.addClient(userName);
    }

    void addUserThread(UserThread userThread) {
        userThreads.add(userThread);
    }

    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed)
            userThreads.remove(aUser);

        controller.removeClient(userName);
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}