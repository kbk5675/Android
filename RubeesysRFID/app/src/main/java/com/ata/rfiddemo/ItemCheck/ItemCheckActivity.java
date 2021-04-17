package com.ata.rfiddemo.ItemCheck;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ata.rfiddemo.MainActivity;
import com.ata.rfiddemo.R;
import com.ata.rfiddemo.Setting.SettingActivity;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.DialogHelper;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.Util.RealmSet.TagData;
import com.wsmr.lib.dev.ATRfidReader;
import com.wsmr.lib.dev.event.RfidReaderEventListener;
import com.wsmr.lib.dev.rfid.type.ActionState;
import com.wsmr.lib.dev.rfid.type.ConnectionState;
import com.wsmr.lib.dev.rfid.type.ResultCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ItemCheckActivity extends ToolbarManager implements RfidReaderEventListener, View.OnClickListener {

    private TextView textView_read_time;
    private TextView textView_total;
    private Button button_clear;
    private ArrayList<TagData> tagDataList;
    private int totalCount ;

    private SoundManager soundManager;

    private int readingType;      // 리딩 타입 전환 (0 : barcode, 1 : rfid)
    private boolean triggerCheck; // keyUp, KeyDown 체크 (전환 시 trigger를 다시 누르도록 제어)

    private DialogHelper dialogHelper;

    private long SKIP_KEY_EVENT_TIME;
    private long mElapsedTick;
    private long mTick;

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    long start = 0;
    long end = 0;
    private  BackgroundThread thread ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_check);

        setToolbar(getResources().getString(R.string.rpid_scan), this);

       // textView_read_time = findViewById(R.id.textView_read_time);
        textView_total     = findViewById(R.id.textView_epc);
        button_clear     = findViewById(R.id.button_clear);
        tagDataList         = new ArrayList<TagData>();

        SKIP_KEY_EVENT_TIME = 1000;

        soundManager = new SoundManager(this);
        //readingType  = 1;
        totalCount = 0;

        button_clear.setOnClickListener(this);
        buttonSetting(true);
        totalCount = 0;

        textViewInit();

        thread = new BackgroundThread();


    }

    @Override
    public void onStart() {
        super.onStart();
        tagDataList.clear();

        totalCount = 0;
        //textView_total.setText(String.format(getResources().getString(R.string.total_count), tagDataList.size()));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ApplicationClass.getmReader() != null) {
            ApplicationClass.getmReader().setEventListener(this);
        }
    }

    @Override
    public void onStop() {
        if (ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();
            }

            ApplicationClass.getmReader().removeEventListener();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (soundManager != null) {
            soundManager.close();
        }
        if (dialogHelper != null) {
            dialogHelper.DialogDismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_clear:
                //dialogHelper = new DialogHelper(clearClickListener);
                //dialogHelper.DialogShow(this, getResources().getString(R.string.clear), getResources().getString(R.string.clear_message));

                textViewInit();

                break;

            case R.id.imageView_setting:
                Intent intent = new Intent(this, SettingActivity.class);

                intent.putExtra("back", "RpidScan");

                startActivity(intent);
                finish();
                break;

        }
    }

    // 초기화
    DialogInterface.OnClickListener clearClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            textView_total.setText(null);
            textViewInit();
            dialogHelper.DialogDismiss();
        }
    };

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 138 && ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getState() == ConnectionState.Disconnected) {
                ApplicationClass.getmReader().connect();
            }

            mElapsedTick = SystemClock.elapsedRealtime() - mTick;

            if (mTick == 0 || mElapsedTick > SKIP_KEY_EVENT_TIME) {

                // 현재 리더 상태가 idle이 아닌 경우
                if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                    return super.onKeyDown(keyCode, event);
                }

                ApplicationClass.getmReader().inventory6cTag();

                mTick = SystemClock.elapsedRealtime();
            }
           // thread.start();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 138 && ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {

    }

    @Override
    public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {

    }


    @Override
    public void onReaderReadTag(ATRfidReader atRfidReader, String data, float v, float v1) {

        String tag = data.substring(4);


        TagData exists = tagDataList.stream().filter(item -> item.getEpc().equals(tag)).findFirst().orElse(null);

        if (exists != null) {
            return;
        }

        TagData tagData = new TagData();
        tagData.setEpc(tag);
        tagDataList.add(tagData);
        /*
        soundManager.playSuccess();
        totalCount= tagDataList.size();
        */
        totalCount++;
        soundManager.playSuccess();

        textView_total.setText(String.format("%d", totalCount));

        // 시간 보여주기
        /*
        if (start == 0) {
            start = System.currentTimeMillis();
            end = start;
        }
        long time = end++ -start - 32400000;
        textView_read_time.setText (sdf.format(time) + "sec") ;

         */



    }

    class BackgroundThread extends Thread {

        public void run(){



        }

    }

    @Override
    public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {

    }

    private void buttonSetting(boolean enabled) {
        button_clear.setEnabled(enabled);
    }

    private void textViewInit()
    {

        textView_total.setText("0");

        /*
        start =0 ;
        if (start == 0) {
            start = System.currentTimeMillis();
            end = start;
        }
        long time = end++ -start - 32400000;
        textView_read_time.setText (sdf.format(time) + "sec") ;*/

    }
}
