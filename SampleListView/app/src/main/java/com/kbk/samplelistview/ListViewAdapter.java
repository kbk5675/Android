package com.kbk.samplelistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    
    private ArrayList<ListViewItem> arrayList = new ArrayList<>();

    public ListViewAdapter() {

    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listviewitem, viewGroup, false);
        }
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView1);
        TextView textView1 = view.findViewById(R.id.textView2);
        CheckBox checkBox = view.findViewById(R.id.checkBox);

        ListViewItem listViewItem = arrayList.get(i);

        imageView.setImageDrawable(listViewItem.getDrawable());
        textView.setText(listViewItem.getText1());
        textView1.setText(listViewItem.getText2());
        checkBox.setChecked(listViewItem.getCheckBox(;

        return view;
    }

    public void addItem(Drawable drawable, String text1, String text2) {
        ListViewItem listViewItem = new ListViewItem();
        listViewItem.setDrawable(drawable);
        listViewItem.setText1(text1);
        listViewItem.setText2(text2);

        arrayList.add(listViewItem);
    }
    public void removeItem(int i) {
        ListViewItem listViewItem = new ListViewItem();
        listViewItem.setCheckBox();
        arrayList.remove(i);
    }
}
