package com.ata.rfiddemo.Util.RealmSet;

import java.util.Collection;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FileData extends RealmObject {

    @PrimaryKey
    private long fileNumber;
    private String fileName;
    private String createDate;
    private RealmList<TagData> tagDataList;

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

    public Collection<? extends TagData> getTagDataList() {
        return tagDataList;
    }

    public void setTagDataList(RealmList<TagData> tagDataList) {
        this.tagDataList = tagDataList;
    }
}
