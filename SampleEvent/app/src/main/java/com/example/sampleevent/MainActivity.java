package com.example.sampleevent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.slice.SliceItem;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Month;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    GestureDetector detector; //제스처 디텍터 객체 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView  = findViewById(R.id.textView);

        View view = findViewById(R.id.view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                float curX = motionEvent.getX();
                float curY = motionEvent.getY();

                if (action == MotionEvent.ACTION_DOWN) {
                    println("손가락 눌림 : " + curX + ", " + curY);
                }


                return false;
            }
        });

        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                println("onDown() 호출됨");

                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                println("onShowPress() 호출됨");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                println("onSingleTapUp() 호출됨");

                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v1) {
                println("onScroll() 호출됨 : " + v + ", " + v1);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                println("onLongPress() 호출됨");
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v1) {
                println("onFling() 호출됨 : " + v + ", " + v1);
                return true;
            }
        });

        View view2 = findViewById(R.id.view2);

        view2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });

    }

    public void println(String data) {
        textView.append(data + "\n");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "시스템 [Back] 버튼이 눌렸습니다.", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return false;
    }
}