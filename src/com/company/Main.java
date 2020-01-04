package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ClientSend cs = new ClientSend(1);
        ServerSend sc = new ServerSend();
        sc.start();
        cs.sendReq();
        int iterator = 2;
        while (true) {
            //System.out.println("here while");
            if (sc.serverRestart) {
                sc.interrupt();
                sc = new ServerSend();
                sc.start();
                iterator++;
            }
        }

    }
}
