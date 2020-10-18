import java.io.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.net.*;
import java.net.http.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

class Util {
    static public String time_now() {
        return "("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"))+")";
    }
}

class MyServerThread extends Thread {
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

class MySocketServer {
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

public class Main {
    private final static String DEFAULT_PORT_NUMBER = "8981";

    public static void main(String args[]) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int port = -1;

        try {
            System.out.print("set port number["+DEFAULT_PORT_NUMBER+"] : ");
            String port_tmp = br.readLine();
            if(port_tmp.equals("")) {
                port_tmp = DEFAULT_PORT_NUMBER;
            }
            port = Integer.parseInt(port_tmp);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MySocketServer mss = new MySocketServer(port);
        mss.init();
        mss.run();
    }
}