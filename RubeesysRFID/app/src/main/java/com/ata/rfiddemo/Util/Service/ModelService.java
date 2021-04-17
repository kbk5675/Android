package com.ata.rfiddemo.Util.Service;

import com.ata.rfiddemo.Util.RealmSet.FileData;
import com.ata.rfiddemo.Util.RealmSet.TagData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ModelService {

    private static ModelService _instance = new ModelService();
    private final Realm realm;

    public static ModelService getInstance() {
        return _instance;
    }

    private ModelService() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (!realm.isClosed()) {
            realm.close();
        }
    }

    public boolean checkFileName(String fileName) {
        FileData fileData = realm.where(FileData.class)
                                .equalTo("fileName", fileName).findFirst();

        if (fileData != null) {
            return true;
        }

        return false;
    }

    public void saveFileData(String fileName, ArrayList<TagData> tagDataList) {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        Date currentTime           = new Date();
        String time                = formatter.format(currentTime);
        long currentNumber         = 0;

        if (realm.where(FileData.class).findAll().size() > 0) {
            currentNumber = realm.where(FileData.class).max("fileNumber").longValue();
        }

        realm.beginTransaction();

        FileData fileData = realm.createObject(FileData.class, currentNumber + 1);

        fileData.setFileName(fileName);
        fileData.setCreateDate(time);

        for (TagData tagData : tagDataList) {
            fileData.getTagDataList().add(tagData);
        }

        realm.commitTransaction();
    }

    public void deleteFileData(ArrayList<FileData> fileDataList) {
        realm.beginTransaction();

        for (FileData fileData : fileDataList) {
            realm.where(FileData.class).equalTo("fileNumber", fileData.getFileNumber()).findAll().deleteAllFromRealm();
        }

        realm.commitTransaction();
    }

    public RealmResults<FileData> getFileDataList() {
        return realm.where(FileData.class).findAll().sort("createDate", Sort.DESCENDING);
    }

    public FileData getFileData(long fileNumber) {
        return realm.where(FileData.class).equalTo("fileNumber", fileNumber).findFirst();
    }
}
