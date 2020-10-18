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

class MySendThread extends Thread {
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

class MyReceiveThread extends Thread {
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

class MySocketClient {
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

public class Main {
    private final static String DEFAULT_IP_ADDRESS = "localhost";
    private final static String DEFAULT_PORT_NUMBER = "8981";

    public static void main(String args[]) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String ip_address = null;
        int port = -1;

        try {
            System.out.print("Server's ip address["+DEFAULT_IP_ADDRESS+"] : ");
            ip_address = br.readLine();
            if(ip_address.equals("")) {
                ip_address = "localhost";
            }
            System.out.print("Server's port number["+DEFAULT_PORT_NUMBER+"] : ");
            String port_tmp = br.readLine();
            if(port_tmp.equals("")) {
                port_tmp = DEFAULT_PORT_NUMBER;
            }
            port = Integer.parseInt(port_tmp);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        MySocketClient msc = new MySocketClient(ip_address, port);
        msc.init();
        msc.run();
    }
}