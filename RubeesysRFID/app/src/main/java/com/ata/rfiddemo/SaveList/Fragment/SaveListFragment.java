package com.ata.rfiddemo.SaveList.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.rfiddemo.R;
import com.ata.rfiddemo.SaveList.Adapter.SaveListAdapter;
import com.ata.rfiddemo.SaveList.SaveListActivity;
import com.ata.rfiddemo.Util.DataSet.FileItem;
import com.ata.rfiddemo.Util.DialogHelper;
import com.ata.rfiddemo.Util.FileSaveHelper;
import com.ata.rfiddemo.Util.RealmSet.FileData;
import com.ata.rfiddemo.Util.RealmSet.TagData;
import com.ata.rfiddemo.Util.Service.ModelService;

import java.util.ArrayList;

import io.realm.RealmList;

public class SaveListFragment extends Fragment implements View.OnClickListener, SaveListAdapter.OnItemClickListener, CompoundButton.OnCheckedChangeListener, SaveListAdapter.OnCheckedListener, SaveListAdapter.OnClickedListener {

    private SaveListActivity saveListActivity;

    private TextView textView_filenameCountDate;
    private CheckBox checkBox_check;
    private RecyclerView recyclerView_saveList;
    private Button button_download;
    private Button button_delete;

    private ArrayList<FileItem> fileItemList;
    private ArrayList<FileData> checkedList;
    private SaveListAdapter saveListAdapter;

    private DialogHelper dialogHelper;

    private boolean allCheck;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save_list, container, false);

        saveListActivity = (SaveListActivity)getActivity();

        textView_filenameCountDate = view.findViewById(R.id.textView_filenameCountDate);
        checkBox_check             = view.findViewById(R.id.checkBox_check);
        recyclerView_saveList      = view.findViewById(R.id.recyclerView_saveList);
        button_download            = view.findViewById(R.id.button_download);
        button_delete              = view.findViewById(R.id.button_delete);

        fileItemList    = new ArrayList<FileItem>();
        checkedList     = new ArrayList<FileData>();
        saveListAdapter = new SaveListAdapter(fileItemList, this, this);

        recyclerView_saveList.setLayoutManager(new LinearLayoutManager(saveListActivity));
        recyclerView_saveList.setAdapter(saveListAdapter);

        checkBox_check.setOnCheckedChangeListener(this);
        button_download.setOnClickListener(this);
        button_delete.setOnClickListener(this);
        saveListAdapter.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        allCheck = false;
        checkBox_check.setChecked(false);

        saveListActivity.setToolbar(getResources().getString(R.string.save_list));
        textView_filenameCountDate.setText(Html.fromHtml(getString(R.string.filename_count_date), Html.FROM_HTML_MODE_LEGACY));

        getData();
    }

    private void getData() {
        fileItemList.clear();
        checkedList.clear();

        ModelService.getInstance().getFileDataList().forEach(fileData -> {
            ArrayList<TagData> tagDataList = new ArrayList<TagData>(fileData.getTagDataList());

            fileItemList.add(new FileItem(fileData.getFileNumber(), fileData.getFileName(), fileData.getCreateDate(), tagDataList, false));
        });

        saveListAdapter.notifyDataSetChanged();
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
        switch (view.getId()) {
            case R.id.button_download:
                // 다운로드
                getCheckedList();

                if (checkedList.size() == 0) {
                    Toast.makeText(saveListActivity, getResources().getString(R.string.download_target_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogHelper = new DialogHelper(downloadClickListener);
                dialogHelper.DialogShow(saveListActivity, getResources().getString(R.string.download), String.format(getResources().getString(R.string.select_download_message), checkedList.size()));
                break;

            case R.id.button_delete:
                // 삭제
                getCheckedList();

                if (checkedList.size() == 0) {
                    Toast.makeText(saveListActivity, getResources().getString(R.string.delete_target_empty), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialogHelper = new DialogHelper(deleteClickListener);
                dialogHelper.DialogShow(saveListActivity, getResources().getString(R.string.delete), String.format(getResources().getString(R.string.delete_message), checkedList.size()));
                break;
        }
    }

    // 다운로드
    DialogInterface.OnClickListener downloadClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // 전체 다운로드
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                int check = FileSaveHelper.fileListSave(checkedList);

                if (check == 0) {
                    Toast.makeText(saveListActivity, getResources().getString(R.string.download_complete_message), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(saveListActivity, String.format(getResources().getString(R.string.download_incomplete_count_message), check), Toast.LENGTH_SHORT).show();
                }
            }

            dialogHelper.DialogDismiss();
        }
    };

    // 삭제
    DialogInterface.OnClickListener deleteClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ModelService.getInstance().deleteFileData(checkedList);

            getData();

            if (fileItemList.size() == 0) {
                checkBox_check.setChecked(false);
            }

            dialogHelper.DialogDismiss();
        }
    };

    private void getCheckedList() {
        fileItemList.stream().filter(FileItem::isCheck).forEach(fileItem -> {
            FileData fileData = new FileData();

            fileData.setFileNumber(fileItem.getFileNumber());
            fileData.setFileName(fileItem.getFileName());
            fileData.setCreateDate(fileItem.getCreateDate());

            RealmList<TagData> tagDataList = new RealmList<TagData>();

            tagDataList.addAll(fileItem.getTagDataList());

            fileData.setTagDataList(tagDataList);

            checkedList.add(fileData);
        });
    }

    @Override
    public void onItemClick(View v, int pos) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("fileNumber", fileItemList.get(pos).getFileNumber());
        saveListActivity.replaceFragment("saveListDetail", bundle);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (allCheck == checked) {
            return;
        }

        allCheck = !allCheck;

        fileItemList.forEach(set -> set.setCheck(allCheck));
        saveListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedListener(boolean flag, int pos) {
        fileItemList.get(pos).setCheck(flag);
    }

    @Override
    public void OnClickedListener() {
        FileItem fileItem = fileItemList.stream().filter(item -> !item.isCheck()).findFirst().orElse(null);
        allCheck = (fileItem == null);

        checkBox_check.setChecked(allCheck);
    }
}
