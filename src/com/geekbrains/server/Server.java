package com.geekbrains.server;

import com.geekbrains.CommonConstants;
import com.geekbrains.client.ChatController;
import com.geekbrains.server.authorization.AuthService;
import com.geekbrains.server.authorization.InMemoryAuthServiceImpl;


import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final AuthService authService;
    private final ExecutorService executorService;
    private List<ClientHandler> connectedUsers;

    public Server() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        authService = new InMemoryAuthServiceImpl();
        try (ServerSocket serverSocket = new ServerSocket(CommonConstants.SERVER_PORT)) {
            authService.start();
            connectedUsers = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(executorService,this, socket);
                            }
        } catch (IOException exception) {
            System.out.println("Ошибка в работе сервера");
            exception.printStackTrace();
        } finally {
            if (authService != null) {
                authService.end();
                executorService.shutdown();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }
        return false;
    }
    //ДЛОБАВЛЕНО CHANGENICK ДЛЯ ИЗМЕНЕНИЯ В АРРЕЙЛИСТЕ  connectedUsers НИКНЕЙМА
    public synchronized void broadcastMessage(String message) throws IOException {
        if (message.contains(ServerCommandConstants.CHANGENICK)) {
            for (ClientHandler handler : connectedUsers) {
                String[] client = message.split(" ");
                String[] oldN = client[0].split(":");
                if (handler.getNickName().equals(oldN[0])) {
                    handler.setNickName(client[2]);
                    break;
                }
            }
        }
        //КОНЕЦ НОВОВВЕДЕНИЙ
        for (ClientHandler handler : connectedUsers) {
            if (message.contains(ServerCommandConstants.PRIVATE)) {
                String[] client = message.split(" ");
                if (client[2].equals(handler.getNickName())) {
                    handler.sendMessage(message);
                    break;
                }
            } else {
                handler.sendMessage(message);
            }
        }
    }


    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
    }

    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
    }

    public String getClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (ClientHandler user : connectedUsers) {
            builder.append(user.getNickName()).append(" ");
        }
        return builder.toString();
    }
}
