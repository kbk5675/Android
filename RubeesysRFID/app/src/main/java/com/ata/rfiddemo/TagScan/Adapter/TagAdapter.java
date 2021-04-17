package com.ata.rfiddemo.TagScan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.rfiddemo.R;
import com.ata.rfiddemo.Util.RealmSet.TagData;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<TagData> tagDataList;

    public TagAdapter(ArrayList<TagData> tagDataList) {
        this.tagDataList = tagDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;


        if (viewType == 0) {
            View view = inflater.inflate(R.layout.recycler_tag_item, parent, false);
            return new ViewHolderDefault(view);
        }

        View view = inflater.inflate(R.layout.recycler_tag_item_other, parent, false);
        return new ViewHolderOther(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TagData tagData = tagDataList.get(position);

        if (holder instanceof ViewHolderDefault) {
            ((ViewHolderDefault)holder).textView_epc.setText(tagData.getEpc());
            ((ViewHolderDefault)holder).textView_count.setText(tagData.getCount());
            ((ViewHolderDefault)holder).textView_itemReference.setText(tagData.getItemReference());
            ((ViewHolderDefault)holder).textView_serialNumber.setText(tagData.getSerialNumber());
        } else {
            ((ViewHolderOther)holder).textView_epc.setText(tagData.getEpc());
            ((ViewHolderOther)holder).textView_count.setText(tagData.getCount());
            ((ViewHolderOther)holder).textView_itemReference.setText(tagData.getItemReference());
            ((ViewHolderOther)holder).textView_serialNumber.setText(tagData.getSerialNumber());
        }
    }

    @Override
    public int getItemCount() {
        if (tagDataList != null) {
            return tagDataList.size();
        }

        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (tagDataList.get(position).isCorrect()) {
            // ViewHolderDefault
            return 0;
        } else {
            // ViewHolderOther
            return 1;
        }
    }

    public class ViewHolderDefault extends RecyclerView.ViewHolder {

        TextView textView_epc;
        TextView textView_count;
        TextView textView_itemReference;
        TextView textView_serialNumber;

        public ViewHolderDefault(@NonNull View itemView) {
            super(itemView);

            textView_epc           = itemView.findViewById(R.id.textView_epc);
            textView_count = itemView.findViewById(R.id.textView_count);
            textView_itemReference = itemView.findViewById(R.id.textView_itemReference);
            textView_serialNumber  = itemView.findViewById(R.id.textView_serialNumber);
        }
    }

    public class ViewHolderOther extends RecyclerView.ViewHolder {

        TextView textView_epc;
        TextView textView_count;
        TextView textView_itemReference;
        TextView textView_serialNumber;

        public ViewHolderOther(@NonNull View itemView) {
            super(itemView);

            textView_epc           = itemView.findViewById(R.id.textView_epc);
            textView_count = itemView.findViewById(R.id.textView_count);
            textView_itemReference = itemView.findViewById(R.id.textView_itemReference);
            textView_serialNumber  = itemView.findViewById(R.id.textView_serialNumber);

            /*
            itemView.setOnClickListener(new View.onClickListener() {

                @Override
                public void onClick(View view){

                }

            });
            */


        }
    }
}
