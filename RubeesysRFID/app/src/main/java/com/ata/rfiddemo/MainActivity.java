package com.ata.rfiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ata.rfiddemo.Setting.SettingActivity;
import com.ata.rfiddemo.TagScan.TagScanActivity;
import com.ata.rfiddemo.Util.Service.BcdUtilHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout_tagConfirm;
    private LinearLayout linearLayout_itemCheck;
    private LinearLayout linearLayout_saveList;
    private LinearLayout linearLayout_setting;

    private Button btnWriteBarcode;
    private Button btnWriteRfid;
    private Button btnRfidInventory;
    private Button btnSetting;
    private Button btnExit;
    private Button btnBarcodeInventory;
    private Button btnRead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnWriteBarcode = findViewById(R.id.btnWriteBarcode);
      //  btnWriteRfid = findViewById(R.id.btnWriteRfid);
        btnRfidInventory  = findViewById(R.id.btnRfidInventory);
        btnSetting = findViewById(R.id.btnSetting);
        btnExit = findViewById(R.id.btn_exit);
        btnBarcodeInventory = findViewById(R.id.btnBarcodeInventory) ;
        btnRead = findViewById(R.id.btnRead);

        btnWriteBarcode.setOnClickListener(this);
      //  btnWriteRfid.setOnClickListener(this);
        btnRfidInventory.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnBarcodeInventory.setOnClickListener(this);
        btnRead.setOnClickListener(this);


        String hexString = "1111";

        BcdUtilHelper bcdu = new BcdUtilHelper();
        String hex2Str = bcdu.hexToString(hexString);

        Log.e(" READ DATA  ###########################  ",   ":"   + String.format( "|  %s ",  hex2Str  ));

         hex2Str = bcdu.stringToHex(hex2Str);

        Log.e(" READ DATA  ###########################  ",   ":"   + String.format( "|  %s ",  hex2Str  ));

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnWriteBarcode:
                // write
                startActivity(new Intent(this, WriteMemory.class));

                break;

           // case R.id.btnWriteRfid:
           //     startActivity(new Intent(this, WriteMemoryRfid.class));
           //     break;

            case R.id.btnRead:
                startActivity(new Intent(this, RfidRead.class));
                break;


            case R.id.btnBarcodeInventory:
                startActivity(new Intent(this, BarcodeInventory.class));

                break;

            case R.id.btnRfidInventory:
                // 태그 스캔
                startActivity(new Intent(this, TagScanActivity.class));
                break;

            case R.id.btnSetting:
                // 설정
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.btn_exit:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                moveTaskToBack(true);						// 태스크를 백그라운드로 이동
                finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
                android.os.Process.killProcess(android.os.Process.myPid());
                break;

            /*
            case R.id.linearLayout_itemCheck:
                // 빠른테그
                startActivity(new Intent(this, ItemCheckActivity.class));
                break;

             */


        }

        finish();
    }
}
