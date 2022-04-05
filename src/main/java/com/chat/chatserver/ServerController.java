package com.chat.chatserver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class ServerController {
    private ChatServer server;
    @FXML
    public TextArea outputTextArea;
    @FXML
    public Button startButton;
    @FXML
    public Button quitButton;
    @FXML
    public ListView<String> clientListView;

    public void update(String message) {
        outputTextArea.appendText(message);
    }

    public void onStartClicked(MouseEvent mouseEvent) {
        server = new ChatServer(8000, this);
        server.execute();
        startButton.setVisible(false);
    }

    public void onQuitClicked(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void addClient(String user) {
        clientListView.getItems().add(user);
    }

    public void removeClient(String user) {
        clientListView.getItems().remove(user);
    }
}