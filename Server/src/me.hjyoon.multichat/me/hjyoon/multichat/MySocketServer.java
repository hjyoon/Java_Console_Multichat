package me.hjyoon.multichat;

import java.io.*;
import java.util.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MySocketServer {
    private ServerSocket serverSocket;      // ���� ����
    private BufferedReader br_from_socket;  // �������κ��� ���޹��� �޽����� �о���̱� ����
    private PrintWriter pw;                 // Ŭ���̾�Ʈ�� �޽����� ����
    private int port;
    private ArrayList<Socket> al;

    public MySocketServer(int port) {
        this.port = port;
    }

    public void init() {
        al = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port); // ���� �����Ƿ� 8981��Ʈ�� ����Ͽ� ���� ����
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