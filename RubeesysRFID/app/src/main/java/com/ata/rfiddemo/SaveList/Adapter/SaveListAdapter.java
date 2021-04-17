package com.ata.rfiddemo.SaveList.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.rfiddemo.R;
import com.ata.rfiddemo.Util.DataSet.FileItem;

import java.util.ArrayList;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FileItem> fileItemList;
    private OnItemClickListener mListener = null;
    public OnCheckedListener mCheckedListener;
    public OnClickedListener mClickedListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    public interface OnCheckedListener {
        void onCheckedListener(boolean flag, int pos);
    }

    public interface OnClickedListener {
        void OnClickedListener();
    }

    public SaveListAdapter(ArrayList<FileItem> fileItemList, OnCheckedListener mCheckedListener, OnClickedListener mClickedListener) {
        this.fileItemList     = fileItemList;
        this.mCheckedListener = mCheckedListener;
        this.mClickedListener = mClickedListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_save_list_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileItem fileItem = fileItemList.get(position);

        holder.textView_filename.setText(fileItem.getFileName());
        holder.textView_count_date.setText(String.format(context.getResources().getString(R.string.count_date_value), fileItem.getTagDataList().size(), fileItem.getCreateDate()));

        holder.checkBox_check.setOnCheckedChangeListener(null);
        holder.checkBox_check.setChecked(fileItem.isCheck());

        holder.checkBox_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                fileItem.setCheck(checked);
            }
        });
        holder.checkBox_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickedListener.OnClickedListener();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fileItemList != null) {
            return fileItemList.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_filename;
        TextView textView_count_date;
        CheckBox checkBox_check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_filename   = itemView.findViewById(R.id.textView_filename);
            textView_count_date = itemView.findViewById(R.id.textView_count_date);
            checkBox_check      = itemView.findViewById(R.id.checkBox_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        mListener.onItemClick(view, pos);
                    }
                }
            });
        }
    }
}