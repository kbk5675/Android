package com.ata.rfiddemo.Util.RealmSet;

import io.realm.RealmObject;

public class TagData extends RealmObject {

    private String epc;
    private byte filter;
    private String count;
    private String itemReference;
    private int checkDigit;
    private String serialNumber;
    private String gs1Gtin;
    private boolean correct;

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public byte getFilter() {
        return filter;
    }

    public void setFilter(byte filter) {
        this.filter = filter;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getItemReference() {
        return itemReference;
    }

    public void setItemReference(String itemReference) {
        this.itemReference = itemReference;
    }

    public int getCheckDigit() {
        return checkDigit;
    }

    public void setCheckDigit(int checkDigit) {
        this.checkDigit = checkDigit;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getGs1Gtin() {
        return gs1Gtin;
    }

    public void setGs1Gtin(String gs1Gtin) {
        this.gs1Gtin = gs1Gtin;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
