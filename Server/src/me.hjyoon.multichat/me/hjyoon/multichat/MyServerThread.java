package me.hjyoon.multichat;

import java.io.*;
import java.util.*;
import java.net.*;

import me.hjyoon.multichat.*;

public class MyServerThread extends Thread {
    private Socket clientSocket;
    private ArrayList<Socket> al;
    private String nickname;

    MyServerThread(Socket clientSocket, ArrayList<Socket> al, String nickname) {
        this.clientSocket = clientSocket;
        this.al = al;
        this.nickname = nickname;
    }

    // 클라이언트 및 서버에 브로드캐스팅
    public void broadcast(String receivedData) {
        System.out.println(receivedData);   // 서버에 출력

        // 모든 클라이언트에게 메시지 브로드캐스팅
        synchronized (al) {
            for (int i=0; i<al.size(); i++) {
                Socket client = al.get(i);
                if(client == clientSocket) {
                    continue;
                }
                try {
                    PrintWriter pw = new PrintWriter(client.getOutputStream()); // 클라이언트로 데이터를 보낼 준비
                    pw.println(receivedData);      // 보낼 내용을 읽어와서 전송
                    pw.flush();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    al.remove(i);
                }
            }
        }
    }

    public String accept_message() {
        return clientSocket+" \""+nickname+"\" has accepted "+"(total clients : "+al.size()+")";
    }

    public String disconnect_message() {
        return clientSocket+" \""+nickname+"\" has disconnected "+"(total clients : "+al.size()+")";
    }

    @Override
    public void run() {
        try {
            // 입장 메세지 출력
            broadcast(Util.time_now()+" "+accept_message());    // 클라이언트 및 서버에 브로드캐스팅

            while(true) {
                // 클라이언트 소켓이 닫혔더나 연결사태가 아닌 경우
                if(!clientSocket.isConnected() && clientSocket.isClosed()) {
                    clientSocket.close();
                    al.remove(clientSocket);
                    broadcast(Util.time_now()+" "+disconnect_message());
                    break;
                }

                // 클라이언트로 부터 데이터를 읽어오기 위함
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String receivedData = br.readLine();

                // 클라이언트로부터 메시지가 정상이 아닌 경우
                if(receivedData == null) {
                    clientSocket.close();
                    al.remove(clientSocket);
                    broadcast(Util.time_now()+" "+disconnect_message());
                    break;
                }

                // 클라이언트가 보낸 메세지 출력
                broadcast(Util.time_now()+" "+receivedData);    // 클라이언트 및 서버에 브로드캐스팅
            }
        }
        catch (SocketException e) {
            al.remove(clientSocket);
            broadcast(Util.time_now()+" "+disconnect_message());
        }
        catch (Exception e) {
            e.printStackTrace();
            al.remove(clientSocket);
            broadcast(Util.time_now()+" "+disconnect_message());
        }
    }
}