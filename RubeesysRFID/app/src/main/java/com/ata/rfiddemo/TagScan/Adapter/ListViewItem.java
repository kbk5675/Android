package com.ata.rfiddemo.TagScan.Adapter;

public class ListViewItem {

    private String epc ;

    private String asciiPcEpc;
    private String hexPcEpc;

    private String asciiEpc;
    private String hexEpc;


    public String getReadCount() {
        return readCount;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    private String readCount;
    private int no;



    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }


    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getAsciiPcEpc() {
        return asciiPcEpc;
    }

    public void setAsciiPcEpc(String asciiPcEpc) {
        this.asciiPcEpc = asciiPcEpc;
    }


    public String getHexPcEpc() {
        return hexPcEpc;
    }

    public void setHexPcEpc(String hexPcEpc) {
        this.hexPcEpc = hexPcEpc;
    }

    public String getAsciiEpc() {
        return asciiEpc;
    }

    public void setAsciiEpc(String asciiEpc) {
        this.asciiEpc = asciiEpc;
    }

    public String getHexEpc() {
        return hexEpc;
    }

    public void setHexEpc(String hexEpc) {
        this.hexEpc = hexEpc;
    }










}
