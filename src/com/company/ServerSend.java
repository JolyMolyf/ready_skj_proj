package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


// todo implement check of the req from client
// todo implement multi threading
// todo implement 5 point
public class ServerSend extends Thread {
    int portGen = 9876;
    private String address = "localHost";
    public static void main(String[] args) {
        ServerSend sc = new ServerSend();
        sc.start();
    }
    int comPort;
    DatagramSocket socket;
    boolean running;
    protected byte[] buf = new byte[256];

    public ServerSend()  {
        try {
            socket = new DatagramSocket(portGen);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("server running");
        running = true;
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(packet);
            System.out.println(packet.getPort() +  "  packet port");
            comPort = packet.getPort();
            String clientReq = new String(packet.getData(), 0 , packet.getLength());
            System.out.println(clientReq + " data");

            File file = new File("/Users/vsevoloddoroshenko/Downloads/ready_skj_proj/src/resources/data.txt");
            System.out.println(file.length());
            Config conf = new Config(file.length(), "data.txt");
            System.out.println(packet.getPort());
            DatagramPacket fileConf = new DatagramPacket(conf.toByteArr(), conf.toByteArr().length, InetAddress.getByName(address), packet.getPort()+1);
            socket.send(fileConf);
            System.out.println("server: sended file config");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FileEvent event = getFileEvent(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(outputStream);
            os.writeObject(event);
            byte[] data = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(address), 9878);
            socket.send(sendPacket);
            System.out.println("File sent from server");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileEvent getFileEvent(File inFile) {
        FileEvent fileEvent = new FileEvent();
        String fileName = inFile.getAbsolutePath().substring(inFile.getAbsolutePath().lastIndexOf("/") + 1, inFile.getAbsolutePath().length());
        String path = inFile.getAbsolutePath().substring(0, inFile.getAbsolutePath().lastIndexOf("/") + 1);
        String destinationPath = "/Users/vsevoloddoroshenko/Documents/test";
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(inFile.getAbsolutePath());
        File file = new File(inFile.getAbsolutePath());
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                fileEvent.setFileSize(len);
                fileEvent.setFileData(fileBytes);
                fileEvent.setStatus("Success");
            } catch (Exception e) {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }
        return fileEvent;
    }


    }
