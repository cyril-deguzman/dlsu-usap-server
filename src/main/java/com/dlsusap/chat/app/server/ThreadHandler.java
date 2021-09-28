package com.dlsusap.chat.app.server;

import com.dlsusap.chat.app.model.Message;
import com.dlsusap.chat.app.model.User;

import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadHandler extends Thread {

    private Server server;
    private Socket clientSocket;
    private User client;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    public ThreadHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            Message msg = new Message();
            boolean isLoggedIn = false;
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            is = new ObjectInputStream(clientSocket.getInputStream());

            while(true) {
                if(!isLoggedIn) {
                    msg.setText("RequestID");
                    os.writeObject(msg);

                    while((msg = (Message)is.readObject()) != null) {
                        if(msg.getText().equalsIgnoreCase("ID")) {
                            client = new User(clientSocket, msg.getSenderName(), msg.getSenderID());
                        }
                        isLoggedIn = true;

                        msg.setSenderName("[Server]");
                        msg.setText(client.username + " logged in.\n");

                        List<ThreadHandler> handlerList = server.getHandlerList();
                        for(ThreadHandler h : handlerList)
                            h.sendMessage(msg);

                        server.log(client.username + ": logged in.");
                        break;
                    }
                }

                if(isLoggedIn)
                    while((msg = (Message)is.readObject()) != null) {
                        List<ThreadHandler> handlerList = server.getHandlerList();

                        loop:for(ThreadHandler h : handlerList)
                            switch(msg.getText()) {
                                case "file":
                                    Message temp = new Message();
                                    temp.setText("fileInfo");
                                    temp.setFileName(msg.getFileName());
                                    temp.setSenderName(msg.getSenderName());
                                    h.sendMessage(temp);
                                    break;
                                case "reqFile":
                                    Message fileToSend = server.getFileList().get(msg.getFileName());
                                    if(fileToSend != null)
                                        os.writeObject(fileToSend);
                                    else {
                                        Message error = new Message();
                                        error.setText("The file you requested isn't available.\n");
                                        error.setSenderName("[Server]");
                                        os.writeObject(error);
                                    }
                                    break loop;
                                default:
                                    h.sendMessage(msg);

                            }

                        switch(msg.getText()) {
                            case "img":
                                server.log(msg.getSenderName() + ": sent an image");
                                break;
                            case "file":
                                server.log(msg.getSenderName() +": sent a file (" + msg.getFileName() +")");
                                server.getFileList().put(msg.getFileName(), msg);
                                break;
                            case "reqFile":
                                server.log(msg.getSenderName() +": requested a file (" + msg.getFileName() +")");
                                break;
                            default:
                                if(!msg.getSenderName().equalsIgnoreCase("[Server]"))
                                    server.log(msg.getSenderName() + ": sent a message");
                        }
                    }
            }

        } catch (Exception e) {
            try {
                server.log(client.username + ": with " + clientSocket + " logged out");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            notifyLogOut();
            e.printStackTrace();
        }
    }

    private void notifyLogOut() {
        server.getHandlerList().remove(this);
        Message logOut = new Message();
        logOut.setSenderName("[Server]");
        logOut.setText(client.username + " logged out\n");

        List<ThreadHandler> handlerList = server.getHandlerList();
        for(ThreadHandler h : handlerList) {
            try {
                h.sendMessage(logOut);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void sendMessage(Message msg) throws Exception {
        os.writeObject(msg);
    }

}
