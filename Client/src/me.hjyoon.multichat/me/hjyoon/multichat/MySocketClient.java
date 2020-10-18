package me.hjyoon.multichat;

import java.io.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MySocketClient {
    private Socket clientSocket;            // Ŭ���̾�Ʈ ����
    private BufferedReader br_from_socket;  // �������κ��� ���޹��� �޽����� �о���̱� ����
    private BufferedReader br_from_user;    // ����ڷ� ���� �����͸� �Է¹ޱ� ����
    private PrintWriter pw;                 // �������� �޽����� ������ ����
    private String ip;
    private int port;
    private String nickname;

    public MySocketClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void init() {
        br_from_user = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("nickname : ");
            nickname = br_from_user.readLine(); // �г��� ����
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Trying to connect...");
            clientSocket = new Socket(ip, port);
            //clientSocket = new Socket();
            //clientSocket.connect(new InetSocketAddress(ip, port), 5);
            //clientSocket.setSoTimeout(1000);
            System.out.println(Util.time_now()+" "+clientSocket+" \""+nickname+"\" has accepted");
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            br_from_socket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            pw = new PrintWriter(clientSocket.getOutputStream());
            pw.println(nickname);
            pw.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        MySendThread send_thread = new MySendThread(clientSocket, br_from_user, pw, nickname);
        MyReceiveThread rec_thread = new MyReceiveThread(clientSocket, br_from_socket);
        send_thread.start();
        rec_thread.start();
    }
}