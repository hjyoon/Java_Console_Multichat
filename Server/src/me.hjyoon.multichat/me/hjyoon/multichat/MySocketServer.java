package me.hjyoon.multichat;

import java.io.*;
import java.util.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MySocketServer {
    private ServerSocket serverSocket;      // 서버 소켓
    private BufferedReader br_from_socket;  // 소켓으로부터 전달받은 메시지를 읽어들이기 위함
    private PrintWriter pw;                 // 클라이언트로 메시지를 보냄
    private int port;
    private ArrayList<Socket> al;

    public MySocketServer(int port) {
        this.port = port;
    }

    public void init() {
        al = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port); // 현재 아이피로 8981포트를 사용하여 서버 오픈
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println("Server address is "+serverSocket.getInetAddress().getLocalHost().getHostAddress());
            System.out.println("Server port is "+serverSocket.getLocalPort());
            System.out.println(Util.time_now()+" Server is ready");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                al.add(clientSocket);

                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String nickname = br.readLine();

                MyServerThread server_thread = new MyServerThread(clientSocket, al, nickname);
                server_thread.start();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}