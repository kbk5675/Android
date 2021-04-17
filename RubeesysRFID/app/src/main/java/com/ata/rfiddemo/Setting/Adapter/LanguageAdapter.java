package com.ata.rfiddemo.Setting.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ata.rfiddemo.R;

import java.util.ArrayList;

public class LanguageAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> dataList;
    LayoutInflater inflater;

    public LanguageAdapter(Context context, ArrayList<String> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        }

        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.spinner, viewGroup, false);
        }

        String text = dataList.get(i);
        ((TextView)view.findViewById(R.id.spinner_text)).setText(text);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.spinner_dropdown_item, parent, false);
        }

        String text = dataList.get(position);
        ((TextView)convertView.findViewById(R.id.spinner_drop_text)).setText(text);

        return convertView;
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
