package org.techtown.sampleactionbar1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ActionBar actionBar;

    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

        Button button = findViewById(R.id.btnActionbarChange);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( value == 0) {
                    actionBar.setLogo(R.drawable.home);
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME);

                    value = 1;
                } else {
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

                    value = 0;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        View v = menu.findItem(R.id.menu_search).getActionView();
        if(v != null) {
            EditText editText = v.findViewById(R.id.editText);

            if (editText !=null) {
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        Toast.makeText(getApplicationContext(), "입력됨.", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
        return true;
    }
}