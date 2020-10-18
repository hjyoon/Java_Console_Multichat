package me.hjyoon.multichat;

import java.io.*;
import java.time.*;
import java.time.format.*;

import me.hjyoon.multichat.*;

public class Util {
    static public String time_now() {
        return "("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss"))+")";
    }
}