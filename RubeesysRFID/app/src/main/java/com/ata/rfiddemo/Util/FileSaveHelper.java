package com.ata.rfiddemo.Util;

import android.os.Environment;

import com.ata.rfiddemo.Util.RealmSet.FileData;
import com.ata.rfiddemo.Util.RealmSet.TagData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileSaveHelper {

    private static String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static boolean fileSave(String fileName, ArrayList<TagData> tagDataList) {
        String fileNamePath = "/" + fileName + ".csv";

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter( filePath + fileNamePath, false));

            bufferedWriter.write("No,EPC,Company,Item,Serial");
            bufferedWriter.newLine();

            int index = 1;

            for (TagData tagData : tagDataList) {
                bufferedWriter.write(index + ",'" + tagData.getEpc() + ",'" + tagData.getCount() + ",'" + tagData.getItemReference() + ",'" + tagData.getSerialNumber());
                bufferedWriter.newLine();
                index++;
            }

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static int fileListSave(ArrayList<FileData> fileDataList) {
        int count = 0;

        for (FileData fileData : fileDataList) {
            ArrayList<TagData> tagDataList = new ArrayList<TagData>(fileData.getTagDataList());

            if (!fileSave(fileData.getFileName(), tagDataList)) {
                count++;
            }
        }

        return count;
    }
}
