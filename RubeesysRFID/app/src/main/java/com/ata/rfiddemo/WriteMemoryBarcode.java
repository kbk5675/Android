package com.ata.rfiddemo;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ata.rfiddemo.Setting.SettingActivity;
import com.ata.rfiddemo.TagScan.Adapter.ListViewAdapter;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.Util.Service.BcdUtilHelper;
import com.ata.rfiddemo.Util.StringUtil;
import com.wsmr.lib.dev.ATRfidReader;
import com.wsmr.lib.dev.barcode.type.BarcodeType;
import com.wsmr.lib.dev.barcode.type.EventType;
import com.wsmr.lib.dev.event.BarcodeEventListener;
import com.wsmr.lib.dev.event.RfidReaderEventListener;
import com.wsmr.lib.dev.rfid.exception.ATRfidReaderException;
import com.wsmr.lib.dev.rfid.param.EpcMatchParam;
import com.wsmr.lib.dev.rfid.type.ActionState;
import com.wsmr.lib.dev.rfid.type.BankType;
import com.wsmr.lib.dev.rfid.type.ConnectionState;
import com.wsmr.lib.dev.rfid.type.MaskMatchingType;
import com.wsmr.lib.dev.rfid.type.ResultCode;
import com.wsmr.lib.diagnostics.ATLog;

import static android.os.SystemClock.sleep;


public class WriteMemoryBarcode extends ToolbarManager implements RfidReaderEventListener, BarcodeEventListener,   View.OnClickListener {

    String backPageCheck;
    String hexLength;
    String writeEpc;
    String readEpc;
    String pc;



    private int offset;
    private int step;
    int power = 0;

    String epc[] = new String[18];

    private TextView textPower;
    private TextView textwPower;

    private TextView textView_Rdata;

    private Button btnWrite;
    private Button btnRead;
    private TextView editTag;
    private EditText editBarcode;


    private EpcMatchParam mEpc;

    private long SKIP_KEY_EVENT_TIME;
    private long mElapsedTick;
    private long mTick;
    private SoundManager soundManager;
    public String asciiFlag = "";
    private static final String TAG = "WriteMemory";
    StringUtil stringUtil = new StringUtil();
    boolean barCodeTriggerFlag = true ;
    boolean successFlag = false ;
    boolean buttonDown = false ;


    String[] arrayPc   = {"0800", "1000", "1800", "2000", "2800",   "3000",    "3800",   "4000",   "4800",   "5000",  "5800" ,   "6000",   "6800",   "7000",   "7800",   "8000",   "8800",   "9000",  "9800",  "A000",   "A800"  ,  "B000",   "B800",   "C000",   "C800",    "D000",    "D800",   "E000",   "E800",   "F000" , "F800"   };
    int[]    arraybit  = {   16,   32,     48,       64,     80,        96,      112 ,      128,      144,     160,     176,        192,     208,       224,      240,      256, 272, 288, 304, 320, 336,352, 368, 384, 400,  416,  432, 448, 464, 480 , 496   };

    private ListView lstBarcodeList;
    private ListViewAdapter adapter;


    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memory);

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
        /*
        textView_epc[0].setText("1111") ;
        textView_epc[1].setText("2222") ;
        textView_epc[2].setText("3333") ;
        textView_epc[3].setText("4444") ;
        textView_epc[4].setText("5555") ;
        textView_epc[5].setText("6666") ;
        textView_epc[6].setText("7777") ;
        textView_epc[7].setText("8888") ;
         */


        // 변수 초기화 END

        // 화면설정 초기화 START
        //  Top Toolbar 에서 설정 버튼 보이게

        setToolbar( getResources().getString(R.string.barcode_read_write) , this);
        //power 값 세팅
        /*
        try {
            /// write 할때는 일단 150으로
            //ApplicationClass.getmReader().setPower(150);
            textPower.setText(String.format(getResources().getString(R.string.power_value), ApplicationClass.getmReader().getPower()));
            SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);


            textwPower.setText(String.format(getResources().getString(R.string.wpower_value),  sharedpreferences.getString("write_power", "300")));

        }    catch (   ATRfidReaderException e) {
            Toast.makeText(this, "getPowerRange() Error!!!", Toast.LENGTH_SHORT).show();
        }

         */

        // spinner 설정 ( combobox )



        // 화면설정 초기화 END

        // Event Listener

        // Event Listener 설정
        this.setListener();


        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        asciiFlag =  sharedpreferences.getString("asciiFlag", "true");

        /*
        if ("true".equals(asciiFlag))
            ApplicationClass.setAsciiFlag(true);
        else
            ApplicationClass.setAsciiFlag(false);

         */



        ApplicationClass.getmScanner().setEventListener(this);
        lstBarcodeList = (ListView) findViewById(R.id.tag_list);
        adapter =  new ListViewAdapter() ;
        lstBarcodeList.setAdapter(adapter);



        try {

            int power = ApplicationClass.getmReader().getPower();

            if(power == -1){
                sleep(500);
                power = ApplicationClass.getmReader().getPower();
            }

            String srPower = "";
            sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            srPower =  sharedpreferences.getString("read_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));

            textPower.setText(String.format(getResources().getString(R.string.power_value), srPower ));

        } catch (ATRfidReaderException e) {
            Toast.makeText(this, "getPowerRange() Error!!!", Toast.LENGTH_SHORT).show();
        }


    }




    private void setListener()
    {
        btnWrite.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        editBarcode.setOnFocusChangeListener(this::onFocusChange);
        editTag.setOnFocusChangeListener(this::onFocusChange);

    }



    // epc 세틍하는 text view 에 커서가 포커스 될경우
    // background 색상을 변경한다 .
    public void onFocusChange(View view, boolean hasFocus) {

        switch (view.getId()) {
            case R.id.editBarcode:
                if (hasFocus) {
                    editBarcode.setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editBarcode.setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.editTag:
                if (hasFocus) {
                    editTag.setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editTag.setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

        }


/*
        textView_epc[0].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int i = 4;
                if ( ApplicationClass.getAsciiFlag())
                    i = 2;

                if(textView_epc[0].length()>=i){  // edit1  값의 제한값을 6이라고 가정했을때
                    textView_epc[1].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int i = 4;
                if ( ApplicationClass.getAsciiFlag())
                    i = 2;

                if(textView_epc[0].length()>= i) {
                    text = textView_epc[0].getText().toString();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

                int i = 4;
                if ( ApplicationClass.getAsciiFlag())
                    i = 2;

                if(textView_epc[0].length()> i){
                    textView_epc[0].setText(text);
                }
            }
        });

 */


    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnRead:
                // 실제 메모리에 write하는 function
                buttonDown = true ;

                if (btnRead.getText().equals(getResources().getString(R.string.read)) )
                {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnRead.setText("STOP");
                            btnWrite.setEnabled(false);
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
                            btnRead.setText(getResources().getString(R.string.read));
                            btnWrite.setEnabled(true);
                        }
                    });

                }



                break;

            // 화면에 write 버튼 클릭
            case R.id.btnWrite:
                // 실제 메모리에 write하는 function

                if ( editBarcode.getText().toString().trim().length() <= 0)
                {
                    break ;
                }



                if (    BcdUtilHelper.stringToHex( editBarcode.getText().toString() ).length() * 4 < 16 )
                {
                    break ;
                }


                buttonDown = true ;

                if (btnWrite.getText().equals(getResources().getString(R.string.write)) )
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnWrite.setText("STOP");
                            btnRead.setEnabled(false);
                        }
                    });

                    writeData();
                }else if (btnWrite.getText().equals("STOP") )
                {
                    if (ApplicationClass.getmReader() != null) {
                        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                            ApplicationClass.getmReader().stop();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnWrite.setText(getResources().getString(R.string.write));
                            btnRead.setEnabled(true);
                        }
                    });


                }
                break;

            case R.id.imageView_setting:
                Intent intent = new Intent(this, SettingActivity.class);

                intent.putExtra("back", "WriteMemory");

                startActivity(intent);
                finish();
                break;
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 트리거 이벤트
        if (keyCode == 138 && event.getRepeatCount() <= 0  ) {

            buttonDown = false ;
            successFlag = false;




            if ( barCodeTriggerFlag==true ) {

                barcodeReadData();
                btnRead.setEnabled(false);

            }

            if (barCodeTriggerFlag ==false && editBarcode.getText().toString().trim().length() > 0 ){


                if (  BcdUtilHelper.stringToHex( editBarcode.getText().toString() ).length() * 4 < 16 )
                {
                    return false ;
                }


                ATLog.e(TAG, "###################### onKeyDown2222222222   ###########" );

                if (ApplicationClass.getmReader() != null) {
                    if (ApplicationClass.getmReader().getState() == ConnectionState.Disconnected) {
                        ApplicationClass.getmReader().connect();
                    }

                    mElapsedTick = SystemClock.elapsedRealtime() - mTick;
                    if (mTick == 0 || mElapsedTick > SKIP_KEY_EVENT_TIME) {

                        // 현재 리더 상태가 idle이 아닌 경우
                        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                            return super.onKeyDown(keyCode, event);
                        }
                        // 실제 메모리에 write하는 function
                        btnWrite.setEnabled(false);
                        writeData();
                        mTick = SystemClock.elapsedRealtime();
                    }
                }
            }
        }



        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {


        if (keyCode == 138 ) {

            btnWrite.setEnabled(true);
            btnRead.setEnabled(true);

            if (successFlag == true) {

                if (barCodeTriggerFlag == true) {
                    barCodeTriggerFlag = false;

                }else {
                    barCodeTriggerFlag = true;
                }
                successFlag = false ;
            }


            if (ApplicationClass.getmScanner().isDecoding()) {
                ApplicationClass.getmScanner().stopDecode();
            }

            if (ApplicationClass.getmReader() != null) {
                if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                    ApplicationClass.getmReader().stop();
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }



    @Override
    public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {

    }

    // Tag 를 쓴후에 이벤트
    @Override
    public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {
        Log.e("WRITE resultCode.getCode  ",   ":"   + String.format( "| %d , %s ",  resultCode.getCode( ) , actionState  ) );
        Log.e("WRITE resultCode.getCode  ",   ":"   + String.format( "| %s , %s ",  s , s1  ) );


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (  buttonDown == true ) {
                    btnWrite.setText(getResources().getString(R.string.write));
                    btnRead.setEnabled(true);
                }
            }
        });



        try {


            String srPower = "";
            SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            srPower =  sharedpreferences.getString("read_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));

        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, "############# power  [s] ###########", e.toString() );
        }

        if (resultCode.getCode() == 0 ){
            //
            textView_Rdata.setText(writeEpc);
            // MSG
            String msg = "";
            if (resultCode == ResultCode.NoError) {
                msg = "Success";
            } else {
                msg = resultCode.toString();
            }
            editTag.setText(msg);


            // list view
            //adapter.addItem( stringUtil.getHexToString(writeEpc).toString() , adapter.getCount() + 1 );
            adapter.addItem( editBarcode.getText().toString().trim() , adapter.getCount() + 1 );

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soundPlay();
                    adapter.notifyDataSetChanged();

                    if (  buttonDown == true ) {
                        barCodeTriggerFlag = true;
                    }
                    successFlag = true;
                    // sleep(200);
                    // editTag.setText("");
                }
            });


        } else  {

            errorShowMessage(resultCode.toString() );

        }
    }

    // Tag 를 읽은후에 이벤트
    public void onReaderReadTag(ATRfidReader atRfidReader, String data, float v, float v1) {
        Log.e("Reding Data  ",   ":"   + String.format( "| %s ",  data  ) );
        textView_Rdata.setText(data);
        soundPlay();
    }


    @Override
    public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {

    }


    @Override
    public void onStart() {
        super.onStart();
        //ApplicationClass.getmReader().powerControl(false);
        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        asciiFlag =  sharedpreferences.getString("asciiFlag", "true");

        /*
        if ("true".equals(asciiFlag))
            ApplicationClass.setAsciiFlag(true);
        else


         */


        try {
            power = ApplicationClass.getmReader().getPower();
        } catch (ATRfidReaderException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }

        if (soundManager != null) {
            soundManager.close();
        }
        super.onDestroy();
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
    public void onBackPressed() {
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // 실제 메모리에 write하는 function

    public void barcodeReadData()
    {
        editBarcode.setText("");
        editTag.setText("");


        if (ApplicationClass.getmScanner().isDecoding()) {
            ApplicationClass.getmScanner().stopDecode();
        } else {
            ApplicationClass.getmScanner().startDecode();
        }

    }

    public void  writeData(){

        editTag.setText("");


        if ( editBarcode.getText().toString().trim().length() <= 0)
        {
            return ;
        }

        if (   BcdUtilHelper.stringToHex( editBarcode.getText().toString() ).length() * 4 < 16 )
        {
            return ;
        }


        if ( ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getState() == ConnectionState.Disconnected) {
                ApplicationClass.getmReader().connect();
            }
        }
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();
            return ;
        }
        ResultCode res;
        BankType bank = BankType.EPC;
        //writeEpc 변수에 epc 값 세팅
        writeEpc = "";
        pc = "";
        setEpc ();

        Log.e("#########  ",   " writeEpc :"   + String.format( "| %s ",  writeEpc));
        Log.e("#########  ",   "  pc :"   + String.format( "| %s ",   pc ));

        mEpc = new EpcMatchParam(MaskMatchingType.Match, 0, writeEpc.length(), writeEpc);

        String swPower = "";
        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        swPower =  sharedpreferences.getString("write_power", "300");


        try {
            power = ApplicationClass.getmReader().getPower();

            if(power == -1){
                sleep(300);
                power = ApplicationClass.getmReader().getPower();
            }

            if (!"".equals(swPower)) {
                ApplicationClass.getmReader().setPower(Integer.parseInt(swPower));

                //  ApplicationClass.getmReader().setOperationTime(3000);
            }

        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, "############# power  [s] ###########", e.toString() );
        }

        ATLog.e(TAG, "############# power [%d] [%s] ###########", power, swPower);

        try {
            res = ApplicationClass.getmReader().writeMemory6c(bank, 1, pc + writeEpc);
        }catch (NumberFormatException e){
            errorShowMessage(" 데이터 형식이 잘못되었습니다. ");
        }

        return ;
    }


    public int  readData(){

        ResultCode res;
        if ( ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getState() == ConnectionState.Disconnected) {
                ApplicationClass.getmReader().connect();
            }
        }
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            return 2;
        }
        res = ApplicationClass.getmReader().readEpc6cTag();
        return res.getCode();
    }



    public boolean setEpc()
    {
        writeEpc  = editBarcode.getText().toString().trim() ;
        //if ( ApplicationClass.getAsciiFlag() ) {

        // TO HEX !!

        if (asciiFlag.equals("true")) {
            writeEpc = BcdUtilHelper.stringToHex(writeEpc);
        }

        int epcLength = writeEpc.length()  ;

        // bit 숫자 채우기

        switch ( epcLength % 4 ) {
            case 3 :
                writeEpc = writeEpc + "0" ;
                break;

            case 2 :
                writeEpc = writeEpc + "00" ;
                break;

            case 1 :
                writeEpc = writeEpc + "000" ;
                break;
        }
        int  bitLength = writeEpc.length() * 4  ;

        for (int i = 0 ;  i < arraybit.length  ; i++  ) {

            if (bitLength == arraybit[i] )
            {
                pc = arrayPc[i];
                break;
            }
        }

        /*
        int epcLength = writeEpc.length()  ;

        for (int i = epcLength ;  i <= 60  ; i++  ) {

            writeEpc = writeEpc + "0" ;
        }
        pc = "7800";
         */

        ATLog.e(TAG, "setEpc([%s], [%s], [%d])", writeEpc, pc, epcLength  );
        return true ;


    }

    private void showMessage(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("안내");
        builder.setMessage("자리수를 다 채워주세요");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                editTag.requestFocus();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        btnWrite         = findViewById(R.id.btnWrite);
        btnRead        = findViewById(R.id.btnRead);
        textPower            = findViewById(R.id.textView_Power);
        textwPower = findViewById(R.id.textView_wPower);
        textView_Rdata       =  findViewById(R.id.textView_Rdata);
        editTag = findViewById(R.id.editTag);
        editBarcode  = findViewById(R.id.editBarcode) ;
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

                    btnRead.setText(getResources().getString(R.string.read));
                    btnWrite.setEnabled(true);
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
                    editBarcode.setText(barcode);
                    // editTag.setText("Success");

                    if (  buttonDown == true ) {
                        barCodeTriggerFlag=false;

                    }
                    successFlag = true;

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
