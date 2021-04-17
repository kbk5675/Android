package com.example.menuactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //menu에서 main으로 넘어올때 자동으로 호출.
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == 101) {
            if(data != null) {
                String name = data.getStringExtra("이름");
                if(name != null) {
                    Toast.makeText(this, "받은 데이터 : " + name, Toast.LENGTH_LONG).show();
                 }
            }
        }
    }
}