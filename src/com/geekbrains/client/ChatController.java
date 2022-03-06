package com.geekbrains.client;

import com.geekbrains.server.ServerCommandConstants;

import javafx.application.Platform;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField messageField, loginField;
    @FXML
    private HBox messagePanel, authPanel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<String> clientList;


    private final Network network;

    public ChatController() {
        this.network = new Network(this);
    }

    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthenticated(false);
    }


    public void displayMessage(String text) {


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int oldText = textArea.getLength() + 1;
                String[] newText = text.split(" ");
                int selectText = oldText + newText[0].length() + newText[1].length() + 1;
                if (textArea.getText().isEmpty()) {
                    textArea.setText(text);
                } else {
                    textArea.setText(textArea.getText() + "\n" + text);
                }
                if (text.contains(" private ")) {
                    textArea.selectRange(oldText, selectText);
                }
            }
        });
    }


    public void displayClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().add(nickName);
            }
        });
    }


    public void removeClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().remove(nickName);
            }
        });
    }


    public void sendAuth(ActionEvent event) {
        boolean authenticated = network.sendAuth(loginField.getText(), passwordField.getText());
        if (authenticated) {
            loginField.clear();
            passwordField.clear();
            setAuthenticated(true);

        }
    }

    public void sendMessage(ActionEvent event) {
        network.sendMessage(messageField.getText());
        messageField.clear();
    }

    public void close() {
        network.closeConnection();
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        String nickName = clientList.getSelectionModel().getSelectedItem();
        messageField.setText(ServerCommandConstants.PRIVATE + " " + nickName + " ");
    }
}

