package com.example.doitmission_06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    ProgressBar progressBar;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.editTextNumber);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBar.setProgress(progress);
                editText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "트래킹 시작", Toast.LENGTH_SHORT); //존나 이렇게 해야함.
                toast.show();
            }




            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getApplicationContext(), "트래킹 멈춤", Toast.LENGTH_SHORT); //존나 이렇게 해야함.
                toast.show();
            }
        });

    }  //end of onCreate



} //end of class