package ru.uryupin.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientSession session = new ClientSession(clientSocket);
                    new Thread(session).start();
                } catch (IOException e) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}