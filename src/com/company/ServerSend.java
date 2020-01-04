package com.company;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// todo implement check of the req from client
// todo implement multi threading
// todo implement 5 point
public class ServerSend extends Thread {
    int portGen = 9876;
    public boolean serverRestart = true;
    public static boolean readyToDelete = false;
    private String address = "localHost";


    public static void main(String[] args) {
                ServerSend server = new ServerSend();
                server.start();

    }


    int comPort;
    DatagramSocket socket;
    boolean running;
    protected byte[] buf = new byte[256];

    public ServerSend() {
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
            System.out.println(packet.getPort() + "  packet port");
            comPort = packet.getPort();
            String clientReq = new String(packet.getData(), 0, packet.getLength());
            System.out.println(clientReq + " data");
            Pattern pattern = Pattern.compile("file req:[0-9].*");
            Matcher validate = pattern.matcher(clientReq);
            if(validate.matches()){


            new Thread(()->{
                File file = new File("/Users/vsevoloddoroshenko/Desktop/ready_skj_proj/src/resources/data.txt");
                System.out.println(file.length());
                Config conf = new Config(file.length(), "data.txt");
                System.out.println(packet.getPort());
                DatagramPacket fileConf = null;
                try {
                    fileConf = new DatagramPacket(conf.toByteArr(), conf.toByteArr().length, InetAddress.getByName(address), packet.getPort() + 1);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                try {
                    socket.send(fileConf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("server: sended file config");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int port = packet.getPort();
                System.out.println(socket.getLocalAddress().getHostAddress());
                String ip = socket.getLocalAddress().toString();
                FileEvent event = getFileEvent(file, port, socket.getLocalAddress().getHostAddress());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream os = null;
                try {
                    os = new ObjectOutputStream(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.writeObject(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] data = outputStream.toByteArray();
                DatagramPacket sendPacket = null;
                try {
                    sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(address), 9878);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                try {
                    socket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }).start();

            }
            this.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileEvent getFileEvent(File inFile, int port, String ip ) {
        FileEvent fileEvent = new FileEvent();
        String fileName = inFile.getAbsolutePath().substring(inFile.getAbsolutePath().lastIndexOf("/") + 1, inFile.getAbsolutePath().length());
        String path = inFile.getAbsolutePath().substring(0, inFile.getAbsolutePath().lastIndexOf("/") + 1);
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = dateFormat.format(date);
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
        String strTime = dateFormat2.format(date);
        fileName = ip + "_" + port + "_" + strDate + "_" + strTime + "_" + fileName;
        String destinationPath = "/Users/vsevoloddoroshenko/Documents/odebrane/";
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
