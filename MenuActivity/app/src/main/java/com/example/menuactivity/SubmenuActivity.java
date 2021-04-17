package com.example.menuactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SubmenuActivity extends AppCompatActivity {

    ConstraintLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submenu);

        container = findViewById(R.id.container);

        Button button = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService((Context.LAYOUT_INFLATER_SERVICE));
                inflater.inflate(R.layout.sub1, container, true);
                CheckBox checkBox = container.findViewById(R.id.checkBox);
                checkBox.setText("로딩되었어요");
            }
        });

    }
}