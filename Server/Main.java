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

class MySocketServer {
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