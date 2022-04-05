module com.chat.chatserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.chat.chatserver to javafx.fxml;
    exports com.chat.chatserver;
}