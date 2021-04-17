package com.ata.rfiddemo.Util.DataSet;

import com.ata.rfiddemo.Util.RealmSet.TagData;

import java.util.ArrayList;

public class FileItem {

    private long fileNumber;
    private String fileName;
    private String createDate;
    private ArrayList<TagData> tagDataList;
    private boolean check;

    public FileItem(long fileNumber, String fileName, String createDate, ArrayList<TagData> tagDataList, boolean check) {
        this.fileNumber = fileNumber;
        this.fileName = fileName;
        this.createDate = createDate;
        this.tagDataList = tagDataList;
        this.check = check;
    }

    public long getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(long fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public ArrayList<TagData> getTagDataList() {
        return tagDataList;
    }

    public void setTagDataList(ArrayList<TagData> tagDataList) {
        this.tagDataList = tagDataList;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
