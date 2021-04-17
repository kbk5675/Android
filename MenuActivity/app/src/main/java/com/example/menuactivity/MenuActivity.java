package com.example.menuactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button  button = findViewById(R.id.button2); //확인
        Button button1 = findViewById(R.id.button3); //닫기

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("이름", "김보근 파이팅");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}