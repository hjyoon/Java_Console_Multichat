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

    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����
    public void broadcast(String receivedData) {
        System.out.println(receivedData);   // ������ ���

        // ��� Ŭ���̾�Ʈ���� �޽��� ��ε�ĳ����
        synchronized (al) {
            for (int i=0; i<al.size(); i++) {
                Socket client = al.get(i);
                if(client == clientSocket) {
                    continue;
                }
                try {
                    PrintWriter pw = new PrintWriter(client.getOutputStream()); // Ŭ���̾�Ʈ�� �����͸� ���� �غ�
                    pw.println(receivedData);      // ���� ������ �о�ͼ� ����
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
            // ���� �޼��� ���
            broadcast(Util.time_now()+" "+accept_message());    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����

            while(true) {
                // Ŭ���̾�Ʈ ������ �������� ������°� �ƴ� ���
                if(!clientSocket.isConnected() && clientSocket.isClosed()) {
                    clientSocket.close();
                    al.remove(clientSocket);
                    broadcast(Util.time_now()+" "+disconnect_message());
                    break;
                }

                // Ŭ���̾�Ʈ�� ���� �����͸� �о���� ����
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String receivedData = br.readLine();

                // Ŭ���̾�Ʈ�κ��� �޽����� ������ �ƴ� ���
                if(receivedData == null) {
                    clientSocket.close();
                    al.remove(clientSocket);
                    broadcast(Util.time_now()+" "+disconnect_message());
                    break;
                }

                // Ŭ���̾�Ʈ�� ���� �޼��� ���
                broadcast(Util.time_now()+" "+receivedData);    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����
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