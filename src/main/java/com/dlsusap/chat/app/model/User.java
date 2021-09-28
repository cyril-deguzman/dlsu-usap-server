package com.dlsusap.chat.app.model;

import java.net.Socket;

public class User {
    public Socket socket;
    public String username;
    public int id;

    public User(Socket socket, String username, int id) {
        this.socket = socket;
        this.username = username;
        this.id = id;
    }

    public User getUser() {
        return this;
    }
}
