package me.hjyoon.multichat;

import java.io.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MyReceiveThread extends Thread {
    private Socket clientSocket;
    private BufferedReader br;

    MyReceiveThread(Socket clientSocket, BufferedReader br) {
        this.clientSocket = clientSocket;
        this.br = br;
    }

    @Override
    public void run() {
        try {
            while(true) {
                if(!clientSocket.isConnected() && clientSocket.isClosed()) {
                    break;
                }

                String receivedData = br.readLine();
                if(receivedData == null) {
                    break;
                }
                System.out.println(receivedData); // 서버에서 받은 데이터를 출력
            }
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}