package me.hjyoon.multichat;

import java.io.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MySendThread extends Thread {
    private Socket clientSocket;
    private BufferedReader br;
    private PrintWriter pw;
    private String nickname;

    MySendThread(Socket clientSocket, BufferedReader br, PrintWriter pw, String nickname) {
        this.clientSocket = clientSocket;
        this.br = br;
        this.pw = pw;
        this.nickname = nickname;
    }

    @Override
    public void run() {
        try {
            while(true) {
                if(!clientSocket.isConnected() && clientSocket.isClosed()) {
                    break;
                }

                System.out.print("You> ");

                String inputData = br.readLine();
                if(inputData == null) {
                    break;
                }
                if(inputData.equals("exit")) {
                    break;
                }

                pw.println(nickname+"> "+inputData);      // 보낼 내용을 읽어와서 전송
                pw.flush();                 // 버퍼에 있던 데이터를 전송하고 버퍼를 비운다
            }
            clientSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}