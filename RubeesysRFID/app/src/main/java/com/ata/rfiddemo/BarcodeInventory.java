package com.ata.rfiddemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.ata.rfiddemo.TagScan.Adapter.ListViewAdapter;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.Util.StringUtil;
import com.wsmr.lib.dev.barcode.type.BarcodeType;
import com.wsmr.lib.dev.barcode.type.EventType;
import com.wsmr.lib.dev.event.BarcodeEventListener;
import com.wsmr.lib.dev.rfid.param.EpcMatchParam;
import com.wsmr.lib.diagnostics.ATLog;


public class BarcodeInventory extends ToolbarManager implements BarcodeEventListener,   View.OnClickListener {

    String backPageCheck;
    String hexLength;
    String writeEpc;
    String readEpc;
    String pc;



    private int offset;
    private int step;
    int power = 0;

    String epc[] = new String[18];


    private Button btnRead;
    private Button btnClear;
    private EpcMatchParam mEpc;

    private long SKIP_KEY_EVENT_TIME;
    private long mElapsedTick;
    private long mTick;
    private SoundManager soundManager;
    public String asciiFlag = "";
    private static final String TAG = "WriteMemory";
    StringUtil stringUtil = new StringUtil();

    boolean buttonDown = false ;

    private ListView lstBarcodeList;
    private ListViewAdapter adapter;


    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_inventory);

        // ViewBy Id
        this.setViewById();

        // 변수 초기화 START
        backPageCheck = null;
        Intent intent = getIntent();
        backPageCheck = intent.getStringExtra("back");
        writeEpc = null;
        readEpc = null;
        step = 0;

        pc = "";
        hexLength="";
        soundManager        = new SoundManager(this);
        SKIP_KEY_EVENT_TIME = getResources().getInteger(R.integer.skip_key_event_time);

        // 화면설정 초기화 START
        //  Top Toolbar 에서 설정 버튼 보이게
        setToolbar(getResources().getString(R.string.barcode_Inventory), this);

        // Event Listener 설정
        this.setListener();

        lstBarcodeList = (ListView) findViewById(R.id.tag_list);
        adapter =  new ListViewAdapter() ;
        lstBarcodeList.setAdapter(adapter);

        ApplicationClass.getmScanner().setEventListener(this);

    }


    private void setListener()
    {
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnRead:
                buttonDown = true ;
                if (btnRead.getText().equals(getResources().getString(R.string.start)) )
                {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnRead.setText("STOP");
                            btnClear.setEnabled(false);
                        }
                    });

                    barcodeReadData();


                }else if (btnRead.getText().equals("STOP") )
                {
                    if (ApplicationClass.getmScanner().isDecoding()) {
                        ApplicationClass.getmScanner().stopDecode();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnRead.setText(getResources().getString(R.string.start));
                            btnClear.setEnabled(true);
                        }
                    });

                }

                break;


            case R.id.btnClear:
                clear();
                break;
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 트리거 이벤트
        if (keyCode == 138 && event.getRepeatCount() <= 0  ) {

            buttonDown = false ;

                btnRead.setEnabled(false);
                btnClear.setEnabled(false);


            barcodeReadData();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == 138 ) {

            btnRead.setEnabled(true);
            btnClear.setEnabled(true);

            // barcode stop
            if (ApplicationClass.getmScanner().isDecoding()) {
                ApplicationClass.getmScanner().stopDecode();
            }

        }

        return super.onKeyUp(keyCode, event);
    }




    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {


        if (soundManager != null) {
            soundManager.close();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // 실제 메모리에 write하는 function

    public void barcodeReadData()
    {


            if (ApplicationClass.getmScanner().isDecoding()) {
                ApplicationClass.getmScanner().stopDecode();
            } else {
                ApplicationClass.getmScanner().startDecode();
            }

    }



    private void errorShowMessage(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error");
        builder.setMessage(msg);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //
    private void setViewById()
    {
        btnRead        = findViewById(R.id.btnRead);
        btnClear       = findViewById(R.id.btnClear);
    }

    @Override
    public void onStateChanged(EventType eventType) {

    }

    @Override
    public void onDecodeEvent(BarcodeType barcodeType, String barcode) {
        ATLog.e(TAG, "onDecodeEvent(%s, [%s])", barcodeType, barcode);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (  buttonDown == true ) {

                    btnRead.setText(getResources().getString(R.string.start));
                    btnRead.setEnabled(true);
                    btnClear.setEnabled(true);
                }
            }
        });


        if (!"No Read".equals(barcodeType)
                && !"NR".equals(barcode)
        ) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soundPlay();

                    int sameflag = 0;

                    for (int j = 0; j < adapter.getCount(); j++) {

                        if (barcode.equals(adapter.getEpc(j))) {
                            adapter.setReadCount(j, adapter.getReadCount(j) + 1);
                            sameflag = 1;
                            break;
                        }
                    }

                    if (sameflag == 0) {
                        adapter.addItem(barcode, 1, adapter.getCount() + 1                        );
                    }
                    //Log.d("Debug2", Tags);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            soundPlay();
                            adapter.notifyDataSetChanged();
                        }
                    });


                }
            });


        }
    }

    public void soundPlay(){

        String asciiBeef = "";
        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        asciiBeef =  sharedpreferences.getString("beefFlag", "true");
        if ("true".equals(asciiBeef) ) {
            soundManager.playSuccess();
        }else {

        }
    }

    public void clear()
    {
        adapter.clear();
    }

}
