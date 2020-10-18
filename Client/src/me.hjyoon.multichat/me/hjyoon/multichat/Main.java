package me.hjyoon.multichat;

import java.io.*;

import me.hjyoon.multichat.*;

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