package com.company;

public class Config {
    long fileLength;
    String fileName;

    public Config(long fileLength, String fileName) {
        this.fileLength = fileLength;
        this.fileName = fileName;
    }

    public byte[] toByteArr(){
        byte [] result = null;
        StringBuilder strb = new StringBuilder();
        strb.append(fileLength + ":");
        strb.append(fileName);
        result = strb.toString().getBytes();
        return result;
    }
}
