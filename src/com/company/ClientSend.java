package com.company;

import jdk.management.resource.internal.inst.DatagramChannelImplRMHooks;

import java.io.*;
import java.net.*;

public class ClientSend {

    private FileEvent fileEvent = null;

    public static void main(String[] args) {
        ClientSend cs = new ClientSend(1);
        cs.sendReq();
    }

    int id;
    DatagramSocket datagramSocket;
    private String destPath = "/Users/vsevoloddoroshenko/Documents/test" + id + "/";
    private InetAddress address;
    private String hostIp = "localHost";
    int comPort = 9876;
    private byte buf[];

    public ClientSend(int id) {
        System.out.println("constructed");
        this.id = id;
        try {
            this.datagramSocket = new DatagramSocket();
            address = InetAddress.getByName(hostIp);

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println(address + " address");
    }

    public String sendReq() {
        DatagramPacket packet = null;
        try {
            String msg = "file req:" + id;
            buf = msg.getBytes();
            packet = new DatagramPacket(buf, buf.length, address, comPort);
            datagramSocket.send(packet);
            System.out.println(packet.toString() + " sended");
            datagramSocket.close();
            DatagramSocket receiveConfigSock = new DatagramSocket();
            byte[] recieveFileBuf = new byte[4096];
            packet = new DatagramPacket(recieveFileBuf, recieveFileBuf.length);
            receiveConfigSock.receive(packet);
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
            String[] dtParse = new String(packet.getData(), 0, packet.getLength()).split(":");
            int dtLength = Integer.parseInt(dtParse[0]);
            byte[] incomingData = new byte[dtLength * 1000];
            DatagramSocket recieveFileSock = new DatagramSocket(9878);
            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                recieveFileSock.receive(incomingPacket);
                System.out.println(incomingPacket.getData().length + " here length");
                byte[] data = incomingPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                fileEvent = (FileEvent) is.readObject();
                if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                    System.out.println("Some issue happened while packing the data @ client side");
                    System.exit(0);
                }
                createAndWriteFile(); // writing the file to hard disk
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void createAndWriteFile() {
        String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();

        if (!new File(fileEvent.getDestinationDirectory()).exists()) {
            new File(fileEvent.getDestinationDirectory()).mkdirs();
        }
        File dstFile = new File(outputFile);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void initializeConnection() {

    }
}

//            packet = new DatagramPacket(buf, buf.length, address, comPort);
//            datagramSocket.receive(packet);
//            System.out.println(packet.toString() + " recieved");
//            int uPort = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
//            System.out.println(uPort + " uPort");
//            byte[] incomingData = new byte[1024];
//            packet = new DatagramPacket(incomingData, incomingData.length);
//            datagramSocket.receive(packet);