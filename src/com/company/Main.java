package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ClientSend cs = new ClientSend(1);
        ServerSend ss = new ServerSend();
        ss.start();
        cs.sendReq();
    }
}
