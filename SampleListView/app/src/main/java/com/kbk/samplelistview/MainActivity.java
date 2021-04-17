package com.kbk.samplelistview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CheckBox checkBox = findViewById(R.id.checkBox);

        //LIST 항목들 *********************************************************************************************************************
        listView = findViewById(R.id.list);
        adapter = new ListViewAdapter();

        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_launcher_background),"김보근","010 9785 5695");
        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_launcher_background),"김수현","010 2125 4150");
        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_launcher_background),"김대성","010 3231 5695");
        adapter.addItem(ContextCompat.getDrawable(this,R.drawable.ic_launcher_background),"김옥희","010 4223 5695");

        listView.setAdapter(adapter);
        // ********************************************************************************************************************************

        Button btnadd = findViewById(R.id.btnAdd); //추가버튼
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count;
                count = adapter.getCount();

                adapter.addItem(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_launcher_background), "List" + Integer.toString(count+1), "리스트 추가항목");

                adapter.notifyDataSetChanged();
            }
        });

        Button btndel = findViewById(R.id.btnDelete);
        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count, checked;
                count = adapter.getCount();

                if(count > 0) {
                    checked = listView.getCheckedItemPosition();

                    if(checked > -1 && checked < count) {

                        adapter.removeItem(checked);

                    }
                }
            }
        });
    }
}