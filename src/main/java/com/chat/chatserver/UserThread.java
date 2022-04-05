package com.chat.chatserver;

import javafx.application.Platform;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private String userName;
    private DataInputStream inputFromClient;
    private DataOutputStream outputToClient;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            inputFromClient = new DataInputStream(socket.getInputStream());
            outputToClient = new DataOutputStream(socket.getOutputStream());

            printUsers();

            userName = inputFromClient.readUTF();
            server.addUserName(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            while (true) {
                clientMessage = inputFromClient.readUTF();
                if (clientMessage.equals("<QUIT>"))
                    break;

                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

                String finalServerMessage = serverMessage + "\n";
                Platform.runLater(() -> server.print(finalServerMessage));
            }

            server.removeUser(userName, this);
            socket.close();
            server.print("User " + userName + " has quit");
            server.broadcast("User " + userName + " has quit", this);
        } catch(IOException ex) {
            server.print(ex.getMessage());
        }
    }

    public void printUsers() {
        try {
            if (server.hasUsers())
                outputToClient.writeUTF("Connected users: " + server.getUserNames());
            else
                outputToClient.writeUTF("No other users connected");
        } catch(IOException ex) {
            server.print(ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            outputToClient.writeUTF(message);
        } catch(IOException ex) {
            server.print(ex.getMessage());
        }
    }

    public String getUserName() {
        return userName;
    }
}