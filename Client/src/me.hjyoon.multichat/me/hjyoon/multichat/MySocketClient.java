package me.hjyoon.multichat;

import java.io.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MySocketClient {
    private Socket clientSocket;            // 클라이언트 소켓
    private BufferedReader br_from_socket;  // 소켓으로부터 전달받은 메시지를 읽어들이기 위함
    private BufferedReader br_from_user;    // 사용자로 부터 데이터를 입력받기 위함
    private PrintWriter pw;                 // 소켓으로 메시지를 보내기 위함
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
            nickname = br_from_user.readLine(); // 닉네임 설정
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