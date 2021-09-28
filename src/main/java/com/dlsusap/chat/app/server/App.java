package com.dlsusap.chat.app.server;

import java.io.*;
import java.text.*;
import java.util.*;

public class App {

    public static void main(String[] args) {
        int port;
        Scanner in = new Scanner(System.in);

        System.out.print("Enter server port: ");
        port = in.nextInt();

        Server server = new Server(port);

        server.logName = createLog();
        server.start();
    }

    static String createLog() {
        Date time = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh_mm_ss");
        String strTime = timeFormat.format(time);

        try {
            File log = new File("[" + strTime + "]log.txt");
            if (log.createNewFile()) {
                System.out.println("File created: " + log.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return "[" + strTime + "]log.txt";
    }
}
