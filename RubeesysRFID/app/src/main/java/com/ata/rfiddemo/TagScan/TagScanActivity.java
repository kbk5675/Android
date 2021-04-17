package com.ata.rfiddemo.TagScan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ata.rfiddemo.MainActivity;
import com.ata.rfiddemo.R;
import com.ata.rfiddemo.Setting.SettingActivity;
import com.ata.rfiddemo.TagScan.Adapter.ListViewAdapter;
import com.ata.rfiddemo.TagScan.Adapter.TagAdapter;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.DataSet.ItemCount;
import com.ata.rfiddemo.Util.DialogHelper;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.Util.RealmSet.TagData;
import com.ata.rfiddemo.Util.SaveDialog;
import com.ata.rfiddemo.Util.Service.BcdUtilHelper;
import com.ata.rfiddemo.WriteMemory;
import com.wsmr.lib.dev.ATRfidReader;
import com.wsmr.lib.dev.event.RfidReaderEventListener;
import com.wsmr.lib.dev.rfid.exception.ATRfidReaderException;
import com.wsmr.lib.dev.rfid.param.RangeValue;
import com.wsmr.lib.dev.rfid.type.ActionState;
import com.wsmr.lib.dev.rfid.type.ConnectionState;
import com.wsmr.lib.dev.rfid.type.ResultCode;
import com.wsmr.lib.diagnostics.ATLog;
import com.xw.repo.BubbleSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.os.SystemClock.sleep;

public class TagScanActivity extends ToolbarManager implements  BubbleSeekBar.OnProgressChangedListener, RfidReaderEventListener, View.OnClickListener {

    private TextView textView_total;
    private TextView textTotal_Reading ;
    private TextView textPower ;
    private TextView txtTitle;

    private Button btnSave;
    private Button btnClear;
    private Button button_write;
    private Button btnAction;

    private Button button_sort_epc;
    private Button button_sort_count;




    private ArrayList<ItemCount> itemCountList;
    private ArrayList<TagData> tagDataList;
    private String epc;

    private TagAdapter tagAdapter;

    private SoundManager soundManager;

    private long SKIP_KEY_EVENT_TIME;
    private long mElapsedTick;
    private long mTick;

    private SaveDialog saveDialog;
    private DialogHelper dialogHelper;

    private String company;
    private String item;
    private int readingCount ;

    private int sortEpcFlag = 1;
    private int sortCountFlag = 0;
    private int sortFlag = 0;
    private ListView lstBarcodeList;
    private ListViewAdapter adapter;

    private CheckBox chkAsciiMode;
    private CheckBox chkDisplayPc;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedpreferences;
    private BubbleSeekBar seekbar_power;
    private RangeValue mPowerRange;
    private static final String TAG = WriteMemory.class.getSimpleName();

    //private LinearLayout writeMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_scan);



        textView_total       = findViewById(R.id.textView_total);
        textTotal_Reading   = findViewById(R.id.textView_total_Reading);
        textPower            = findViewById(R.id.textView_Power);
        chkAsciiMode        = findViewById(R.id.asciiMode);
        chkDisplayPc        = findViewById(R.id.displayPc);
        seekbar_power    = findViewById(R.id.seekbar_power);


        lstBarcodeList = (ListView) findViewById(R.id.tag_list);
        adapter =  new ListViewAdapter() ;
        lstBarcodeList.setAdapter(adapter);


       // setToolbar(getResources().getString(R.string.inventory_rfid), this);

        btnSave          = findViewById(R.id.btnSave);
        btnClear         = findViewById(R.id.btnClear);
        button_sort_count    = findViewById(R.id.button_sort_count);
        button_sort_epc      = findViewById(R.id.button_sort_epc);
        // button_write         = findViewById(R.id.button_write);

        btnAction     = findViewById(R.id.btnAction);

        soundManager        = new SoundManager(this);
        itemCountList       = new ArrayList<ItemCount>();
        tagDataList         = new ArrayList<TagData>();
        tagAdapter          = new TagAdapter(tagDataList);
        epc                  = null;
        SKIP_KEY_EVENT_TIME = getResources().getInteger(R.integer.skip_key_event_time);
        company             = null;
        item                = null;
        readingCount       = 0;

        // recyclerView


        // event
        btnSave.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnAction.setOnClickListener(this);

        button_sort_count.setOnClickListener(this);
        button_sort_epc.setOnClickListener(this);
       // button_write.setOnClickListener(this);
        seekbar_power.setOnProgressChangedListener(this);

        textViewInit();
        buttonSetting(false);


        try {

            //ApplicationClass.getmReader().stop();
            //sleep(300);
            ApplicationClass.getmReader().setOperationTime(0);

            ATLog.e(TAG, "============================================================ " );
            ATLog.e(TAG, " [%s]", ApplicationClass.getmReader().getAction());

            ATLog.e(TAG, "[%d] " ,  ApplicationClass.getmReader().getPower() );
            ATLog.e(TAG, "============================================================ " );

            mPowerRange = ApplicationClass.getmReader().getPowerRange();

            int power = ApplicationClass.getmReader().getPower();

            if(power == -1){
                sleep(500);
                power = ApplicationClass.getmReader().getPower();
            }

            String srPower = "";
            sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            editor             = sharedpreferences.edit();
            srPower =  sharedpreferences.getString("read_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));

            textPower.setText(String.format(getResources().getString(R.string.power_value), srPower ));


            // Ascii 설정 확인
            String asciiFlag = "";
            asciiFlag =  sharedpreferences.getString("asciiFlag", "true");
            if ("true".equals(asciiFlag) ) {
                chkAsciiMode.setChecked(true);
            }else {
                chkAsciiMode.setChecked(false);
            }

            String displayPcFlag = "";
            displayPcFlag =  sharedpreferences.getString("displayPcFlag", "true");
            if ("true".equals(displayPcFlag) ) {
                chkDisplayPc.setChecked(true);
            }else {
                chkDisplayPc.setChecked(false);
            }


            if (ApplicationClass.getmReader() != null) {
                if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                    ApplicationClass.getmReader().stop();
                }
                ApplicationClass.getmReader().removeEventListener();
            }

        } catch (ATRfidReaderException e) {
            Toast.makeText(this, "getPowerRange() Error!!!", Toast.LENGTH_SHORT).show();
        }

        String srPower = "";
        srPower =  sharedpreferences.getString("read_power", "300");


        seekbar_power.getConfigBuilder()
                .max(mPowerRange.getMax())
                .min(0) // 이하로 설정 시 에러
                .progress(Integer.parseInt(srPower))
                .build();



        chkDisplayPc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String tag = "";

                if (isChecked) {
                    //ApplicationClass.setAsciiFlag(true);
                    editor.putString("displayPcFlag", "true");
                    editor.commit();

                } else {
                    editor.putString("displayPcFlag", "false");
                    editor.commit();
                }


                for (int j = 0; j < adapter.getCount(); j++) {

                    if (chkAsciiMode.isChecked())
                    {
                        if (chkDisplayPc.isChecked()){
                            tag =  adapter.getAsciiPcEpc(j);

                        }else {
                            tag = adapter.getAsciiEpc(j);
                        }


                    }else {

                        if (chkDisplayPc.isChecked()){
                            tag = adapter.getHexPcEpc(j);
                        }else {
                            tag = adapter.getHexEpc(j);

                        }

                    }

                    adapter.setEpc(j, tag);
                }


                //Log.d("Debug2", Tags);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("Debug3" , "  ");
                     //   textView_total.setText( String.format("%d", adapter.getCount() ) );
                     //   soundPlay();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });



        chkAsciiMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    String tag = "";

                if (isChecked) {
                    //ApplicationClass.setAsciiFlag(true);
                    editor.putString("asciiFlag", "true");
                    editor.commit();

                } else {
                    editor.putString("asciiFlag", "false");
                    editor.commit();
                }


                    for (int j = 0; j < adapter.getCount(); j++) {

                        if (chkAsciiMode.isChecked())
                        {
                            if (chkDisplayPc.isChecked()){
                                tag =  adapter.getAsciiPcEpc(j);

                            }else {
                                tag = adapter.getAsciiEpc(j);
                            }


                        }else {

                            if (chkDisplayPc.isChecked()){
                                tag = adapter.getHexPcEpc(j);
                            }else {
                                tag = adapter.getHexEpc(j);

                            }

                        }

                        adapter.setEpc(j, tag);
                    }


                //Log.d("Debug2", Tags);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("Debug3" , "  ");
                       // textView_total.setText( String.format("%d", adapter.getCount() ) );
                      //  soundPlay();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });


    }



    @Override
    public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

        switch (bubbleSeekBar.getId()) {
            case R.id.seekbar_power:
                try {
                    ApplicationClass.getmReader().setPower(progress);
                } catch (ATRfidReaderException e) {
                    Toast.makeText(this, "Power Setting Error", Toast.LENGTH_SHORT).show();
                }

                editor.putString("read_power",  String.format("%d", progress)   );
                editor.commit();
                break;

            case R.id.write_power:
                editor.putString("write_power",  String.format("%d", progress)   );
                editor.commit();

                break;
        }

    }

    @Override
    public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

    }

    @Override
    public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

    }


    ItemTouchHelper.SimpleCallback tagTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            TagData tagData = tagDataList.get(position);

            Collections.sort(tagDataList,countSortAsc );
            tagAdapter.notifyDataSetChanged();

            epc  = tagData.getEpc();


            return ;

        }
    };

    @Override
    public void onStart() {
        super.onStart();

        company = null;
        item    = null;

        Log.e("onStart  ", "!! #####################################################"   );



        // sleep 모드로 갔다가 오류 발생으로
        //tagDataList.clear();
        //itemCountList.clear();
        //textViewInit();
        //tagAdapter.notifyDataSetChanged();

        //
        //ApplicationClass.getmReader().powerControl(false);
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

        Log.e("onStop  ", "!! #####################################################"   );


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

        if (saveDialog != null && saveDialog.isShowing()) {
            saveDialog.dismiss();
        }

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.btnSave:
                // 저장
                Date date = new Date();

                SimpleDateFormat format = new SimpleDateFormat( "yyyyMMdd_HHmmss");
                String time = format.format(date);

                String defaultFileName =  time;

                saveDialog = new SaveDialog(this, getResources().getString(R.string.save), defaultFileName, positiveListener);
                saveDialog.setCancelable(false);
                saveDialog.setCanceledOnTouchOutside(false);
                saveDialog.show();
                break;

            case R.id.btnClear:

                if (ApplicationClass.getmReader().getAction() == ActionState.Stop) {
                    dialogHelper = new DialogHelper(clearClickListener);
                    dialogHelper.DialogShow(this, getResources().getString(R.string.clear), getResources().getString(R.string.clear_message));
                }

                break;

            case R.id.button_sort_count:

                // 오름 차순
                if (sortCountFlag ==0) {
                    Collections.sort(tagDataList, countSortAsc);
                    sortCountFlag = 1;
                    sortFlag = 2;
                    button_sort_count.setText( "↓ Count" );
                    button_sort_epc.setText("  Epc");
                }else {
                    Collections.sort(tagDataList, countSortDesc);
                    sortCountFlag = 0;
                    sortFlag = 3;
                    button_sort_count.setText( "↑ Count" );
                    button_sort_epc.setText("  Epc");
                }
                tagAdapter.notifyDataSetChanged();
                break;


            case R.id.button_sort_epc:

                // 오름차순
                if (sortEpcFlag ==0) {
                    Collections.sort(tagDataList, epcSortAsc);
                    sortEpcFlag = 1;
                    sortFlag = 0;
                    button_sort_epc.setText("↓ Epc");
                    button_sort_count.setText( "  Count" );
                }else {
                    Collections.sort(tagDataList, epcSortDesc);
                    sortEpcFlag = 0;
                    sortFlag = 1;
                    button_sort_epc.setText("↑ Epc");
                    button_sort_count.setText( "  Count" );
                }
                tagAdapter.notifyDataSetChanged();
                break;

            case R.id.imageView_setting:
                Intent intent = new Intent(this, SettingActivity.class);

                intent.putExtra("back", "tagScan");

                startActivity(intent);
                finish();
                break;


            case R.id.btnAction:

                    if (btnAction.getText().equals("START")  )
                    {
                        action();
                    }else {

                        actionStop();
                    }

                break;

        }
    }

    // 저장
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            saveData();
            saveDialog.dismiss();
        }
    };

    private void saveData() {

        /*
        if (saveDialog.getFileName() == null || saveDialog.getFileName().trim().equals("")) {
            return;
        }

        // 파일명 중복 검사
        boolean check = ModelService.getInstance().checkFileName(saveDialog.getFileName());

        if (check) {
            Toast.makeText(this, getResources().getString(R.string.duplicate_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터 저장
        ModelService.getInstance().saveFileData(saveDialog.getFileName(), tagDataList);



         */

        adapter.excelSave( saveDialog.getFileName() );

        Toast.makeText(this, getResources().getString(R.string.save_complete_message), Toast.LENGTH_SHORT).show();


    }

    // 초기화
    DialogInterface.OnClickListener clearClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            company = null;
            item    = null;

            tagDataList.clear();
            itemCountList.clear();


            buttonSetting(false);

            textViewInit();
            adapter.clear();

            tagAdapter.notifyDataSetChanged();

            readingCount = 0 ;
            textTotal_Reading.setText(String.format(getResources().getString(R.string.count), readingCount ));

            dialogHelper.DialogDismiss();
        }
    };

    @Override
    public void onBackPressed() {
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 138 && ApplicationClass.getmReader() != null) {

            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                return super.onKeyDown(keyCode, event);
            }

            action();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 138 && ApplicationClass.getmReader() != null) {
            actionStop();
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {

    }

    @Override
    public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {

    }

    public void actionStop()
    {
        btnAction.setText("START");
        buttonSetting(true);

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }
    }

    public void action(){

            if (ApplicationClass.getmReader().getState() == ConnectionState.Disconnected) {
                ApplicationClass.getmReader().connect();
            }

            mElapsedTick = SystemClock.elapsedRealtime() - mTick;

            if (mTick == 0 || mElapsedTick > SKIP_KEY_EVENT_TIME) {

                //ApplicationClass.getmReader().readEpc6cTag(2, 3);
                btnAction.setText("STOP");
                buttonSetting(false);

                ApplicationClass.getmReader().inventory6cTag();

                mTick = SystemClock.elapsedRealtime();
            }

    }

    @Override
    public void onReaderReadTag(ATRfidReader atRfidReader, String data, float v, float v1) {
        //epc 값추출

        String tag ;

        // Ascii 설정 확인
        //

        /*
        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        String asciiFlag = "";
        asciiFlag =  sharedpreferences.getString("asciiFlag", "true");
        if ("true".equals(asciiFlag) ) {
            tag = BcdUtilHelper.hexToString(data.substring(4));
        }else {
            tag = data.substring(4);
        }*/



        if (chkAsciiMode.isChecked())
        {
            if (chkDisplayPc.isChecked()){

                tag =  data.substring(0,4);
                tag =  tag + BcdUtilHelper.hexToString(data.substring(4));

            }else {
                tag = BcdUtilHelper.hexToString(data.substring(4));
            }


        }else {

            if (chkDisplayPc.isChecked()){
                tag = data.substring(0);
            }else {
                tag = data.substring(4);

            }

        }



        Log.e(" READ DATA  ###########################  ",   ":"   + String.format( "|  %s ",  data ));
        Log.e(" tag DATA  ###########################  ",   ":"   + String.format( "|  %s ",  tag ));

        // 각 epc 별마다 읽힌 값
        textTotal_Reading.setText(String.format(getResources().getString(R.string.count), ++ readingCount  ));


        int sameflag = 0;

        for (int j = 0; j < adapter.getCount(); j++) {

            if (data.substring(0).equals(adapter.getHexPcEpc(j))) {
                adapter.setReadCount(j, adapter.getReadCount(j) + 1);
                sameflag = 1;
                break;
            }
        }

        if (sameflag == 0) {

            adapter.addItem(tag, 1, adapter.getCount() + 1    );

            adapter.setAsciiPcEpc(adapter.getCount() -1,  data.substring(0,4) + BcdUtilHelper.hexToString(data.substring(4))  );
            adapter.setHexPcEpc(adapter.getCount()-1,  data.substring(0)   );

            adapter.setAsciiEpc(adapter.getCount()-1,   BcdUtilHelper.hexToString(data.substring(4))  );
            adapter.setHexEpc(adapter.getCount()-1,  data.substring(4)   );

/*
            Log.e(" AsciiPcEpc ###########################  ",   ":"  +data.substring(0,4) + BcdUtilHelper.hexToString(data.substring(4)) );
            Log.e(" HexPcEpc  ###########################  ",   ":"   +data.substring(0) );

            Log.e(" AsciiPcEpc ###########################  ",   ":"  +BcdUtilHelper.hexToString(data.substring(4)) );
            Log.e(" HexPcEpc  ###########################  ",   ":"   + data.substring(4)  );
*/

        }
        //Log.d("Debug2", Tags);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.d("Debug3" , "  ");
                textView_total.setText( String.format("%d", adapter.getCount() ) );
                soundPlay();
                adapter.notifyDataSetChanged();
            }
        });


    }

    private void checkSort() {
        // 정렬 기준 세팅
        ItemCount itemCount = itemCountList.stream().max(Comparator.comparing(ItemCount::getCount)).orElse(null);

        if (itemCount != null) {
            for (TagData tagInfo : tagDataList) {
                tagInfo.setCorrect(false);
            }

            tagDataList.stream()
                    .filter(tagInfo -> tagInfo.getCount().equals(itemCount.getCompanyPrefix())
                            && tagInfo.getItemReference().equals(itemCount.getItemReference()))
                    .forEach(info -> {
                        info.setCorrect(true);
                    });

            company = itemCount.getCompanyPrefix();
            item    = itemCount.getItemReference();
        }
    }

    @Override
    public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {

    }


    private Comparator<TagData> countSortAsc = new Comparator<TagData>(){

        @Override
        public int compare(TagData o1, TagData o2) {
            if (!o1.getCount().equals(o2.getCount())) {

                if ( Integer.parseInt(  o1.getCount() )  >=  Integer.parseInt(  o2.getCount() ) )
                    return -1;

            }
            return 1;
        }
    };

    private Comparator<TagData> countSortDesc = new Comparator<TagData>(){

        @Override
        public int compare(TagData o1, TagData o2) {
            if (!o1.getCount().equals(o2.getCount())) {

                if ( Integer.parseInt(  o1.getCount() )  <=  Integer.parseInt(  o2.getCount() ) )
                    return -1;

            }
            return 1;
        }
    };

    private Comparator<TagData> epcSortAsc = new Comparator<TagData>(){

        @Override
        public int compare(TagData o1, TagData o2) {
            if (!o1.getEpc().equals(o2.getEpc())) {

                if (o1.getEpc().compareTo ( o2.getEpc()) > 0)
                 return 1;
                else
                 return -1;

            }
            return 1;
        }
    };


    private Comparator<TagData> epcSortDesc = new Comparator<TagData>(){

        @Override
        public int compare(TagData o1, TagData o2) {
            if (!o1.getEpc().equals(o2.getEpc())) {

                if (o2.getEpc().compareTo ( o1.getEpc()) > 0)
                    return 1;
                else
                    return -1;

            }
            return 1;
        }
    };

    private Comparator<TagData> tagSort = new Comparator<TagData>(){

        @Override
        public int compare(TagData o1, TagData o2) {
            if (o1.isCorrect() != o2.isCorrect()) {
                return Boolean.compare(o1.isCorrect(), o2.isCorrect());
            }

            if (!o1.getCount().equals(o2.getCount())) {
                return o1.getCount().compareTo(o2.getCount());
            }

            if (!o1.getItemReference().equals(o2.getItemReference())) {
                return o1.getItemReference().compareTo(o2.getItemReference());
            }

            return o1.getSerialNumber().compareTo(o2.getSerialNumber());
        }
    };

    private void buttonSetting(boolean enabled) {
        //btnSave.setEnabled(enabled);
        btnClear.setEnabled(enabled);
        btnSave.setEnabled(enabled);
    }

    private void textViewInit(){
        textTotal_Reading.setText(String.format(getResources().getString(R.string.count), readingCount ));
        textView_total.setText(String.format(getResources().getString(R.string.count), tagDataList.size()));


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



}
