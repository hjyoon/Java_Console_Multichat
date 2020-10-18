package me.hjyoon.multichat;

import java.io.*;
import java.util.*;

import me.hjyoon.multichat.*;

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