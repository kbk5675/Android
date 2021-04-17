package com.ata.rfiddemo.SaveList.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.rfiddemo.R;
import com.ata.rfiddemo.SaveList.Adapter.SaveListDetailAdapter;
import com.ata.rfiddemo.SaveList.SaveListActivity;
import com.ata.rfiddemo.Util.DialogHelper;
import com.ata.rfiddemo.Util.FileSaveHelper;
import com.ata.rfiddemo.Util.RealmSet.FileData;
import com.ata.rfiddemo.Util.RealmSet.TagData;
import com.ata.rfiddemo.Util.Service.ModelService;

import java.util.ArrayList;

public class SaveListDetailFragment extends Fragment implements View.OnClickListener {

    private SaveListActivity saveListActivity;

    private TextView textView_total;
    private RecyclerView recyclerView_saveListDetail;
    private Button button_download;

    private Long fileNumber;
    private FileData fileData;
    private ArrayList<TagData> tagDataList;
    private SaveListDetailAdapter saveListDetailAdapter;

    private DialogHelper dialogHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_list_detail, container, false);

        saveListActivity = (SaveListActivity)getActivity();

        textView_total              = view.findViewById(R.id.textView_total);
        recyclerView_saveListDetail = view.findViewById(R.id.recyclerView_saveListDetail);
        button_download             = view.findViewById(R.id.button_download);

        fileData              = new FileData();
        tagDataList           = new ArrayList<TagData>();
        saveListDetailAdapter = new SaveListDetailAdapter(tagDataList);

        // recyclerView
        recyclerView_saveListDetail.setLayoutManager(new LinearLayoutManager(saveListActivity));
        recyclerView_saveListDetail.setAdapter(saveListDetailAdapter);

        button_download.setOnClickListener(this);

        return view;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onStart() {
        super.onStart();

        tagDataList.clear();

        saveListActivity.setToolbar(getResources().getString(R.string.save_list_detail));

        Bundle bundle = getArguments();

        if (bundle != null) {
            fileNumber = (Long) bundle.getSerializable("fileNumber");
        }

        if (fileNumber != null) {
            fileData = ModelService.getInstance().getFileData(fileNumber);
        }

        if (fileData != null) {
            tagDataList.addAll(fileData.getTagDataList());
        }

        textView_total.setText(String.format(getResources().getString(R.string.total_count), tagDataList.size()));
    }

    @Override
    public void onDestroyView() {
        if (dialogHelper != null) {
            dialogHelper.DialogDismiss();
        }

        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_download) {
            dialogHelper = new DialogHelper(downloadClickListener);
            dialogHelper.DialogShow(saveListActivity, getResources().getString(R.string.download), getResources().getString(R.string.download_message));
        }
    }

    DialogInterface.OnClickListener downloadClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                boolean saveCheck = FileSaveHelper.fileSave(fileData.getFileName(), tagDataList);

                if (saveCheck) {
                    Toast.makeText(saveListActivity, getResources().getString(R.string.download_complete_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(saveListActivity, getResources().getString(R.string.download_incomplete_message), Toast.LENGTH_SHORT).show();
                }
            }

            dialogHelper.DialogDismiss();
        }
    };
}
