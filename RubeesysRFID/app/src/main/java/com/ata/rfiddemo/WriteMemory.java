package com.ata.rfiddemo;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.Util.StringUtil;
import com.wsmr.lib.dev.ATRfidReader;
import com.wsmr.lib.dev.event.RfidReaderEventListener;
import com.wsmr.lib.dev.rfid.exception.ATRfidReaderException;
import com.wsmr.lib.dev.rfid.param.EpcMatchParam;
import com.wsmr.lib.dev.rfid.param.QValue;
import com.wsmr.lib.dev.rfid.param.RangeValue;
import com.wsmr.lib.dev.rfid.type.ActionState;
import com.wsmr.lib.dev.rfid.type.BankType;
import com.wsmr.lib.dev.rfid.type.ConnectionState;
import com.wsmr.lib.dev.rfid.type.GlobalBandType;
import com.wsmr.lib.dev.rfid.type.InventorySession;
import com.wsmr.lib.dev.rfid.type.InventoryTarget;
import com.wsmr.lib.dev.rfid.type.MaskMatchingType;
import com.wsmr.lib.dev.rfid.type.ResultCode;
import com.wsmr.lib.dev.rfid.type.TagType;
import com.wsmr.lib.diagnostics.ATLog;
import com.xw.repo.BubbleSeekBar;

import static android.os.SystemClock.sleep;

public class WriteMemory extends ToolbarManager implements BubbleSeekBar.OnProgressChangedListener, RfidReaderEventListener,   View.OnClickListener {


    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------
    //protected Ini ini;
    protected String tempOperationTime;
    protected String tempGlobalBand;
    protected String tempInventoryTime;
    protected String tempIdleTime;
    protected String tempSession;
    protected String tempTarget;
    protected String tempStartQ;
    protected String tempMaxQ;
    protected String tempMinQ;
    protected String tempPowerGain;

    private static int InitFlag = 0;

    private RangeValue mPowerRange;
    private GlobalBandType mGlobalBand;
    private int mOperationTime;
    private int mInventoryTime;
    private int mIdleTime;
    private int mPowerLevel;

    private String[] mFreqChanNames;
    private boolean[] mFreqChanUses;
    private InventorySession mInventorySession;
    private InventoryTarget mInventoryTarget;
    private QValue mQValue;

    private TextView txtWriteValue_Hex;
    private TextView txtWriteValue_Ascii;
    private TextView textView_bits;
    private TextView tag_type;
    private Button btnActionHex;
    // private Button btnActionAscii;
    private Button actionRead;
    private Button clear;
    // private Button actionKill;

    private String mWriteValue_Hex;
    private String mWriteValue_Ascii;
    private SoundManager soundManager;


    // 신규로 추가한 소스 20200310 mino
    Spinner spinner;
    String[] arrayPc   = {"0800","1000","1800", "2000", "2800",   "3000",    "3800",   "4000",   "4800",   "5000",  "5800" ,   "6000",   "6800",   "7000",   "7800",   "8000",   "8800",   "9000",  "9800",  "A000",   "A800"  ,  "B000",   "B800",   "C000",   "C800",    "D000",    "D800",   "E000",   "E800",   "F000" , "F800"  };
    String[] bits      = {"16bit","32bit","48bit","64bit","80bit", "96bit", "112bit" , "128bit", "144bit", "160bit", "176bit", "192bit", "208bit", "224bit", "240bit", "256bit", "272bit", "288bit", "304bit", "320bit", "336bit","352bit", "368bit", "384bit", "400bit",  "416bit",  "432bit", "448bit", "464bit", "480bit" , "496bit" };
    public EditText editText_epc[] = new EditText[31];
    String epc[] = new String[31];
    String pc = "";
    private int spinnerIndex;
    private boolean asciiFlag=false ;

    RadioGroup radioGroup_Hex_Ascii;
    RadioButton radioButton_Hex;
    RadioButton radioButton_Ascii;
    String writeEpc = "";
    String data = "";
    TextView txtSelection;
    TextView txtMessage;
    boolean readFlag = true ;
    StringUtil stringUtil = new StringUtil();
    public static  InputFilter[] defaultFilter;

    public TextView getTxtWriteValue_Ascii() {
        return txtWriteValue_Ascii;
    }

    private static final String TAG = WriteMemory.class.getSimpleName();

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedpreferences;
    private BubbleSeekBar seekbar_power;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        // ViewBy Id
        this.setViewById();


        mQValue = new QValue();

        mWriteValue_Hex = "";
        mWriteValue_Ascii = "";

        soundManager        = new SoundManager(this);
        initWidgets();


        try {

            //ApplicationClass.getmReader().stop();
           // sleep(300);
            ApplicationClass.getmReader().setOperationTime(10000);

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
            srPower =  sharedpreferences.getString("write_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));

            String strAsciiFlag =  sharedpreferences.getString("asciiFlag", "true");
            if ("true".equals(strAsciiFlag) ) {
                radioButton_Ascii.setChecked(true);
                asciiFlag = true;
            }else {
                radioButton_Hex.setChecked(true);
            }


            seekbar_power.getConfigBuilder()
                    .max(mPowerRange.getMax())
                    .min(0) // 이하로 설정 시 에러
                    .progress(Integer.parseInt(srPower))
                    .build();

        } catch (ATRfidReaderException e) {

            ATLog.e(TAG, "============================================================ " );
            Toast.makeText(this, "getPowerRange() Error!!!", Toast.LENGTH_SHORT).show();
            ATLog.e(TAG, "============================================================ " );
        }

        if (ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();
            }
            ApplicationClass.getmReader().removeEventListener();
        }


        setFilter(false);
        this.setListener();
    }


    // ------------------------------------------------------------------------
    // Override Widgets Control Methods
    // ------------------------------------------------------------------------

    // Initialize Activity Widgets
    protected void initWidgets() {

        //꺼짐방지
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //super.initWidgets();

        // Initialize Write Data EditText
        // txtWriteValue_Hex = (TextView) findViewById(R.id.write_value_Hex);
        // txtWriteValue_Hex.setOnClickListener(this);
        // Initialize Write Data EditText
        // txtWriteValue_Ascii = (TextView) findViewById(R.id.write_value_Ascii);
        // txtWriteValue_Ascii.setOnClickListener(this);

        // Initialize Action Button
        btnActionHex = (Button) findViewById(R.id.actionhex);
        btnActionHex.setOnClickListener(this);
        // Initialize Action Button
        //btnActionAscii = (Button) findViewById(R.id.actionascii);
        //btnActionAscii.setOnClickListener(this);

        // Initialize Action Button
        actionRead = (Button) findViewById(R.id.actionRead);
        actionRead.setOnClickListener(this);

        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(this);

        // actionKill = (Button) findViewById(R.id.actionKill);
        // actionKill.setOnClickListener(this);

        //setBank(BankType.EPC);
        //setOffset(2);

        // spinner 설정 ( combobox )
        spinner     = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this , R.layout.spinner_text, bits);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(adapter);
        //Load_IniFile();

        // ViewBy Id
        this.setViewById();
        // epc text box 설정
        for (int i = 0 ; i < epc.length ; i++)
        {
            epc[i] = "";
            editText_epc[i].setText (epc[i]) ;
        }
        // epc text 에 focusChange 설정

        spinner.setSelection(5);

        radioButton_Hex = findViewById(R.id.radioButton_Hex);
        radioButton_Ascii = findViewById(R.id.radioButton_Ascii);
        tag_type = findViewById(R.id.tag_type);

        tag_type.setText ( TagType.Tag6C.toString()  );

        //
        // radioButton_Hex.setChecked(true);
        pc = "3000";

        txtSelection = (TextView) findViewById(R.id.selection);
        txtMessage = (TextView) findViewById(R.id.message);

        //editText_epc[0].setPrivateImeOptions("defaultInputmode=numberic");
        //editText_epc[1].setPrivateImeOptions("defaultInputmode=numberic");
        seekbar_power    = findViewById(R.id.seekbar_power);
        seekbar_power.setOnProgressChangedListener(this);

        //super.activityMode  = 2;
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

    // ------------------------------------------------------------------------
    // Activity Event Handler
    // ------------------------------------------------------------------------

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 138 && ApplicationClass.getmReader() != null   ) {
            btnActionHex.setText(R.string.write);

            ATLog.e(TAG, "################## STOP ###################"  );

            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    enableWidgets(true, "WRITE");
                }
            });
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 138 && ApplicationClass.getmReader() != null && event.getRepeatCount() <= 0   ) {


            Log.e("onKeyDown  ", "!! #####################################################"   );


            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                return super.onKeyDown(keyCode, event);
            }

            ATLog.e(TAG, "################## INFO. onClick()" + btnActionHex.getText() );

            if (btnActionHex.getText().equals(getResources().getString(R.string.write)) ) {
                startAction();

            }else {

                btnActionHex.setText(R.string.write);

                ATLog.e(TAG, "################## STOP ###################"  );

                if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                    ApplicationClass.getmReader().stop();

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableWidgets(false, "WRITE");
                    }
                });
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    protected void enableWidgets(boolean enabled, String buttonName) {

        if (buttonName == "READ")
        {
           // btnActionHex.setEnabled(enabled);
        }
        if (buttonName == "WRITE")
        {
           // actionRead.setEnabled(enabled);
        }

/*

        ATLog.v(TAG, "################ INFO. enableWidgets(%s)" , enabled );

        if ( enabled == true ) {
            //txtWriteValue_Hex.setEnabled(enabled);
           // txtWriteValue_Ascii.setEnabled(enabled);
            btnActionHex.setText(R.string.write);
            actionRead.setText( R.string.action_read  );
            //btnActionAscii.setText(R.string.actionascii_write);
            actionRead.setEnabled(enabled);

            // actionKill.setText(R.string.action_kill);
            // actionKill.setEnabled(enabled);
        } else {
            //txtWriteValue_Hex.setEnabled(false);

         //   txtWriteValue_Ascii.setEnabled(false);
            btnActionHex.setText(R.string.stop);
            actionRead.setText(R.string.stop);
            //btnActionAscii.setText(R.string.actionascii_stop);
            actionRead.setEnabled(enabled);
            //actionKill.setText(R.string.action_stop);
            //actionKill.setEnabled(false);

        }
        btnActionHex.setEnabled(enabled);
        */

        // btnActionAscii.setEnabled(enabled);

    }

    @Override
    public void onClick(View v) {
      //  super.onClick(v);
        switch (v.getId()) {


            case R.id.actionhex:

                ATLog.e(TAG, "################## INFO. onClick()" + btnActionHex.getText() );

                if (btnActionHex.getText().equals(getResources().getString(R.string.write)) ) {
                    startAction();

                }else {

                    btnActionHex.setText(R.string.write);

                    ATLog.e(TAG, "################## STOP ###################"  );

                    if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                        ApplicationClass.getmReader().stop();

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableWidgets(false, "WRITE");
                        }
                    });
                }
                break;

            case R.id.actionRead:

                if (actionRead.getText().equals(getResources().getString(R.string.action_read)) ) {
                    startReadAction();
                }else {


                    ATLog.e(TAG, "################## STOP ###################"  );

                    actionRead.setText(getResources().getString(R.string.action_read));

                    if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                        ApplicationClass.getmReader().stop();

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enableWidgets(false, "READ");
                        }
                    });
                }

                break;

            case R.id.clear:

                ATLog.e(TAG, "################## clear ###################"  );
                clear();
                break;

        }
    }





    // startReadAction  Action
    protected void startReadAction() {

        ATLog.e(TAG, "################## INFO. startAction()");
        actionRead.setText("STOP");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableWidgets(false, "READ");
            }
        });


        readFlag = true ;
        clear();

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }


        ResultCode res;
        BankType bank = BankType.EPC;;
        //int offset = getOffset();
        //int length = getLength();

        int offset = 0;
        int length = 0;


        // epc 는 offset 이 큰 의미가 없음
        if (bank.equals( BankType.EPC ) ) {
            // higgs-ec
            offset = 0;
            length = 1;

        }

        String password = "";
        EpcMatchParam epc = new EpcMatchParam();

        //ApplicationClass.getmReader().readEpc6cTag();

        //enableWidgets(false);


        if ((res =  ApplicationClass.getmReader().readMemory6c(bank, offset, length )) != ResultCode.NoError) {
            ATLog.e(TAG, "ERROR. startAction() - Failed to read memory 6C tag [%s]", res);

           // enableWidgets(true);
            return;
        }else {
            Log.e(TAG,  String.format("SUCCESS. startAction() - write memory 6B tag [%s]", res.toString() ));
          //  enableWidgets(true);
        }


    }
    // ------------------------------------------------------------------------
    // Reader Control Methods
    // ------------------------------------------------------------------------




    protected void startAction() {

        btnActionHex.setText("STOP");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableWidgets(false, "WRITE");
            }
        });

        if (ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();
            }
            sleep(100);
        }

        // new code mino start
        writeEpc = "";
        if ( setEpc (spinnerIndex) == false ) {

        }

        txtSelection.setText("");
        txtMessage.setText("");

       // clear();
        readFlag = false ;



        if (radioButton_Hex.isChecked()){
            data = pc + writeEpc ;
        }else if(radioButton_Ascii.isChecked()){
            data = pc +  stringUtil.getStringToHex(writeEpc);
        }
        int offset = 1;
        // end

        Log.e("#########  ",   " writeEpc :"   + String.format( "| %s ",  writeEpc));
        Log.e("#########  ",   "  pc :"   + String.format( "| %s ",   pc ));
        Log.e("#########  ",   "  DATA :"   + String.format( "| %s ",   data ));
        ResultCode res;
        BankType bank = BankType.EPC;


        txtSelection.setText("");
        txtMessage.setText("");

        //enableWidgets(false);



        EpcMatchParam mEpc = new EpcMatchParam(MaskMatchingType.Match, 0, 4, editText_epc[0].getText().toString().trim());

        //Log.e(TAG,  String.format("####################################### [%s]", editText_epc[0].getText().toString().trim()));

            if ((res = ApplicationClass.getmReader().writeMemory6c(bank, offset, data  )) != ResultCode.NoError) {
                Log.e(TAG,  String.format("ERROR. startAction() - Failed to write memory 6B tag [%s]", res.toString() ));
                //enableWidgets(true);
                return;
            }else {
                Log.e(TAG,  String.format("SUCCESS. startAction() - write memory 6B tag [%s]", res.toString() ));
               // enableWidgets(true);


            }

        ATLog.i(TAG, "INFO. startAction()");

        //Set_IniFile();
    }


    @Override
    public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {

    }

    @Override
    public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {

    }

    @Override
    public void onReaderReadTag(ATRfidReader atRfidReader, String s, float v, float v1) {


        ATLog.e(TAG, "############################ onReaderReadTag(%s, ) ######################## ", s);
    }

    @Override
    public void onStart() {
        super.onStart();
        //ApplicationClass.getmReader().powerControl(false);

        /*
        if ("true".equals(asciiFlag))
            ApplicationClass.setAsciiFlag(true);
        else
            ApplicationClass.setAsciiFlag(false);
        */

        try {
            int    power = ApplicationClass.getmReader().getPower();
        } catch (ATRfidReaderException e) {
            e.printStackTrace();
        }
    }



    // Action 끝난후
    @Override
    public void onReaderResult(ATRfidReader reader, ResultCode code, ActionState action, String tag, String epc,
                               float rssi, float phase) {

            Log.e("WRITE resultCode.getCode  ",   ":"   + String.format( "| %d , %s ",  code.getCode( ) , action  ) );
            Log.e("WRITE resultCode.getCode  ",   ":"   + String.format( "| %s , %s ",  tag , epc  ) );

        actionRead.setText(getResources().getString(R.string.action_read));
        btnActionHex.setText(R.string.write);


        try {

            String srPower = "";
            SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            srPower =  sharedpreferences.getString("read_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));

        } catch (ATRfidReaderException e) {
            ATLog.e(TAG, "############# power  [s] ###########", e.toString() );
        }



        resultMessage(code);

        if (code.getCode() == 0 ) {
            if (readFlag) {


                if (radioButton_Hex.isChecked()) {
                    setSelection(tag);
                }else {

                    setSelection(tag.substring(0,4 ) +  stringUtil.getHexToString(tag.substring(4)) );
                }


                int j = 4;
                int k = 8;

                //editText_epc[0].setText(epc.substring(j, k));

                //editText_epc[0].setFilters(defaultFilter);

                setFilter(true);


                for (int i = 0 ; i < arrayPc.length ; i++)
                {
                    if (arrayPc[i].equals(tag.substring(0,4))) {
                        spinnerIndex = i;

                        spinner.setSelection(i);

                        break;

                    }
                }

                for (int i = 0; i < spinnerIndex + 1; i++) {
                 //   Log.e("###########  ", ":" + String.format(" onReaderResult ( [%s] )", tag.substring(j, k)));

                    if (radioButton_Hex.isChecked()) {
                        editText_epc[i].setText(tag.substring(j, k));
                    } else if (radioButton_Ascii.isChecked()) {
                        editText_epc[i].setText(stringUtil.getHexToString(tag.substring(j, k)));
                    }

                    j = j + 4;
                    k = k + 4;
                }
                setFilter(false);
            } else {

                if (radioButton_Hex.isChecked()) {
                    setSelection(data);
                }else {

                    setSelection(data.substring(0,4 ) +  stringUtil.getHexToString(data.substring(4)) );
                }
            }

            soundPlay();

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableWidgets(true, "WRITE");
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableWidgets(true, "READ");
            }
        });
    }




    public boolean setEpc(int position)
    {
        for (int i = 0 ; i < 1 + position ; i++)
        {
            epc[i] = editText_epc[i].getText().toString().trim();

        }

        if ( radioButton_Ascii.isChecked() )
        {
            for (int i = 0; i < 1 + position; i++) {

                if (epc[i].trim().length() != 2) {
                    // 원하는 자리수가 채워지지 않을경우 팝업
                    showMessage(i, "Hex 는 4자리 Ascii 는 2자리로 채워주세요");
                    return false;
                }
                writeEpc = writeEpc + epc[i];
            }

        }else {

            for (int i = 0; i < 1 + position; i++) {

                if (epc[i].trim().length() != 4) {
                    // 원하는 자리수가 채워지지 않을경우 팝업
                    showMessage(i, "Hex 는 4자리 Ascii 는 2자리로 채워주세요");
                    return false;
                }

                writeEpc = writeEpc + epc[i];

            }
        }

        return true ;
    }

    private void showMessage(int position, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("안내");
        builder.setMessage(msg);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        editText_epc[position].requestFocus();

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


    //////////////////////////////////////////////
    // event  모음
    private void setListener()
    {
        // hex ascii 라디오 버튼 이벤트
        radioGroup_Hex_Ascii.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioButton_Ascii){

                    asciiFlag = true;
                    setFilter(false);
                    //setEditBoxMaxLength();

                    editor.putString("asciiFlag", "true");
                    editor.commit();

                }else{



                    asciiFlag = false;
                    setFilter(false);

                    editor.putString("asciiFlag", "false");
                    editor.commit();
                    // setEditBoxMaxLength();
                }
            }
        });


        // epc text 포커스 시에 배경색 바꿈 START
        editText_epc[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[0].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[0].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[1].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[1].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[1].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[2].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[2].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[2].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[3].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[3].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[3].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[4].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[4].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[4].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[5].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[5].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[5].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[6].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[6].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[6].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[7].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[7].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[7].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[8].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[8].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[8].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[9].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[9].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[9].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[10].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[10].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[10].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[11].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[11].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[11].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[12].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[12].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[12].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[13].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[13].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[13].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[14].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[14].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[14].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[15].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[15].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[15].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[16].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[16].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[16].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[17].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[17].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[17].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[18].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[18].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[18].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[19].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[19].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[19].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[20].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[20].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[20].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[21].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[21].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[21].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[22].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[22].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[22].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[23].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[23].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[23].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[24].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[24].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[24].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[25].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[25].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[25].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[26].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[26].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[26].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[27].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[26].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[26].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[28].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[28].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[28].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });


        editText_epc[29].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[29].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[29].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });

        editText_epc[30].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editText_epc[30].setBackgroundResource(R.drawable.fource_text_style);
                }else {
                    editText_epc[30].setBackgroundResource(R.drawable.edit_text_style);
                }
            }
        });
        // 배경생 바꿈 END


        // spinner 의 선택에 따르 epc text 박스 visible 기능
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View View, int position , long id){

                pc = arrayPc[position] ;
                spinnerIndex = position;

                setTextBoxVisible(position, adapterView);

                /*
                if (position ==0 )
                {
                    setTextBoxVisible(position, adapterView);
                } else if (position ==1 ) {
                    setTextBoxVisible(position, adapterView);
                } else if (position ==2 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==3 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==4 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==5 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==6 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==7 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==8 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==9 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==10 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==11) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==12 ) {
                    setTextBoxVisible(position, adapterView);
                }else if (position ==13 ) {
                    setTextBoxVisible(position, adapterView);
                }*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
                pc = "";
            }
        } );

        // epc text 박스에 text 개수에 따라 다음 칸으로 이동
        editText_epc[0].addTextChangedListener(new TextWatcher() {

            public String text = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;

                setbits();
                if(editText_epc[0].length()==i){  // edit1  값의 제한값을 6이라고 가정했을때
                    Log.e("#########  ",   " defaultInputmode :"   + String.format( "| %s ",  editText_epc[0].getPrivateImeOptions().toString() ));
                    editText_epc[1].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*
                int i = 4;
                if ( asciiFlag)
                    i = 2;

                if(editText_epc[0].length()== i) {
                    text = editText_epc[0].getText().toString();
                }*/
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        editText_epc[1].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[1].length()==i){  // edit1  값의 제한값을 6이라고 가정했을때
                    editText_epc[2].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[2].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[2].length()==i){  // edit2  값의 제한값을 6이라고 가정했을때
                    editText_epc[3].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[3].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[3].length()==i){  // edit3  값의 제한값을 6이라고 가정했을때

                    editText_epc[4].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[4].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[4].length()==i){  // edit4  값의 제한값을 6이라고 가정했을때
                    editText_epc[5].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[5].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[5].length()==i){  // edit5  값의 제한값을 6이라고 가정했을때
                    editText_epc[6].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[6].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[6].length()==i){  // edit6  값의 제한값을 6이라고 가정했을때
                    editText_epc[7].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[7].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[7].length()==i){  // edit7  값의 제한값을 7이라고 가정했을때
                    editText_epc[8].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[8].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[8].length()==i){  // edit8  값의 제한값을 8이라고 가정했을때
                    editText_epc[9].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[9].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[9].length()==i){  // edit9  값의 제한값을 9이라고 가정했을때
                    editText_epc[10].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[10].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[10].length()==i){  // edit10  값의 제한값을 10이라고 가정했을때
                    editText_epc[11].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[11].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[11].length()==i){  // edit11  값의 제한값을 11이라고 가정했을때
                    editText_epc[12].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[12].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[12].length()==i){  // edit12  값의 제한값을 12이라고 가정했을때
                    editText_epc[13].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[13].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[13].length()==i){  // edit13  값의 제한값을 13이라고 가정했을때
                    editText_epc[14].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[14].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[14].length()==i){  // edit14  값의 제한값을 14이라고 가정했을때
                    editText_epc[15].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[15].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[15].length()==i){  // edit15  값의 제한값을 15이라고 가정했을때
                    editText_epc[16].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[16].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[16].length()==i){  // edit16  값의 제한값을 16이라고 가정했을때
                    editText_epc[17].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[17].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[17].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    //editText_epc[18].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editText_epc[18].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[18].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[19].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[19].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[19].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[20].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[20].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[20].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[21].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[21].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[21].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[22].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editText_epc[22].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[22].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[23].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[23].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[23].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[24].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[24].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[24].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[25].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[25].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[25].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[26].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText_epc[26].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[26].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[27].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        editText_epc[27].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[27].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[28].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editText_epc[28].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[28].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[29].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText_epc[29].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[29].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    editText_epc[30].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editText_epc[30].addTextChangedListener(new TextWatcher() {

            public String text = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 4;
                if ( asciiFlag)
                    i = 2;
                setbits();
                if(editText_epc[30].length()==i){  // edit17  값의 제한값을 17이라고 가정했을때
                    //editText_epc[31].requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private String getmWriteValue_Hex() {
        return mWriteValue_Hex;
    }

    private void setmWriteValue_Hex(String value) {
        mWriteValue_Hex = value;
        txtWriteValue_Hex.setText(mWriteValue_Hex);
    }

    private String getmWriteValue_Ascii() {
        return mWriteValue_Ascii;
    }

    private void setmWriteValue_Ascii(String value) {
        mWriteValue_Ascii = value;
        txtWriteValue_Ascii.setText(mWriteValue_Ascii);
    }

    // ------------------------------------------------------------------------
    // CommonDialog Result Listener
    // ------------------------------------------------------------------------

    /*
    private CommonDialog.IStringDialogListener mWriteValueListener_Hex = new CommonDialog.IStringDialogListener() {

        @Override
        public void onConfirm(String value, DialogInterface dialog) {
            setmWriteValue_Hex(value);
            ATLog.i(TAG, "INFO. mWriteValueListener.$CommonDialog.IStringDialogListener.onConfirm([%s])", value);
        }
    };

    private CommonDialog.IAsciiStringDialogListener mWriteValueListener_Ascii = new CommonDialog.IAsciiStringDialogListener() {

        @Override
        public void onConfirm(String value, DialogInterface dialog) {
            setmWriteValue_Ascii(value);
            ATLog.i(TAG, "INFO. mWriteValueListener.$CommonDialog.IStringDialogListener.onConfirm([%s])", value);
        }
    };

     */


    // ------------------------------------------------------------------------
    // Ini File Initial Load
    // ------------------------------------------------------------------------



    // 신규로 추가한 소스 20200310 mino
    // epc 세틍하는 text view 에 커서가 포커스 될경우
    // background 색상을 변경한다 .

    public void onFocusChange(View view, boolean hasFocus) {

        switch (view.getId()) {
            case R.id.textView_epc1:
                if (hasFocus) {
                    editText_epc[0].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[0].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc2:
                if (hasFocus) {
                    editText_epc[1].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[1].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc3:
                if (hasFocus) {
                    editText_epc[2].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[2].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc4:
                if (hasFocus) {
                    editText_epc[3].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[3].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc5:
                if (hasFocus) {
                    editText_epc[4].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[4].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc6:
                if (hasFocus) {
                    editText_epc[5].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[5].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc7:
                if (hasFocus) {
                    editText_epc[6].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[6].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc8:
                if (hasFocus) {
                    editText_epc[7].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[7].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc9:
                if (hasFocus) {
                    editText_epc[8].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[8].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc10:
                if (hasFocus) {
                    editText_epc[9].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[9].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc11:
                if (hasFocus) {
                    editText_epc[10].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[10].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc12:
                if (hasFocus) {
                    editText_epc[11].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[11].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc13:
                if (hasFocus) {
                    editText_epc[12].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[12].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc14:
                if (hasFocus) {
                    editText_epc[13].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[13].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc15:
                if (hasFocus) {
                    editText_epc[14].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[14].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc16:
                if (hasFocus) {
                    editText_epc[15].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[15].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc17:
                if (hasFocus) {
                    editText_epc[16].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[16].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc18:
                if (hasFocus) {
                    editText_epc[17].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[17].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
            case R.id.textView_epc19:
                if (hasFocus) {
                    editText_epc[18].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[18].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;


            case R.id.textView_epc20:
                if (hasFocus) {
                    editText_epc[19].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[19].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;


            case R.id.textView_epc21:
                if (hasFocus) {
                    editText_epc[20].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[20].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc22:
                if (hasFocus) {
                    editText_epc[21].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[21].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc23:
                if (hasFocus) {
                    editText_epc[22].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[22].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc24:
                if (hasFocus) {
                    editText_epc[23].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[23].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc25:
                if (hasFocus) {
                    editText_epc[24].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[24].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc26:
                if (hasFocus) {
                    editText_epc[25].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[25].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc27:
                if (hasFocus) {
                    editText_epc[26].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[26].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc28:
                if (hasFocus) {
                    editText_epc[27].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[27].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc29:
                if (hasFocus) {
                    editText_epc[28].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[28].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc30:
                if (hasFocus) {
                    editText_epc[29].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[29].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;

            case R.id.textView_epc31:
                if (hasFocus) {
                    editText_epc[30].setBackgroundResource(R.drawable.fource_text_style);
                } else {
                    editText_epc[30].setBackgroundResource(R.drawable.edit_text_style);
                }
                break;
        }
    }

    @SuppressLint("WrongViewCast")
    private void setViewById()
    {

        editText_epc[0]        =  findViewById(R.id.textView_epc1);
        editText_epc[1]        =  findViewById(R.id.textView_epc2);
        editText_epc[2]        =  findViewById(R.id.textView_epc3);
        editText_epc[3]        =  findViewById(R.id.textView_epc4);
        editText_epc[4]        =  findViewById(R.id.textView_epc5);
        editText_epc[5]        =  findViewById(R.id.textView_epc6);
        editText_epc[6]        =  findViewById(R.id.textView_epc7);
        editText_epc[7]        =  findViewById(R.id.textView_epc8);
        editText_epc[8]        =  findViewById(R.id.textView_epc9);
        editText_epc[9]        =  findViewById(R.id.textView_epc10);
        editText_epc[10]       =  findViewById(R.id.textView_epc11);
        editText_epc[11]       =  findViewById(R.id.textView_epc12);
        editText_epc[12]       =  findViewById(R.id.textView_epc13);
        editText_epc[13]       =  findViewById(R.id.textView_epc14);
        editText_epc[14]       =  findViewById(R.id.textView_epc15);
        editText_epc[15]       =  findViewById(R.id.textView_epc16);
        editText_epc[16]       =  findViewById(R.id.textView_epc17);
        editText_epc[17]       =  findViewById(R.id.textView_epc18);


        editText_epc[18]       =  findViewById(R.id.textView_epc19);
        editText_epc[19]       =  findViewById(R.id.textView_epc20);
        editText_epc[20]       =  findViewById(R.id.textView_epc21);
        editText_epc[21]       =  findViewById(R.id.textView_epc22);
        editText_epc[22]       =  findViewById(R.id.textView_epc23);
        editText_epc[23]       =  findViewById(R.id.textView_epc24);
        editText_epc[24]       =  findViewById(R.id.textView_epc25);
        editText_epc[25]       =  findViewById(R.id.textView_epc26);
        editText_epc[26]       =  findViewById(R.id.textView_epc27);
        editText_epc[27]       =  findViewById(R.id.textView_epc28);
        editText_epc[28]       =  findViewById(R.id.textView_epc29);
        editText_epc[29]       =  findViewById(R.id.textView_epc30);

        editText_epc[30]       =  findViewById(R.id.textView_epc31);

        radioGroup_Hex_Ascii = findViewById(R.id.radioGroup_Hex_Ascii);
        textView_bits  = (TextView) findViewById(R.id.textView_bits );
    }

    public boolean setTextBoxVisible(int position, AdapterView<?> adapterView)
    {
        for (int i = 1 ; i < 1+position ; i++)
        {
            editText_epc[i].setVisibility(adapterView.VISIBLE);
        }

        for (int i = 1+position  ; i < epc.length ; i++)
        {
            editText_epc[i].setVisibility(adapterView.INVISIBLE);
        }

        return true ;
    }

    private void setbits()
    {
        int totalEpcLength = 0;

        for (int i = 0; i< epc.length ; i++)
        {
            totalEpcLength = totalEpcLength + editText_epc[i].length();
        }

        Log.e("#########  ", "  setbits  :" + String.format("| %d , %s",  totalEpcLength , asciiFlag ));

        if ( asciiFlag){
            textView_bits.setText(String.format("%dBit", totalEpcLength * 8));
        }else {

            textView_bits.setText(String.format("%dBit", totalEpcLength * 4));
        }
    }

        /*
        private void setEditBoxMaxLength() {
            if (asciiFlag == true) {
                for (int i = 0; i < epc.length; i++) {
                    editText_epc[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
                }

            } else {
                for (int i = 0; i < epc.length; i++) {
                    editText_epc[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                }
            }
        }*/

    // Clear Widgets
    //@Override
    protected void clear() {
     //   super.clear();

        txtSelection.setText("");
        txtMessage.setText("");

        for (int i = 0 ; i < epc.length ; i++)
        {
            epc[i] = "";
            editText_epc[i].setText (epc[i]) ;
        }
    }


    protected void  setFilter(boolean readonly) {

        if (readonly)
        {

            for (int i = 0; i < epc.length; i++) {

                InputFilter[] filters = (new InputFilter[]{
                        new InputFilter() {
                            public CharSequence filter(CharSequence src, int start,
                                                       int end, Spanned dst, int dstart, int dend) {

                                //ATLog.i(TAG , "##### EVENT. setFilter()1  (%s , %d, %d, )", src, start, end );

                                if (src.equals("")) {
                                    return src;
                                }
                               // if (src.toString().matches("^[0-9a-zA-Z]")) {

                                if (true) {
                                    return src;

                                }
                                  //  return src;
                                //}
                                //ATLog.i(TAG , "##### EVENT. setFilter()2  (%s , %d, %d, )", src, start, end );
                                return src;
                            }
                        },
                        new InputFilter.LengthFilter(4)
                });
                editText_epc[i].setFilters(filters);
            }


        }else{


            if (radioButton_Ascii.isChecked()) {

                ATLog.e(TAG , "##### ########## EVENT. setFilter()1  (%s )", radioButton_Ascii.isChecked());


                for (int i = 0; i < epc.length; i++) {

                    InputFilter[] filters = (new InputFilter[]{
                            new InputFilter() {
                                public CharSequence filter(CharSequence src, int start,
                                                           int end, Spanned dst, int dstart, int dend) {


                                    //ATLog.i(TAG , "##### EVENT. setFilter()1  (%s , %d, %d, )", src, start, end );

                                    if (src.equals("")) {
                                        return src;
                                    }
                                   // if (src.toString().matches("^[0-9a-zA-Z]")) {

                                    if (true) {
                                        return src;
                                    }
                                    //ATLog.i(TAG , "##### EVENT. setFilter()2  (%s , %d, %d, )", src, start, end );
                                    return "";
                                }
                            },
                            new InputFilter.LengthFilter(2)
                    });
                    editText_epc[i].setFilters(filters);
                }
            } else {

                ATLog.e(TAG , "##### ########## EVENT. setFilter()2  (%s )", radioButton_Ascii.isChecked() );

                for (int i = 0; i < epc.length; i++) {

                    InputFilter[] filters = (new InputFilter[]{
                            new InputFilter() {
                                public CharSequence filter(CharSequence src, int start,
                                                           int end, Spanned dst, int dstart, int dend) {
                                    //ATLog.i(TAG , "##### EVENT. setFilter()1  (%s , %d, %d, )", src, start, end );
                                    if (src.equals("")) {
                                        return src;
                                    }

                                    if (src.toString().matches("^[0-9a-fA-F]")) {
                                        return src;
                                    }
                                    //ATLog.i(TAG , "##### EVENT. setFilter()2  (%s , %d, %d, )", src, start, end );
                                    return "";
                                }
                            },
                            new InputFilter.LengthFilter(4)
                    });
                    editText_epc[i].setFilters(filters);
                } // for
            } // asciiFlag if
        } // read only


    }


    @Override
    public void onBackPressed() {
        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
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




    protected void resultMessage(ResultCode code) {
        String msg;
        int resId;

        if (code == ResultCode.NoError) {
            msg = "Success";
            resId = R.color.blue;
        } else {
            msg = code.toString();
            resId = R.color.red;
        }
      //  progWait.setVisibility(View.GONE);
        txtMessage.setText(msg);
       // layoutBack.setBackgroundResource(resId);
    }

    protected void setSelection(String epc) {
        txtSelection.setText(epc);
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
