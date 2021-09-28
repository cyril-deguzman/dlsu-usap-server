package com.dlsusap.chat.app.server;

import com.dlsusap.chat.app.model.Message;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class Server extends Thread {

    private final int serverPort;
    private ArrayList<ThreadHandler> handlerList = new ArrayList<>();
    private HashMap<String, Message> fileList = new HashMap<>();

    public String logName;

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ThreadHandler> getHandlerList() {
        return handlerList;
    }

    public HashMap<String, Message> getFileList() {
        return fileList;
    }

    @Override
    public void run() {
        try
        {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            log("[Server] Successfully booted at port [" + serverPort + "]");
            while(true) {
                System.out.println("Listening for connections...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from: " + clientSocket);
                ThreadHandler handler = new ThreadHandler(this, clientSocket);
                handlerList.add(handler);
                handler.start();
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void log(String entry) throws Exception {
        Date time = Calendar.getInstance().getTime();
        DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        String strTime = timeFormat.format(time);
        System.out.println("[" + strTime + "] " + entry);

        try(FileWriter fw = new FileWriter(logName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("[" + strTime + "] " + entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
