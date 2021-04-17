package org.techtown.sampletab;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

import static com.google.android.material.tabs.TabLayout.*;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("통화기록"));
        tabs.addTab(tabs.newTab().setText("스팸기록"));
        tabs.addTab(tabs.newTab().setText("연락처"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                int position = tab.getPosition();

                if (position == 0) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                }else if (position == 1) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                }else if (position == 2) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });

    }
}