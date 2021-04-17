package com.ata.rfiddemo.Util.Manager;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ata.rfiddemo.R;

public class ToolbarManager extends AppCompatActivity {

    public TextView textView_title;
    public ImageView imageView_setting;
    public LinearLayout linearLayout_back;

    public String title;

    public void setToolbar(String title) {

        initToolBar(title);

        imageView_setting.setVisibility(View.GONE);
    }

    public void setToolbar(String title, View.OnClickListener settingClickListener) {

        initToolBar(title);

        imageView_setting.setVisibility(View.VISIBLE);

        imageView_setting.setOnClickListener(settingClickListener);
    }

    private void initToolBar(String title) {
        this.title = title;

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        textView_title    = findViewById(R.id.textView_title);
        linearLayout_back = findViewById(R.id.linearLayout_back);
        imageView_setting = findViewById(R.id.imageView_setting);

        textView_title.setText(title);
        linearLayout_back.setVisibility(View.VISIBLE);

        linearLayout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
