package com.ata.rfiddemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.rfiddemo.Adapter.MemoryListAdapter;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.HiggsType;
import com.ata.rfiddemo.Util.Manager.SoundManager;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
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
import com.wsmr.lib.dev.rfid.type.ResultCode;
import com.wsmr.lib.diagnostics.ATLog;

import static android.os.SystemClock.sleep;

public class RfidRead extends ToolbarManager implements RfidReaderEventListener, View.OnClickListener {


    // ------------------------------------------------------------------------
    // Member Variable
    // ------------------------------------------------------------------------


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


    private ListView lstReadValue;
    private ListView lstReadTidValue;
    private ListView lstReadUserValue;


    private TextView txtLength;

    private Button btnAction;
    private Button btnClear;

    private MemoryListAdapter adpReadValue;
    private MemoryListAdapter adpReadTidValue;
    private MemoryListAdapter adpReadUserValue;


    private int mLength;

    private BankType bank;

    // higgs-4 higgs-ec
    private HiggsType ht = new HiggsType();
    private int htIndex = 0;
    private int anotherHtIndex = 0;
    private TextView textview_epcbits;
    private TextView textview_utidbits;
    private TextView textview_higgsType;
    private TextView textview_userbits;
    private TextView txtMessage;
    private TextView txtSelection;
    private TextView read_memory_param;
    private String maskEpc ;
    private String tagType;
    private String TAG="RFidRead" ;
    private SoundManager soundManager;

    private long mElapsedTick;
    private long mTick;
    private long SKIP_KEY_EVENT_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfid_read);

        try {

            //ApplicationClass.getmReader().stop();
            //sleep(300);
            ApplicationClass.getmReader().setOperationTime(0);

            ATLog.e(TAG, "============================================================ " );
            ATLog.e(TAG, " [%s]", ApplicationClass.getmReader().getAction());

            ATLog.e(TAG, "[%d] " ,  ApplicationClass.getmReader().getPower() );
            ATLog.e(TAG, "============================================================ " );

            int power = ApplicationClass.getmReader().getPower();

            if(power == -1){
                sleep(500);
                power = ApplicationClass.getmReader().getPower();
            }

            String srPower = "";
            SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
            srPower =  sharedpreferences.getString("read_power", "300");

            ApplicationClass.getmReader().setPower(Integer.parseInt(srPower));


        } catch (ATRfidReaderException e) {
            Toast.makeText(this, "getPowerRange() Error!!!", Toast.LENGTH_SHORT).show();
        }

        soundManager        = new SoundManager(this);

        //꺼짐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Initialize Read Value
        lstReadValue = (ListView) findViewById(R.id.read_value);
        adpReadValue = new MemoryListAdapter(this);
        lstReadValue.setAdapter(adpReadValue);

        lstReadTidValue = (ListView) findViewById(R.id.read_utid_value);
        adpReadTidValue = new MemoryListAdapter(this);
        lstReadTidValue.setAdapter(adpReadTidValue);

        lstReadUserValue = (ListView) findViewById(R.id.read_user_value);
        adpReadUserValue = new MemoryListAdapter(this);
        lstReadUserValue.setAdapter(adpReadUserValue);


        // Initialize Action Button
        btnAction = (Button) findViewById(R.id.btnAction);
        btnAction.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        textview_epcbits = findViewById(R.id.textview_epcbits);
        textview_utidbits = findViewById(R.id.textview_utidbits);
        textview_userbits = findViewById(R.id.textview_userbits);
        txtSelection = findViewById(R.id.selection);
        txtMessage = findViewById(R.id.message);
        textview_higgsType = findViewById(R.id.textview_higgsType);
        read_memory_param = findViewById(R.id.read_memory_param);

        SKIP_KEY_EVENT_TIME = getResources().getInteger(R.integer.skip_key_event_time);
        //tagType = tag_type.getText().toString();
        //Load_IniFile();
        //setToolbar(getResources().getString(R.string.read_memory ), this);

        if (ApplicationClass.getmReader() != null) {
            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                ApplicationClass.getmReader().stop();
            }
            ApplicationClass.getmReader().removeEventListener();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 138 && ApplicationClass.getmReader() != null && event.getRepeatCount() <= 0   ) {

            Log.e("onKeyDown  ", "!! #####################################################"   );


            if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
                return super.onKeyDown(keyCode, event);
            }

            startAction();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 138 && ApplicationClass.getmReader() != null   ) {
            actionStop();
        }

        return super.onKeyUp(keyCode, event);
    }

    public void actionStop()
    {

        buttonSetting(true);

        btnAction.setText(getResources().getString(R.string.action_read));

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }
    }

    private void buttonSetting(boolean enabled) {
        //btnSave.setEnabled(enabled);

    }



    protected void startAction() {

        clear();
        btnAction.setText("STOP");


        // tag_type.setText(tagType);
        bank = BankType.TID;
        action(bank);

    }



    private void action(BankType bk)
    {
        //꺼짐방지
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }


        ResultCode res;
        BankType bank = bk;
        //int offset = getOffset();
        //int length = getLength();

        int offset = 0;
        int length = 0;


        // epc 는 offset 이 큰 의미가 없음
        if (bank.equals( BankType.EPC ) ) {
            // higgs-ec
            offset = 0;
            length = 1;

        }else if (bank.equals( BankType.TID ) )
        {
            // higgs-ec
            offset = ht.tidOffset[0];
            length = ht.tidLength[0];

        }else if (bank.equals( BankType.User ) )
        {
            offset = ht.userOffset[htIndex];
            length = ht.userLength[htIndex];
        }

        String password = "";
        EpcMatchParam epc = new EpcMatchParam();

        //ApplicationClass.getmReader().readEpc6cTag();


        if ((res =  ApplicationClass.getmReader().readMemory6c(bank, offset, length )) != ResultCode.NoError) {
                ATLog.e(TAG, "ERROR. startAction() - Failed to read memory 6C tag [%s]", res);


                return;
        }




    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAction:
                if (btnAction.getText().equals(getResources().getString(R.string.action_read)) )
                {
                    startAction();
                }else if (btnAction.getText().equals("STOP") )
                {
                    actionStop();

                }

                break;

            case R.id.btnClear:
                    clear();
                break;
        }
    }

    @Override
    public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {

    }

    @Override
    public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {

    }

    @Override
    public void onReaderReadTag(ATRfidReader atRfidReader, String s, float v, float v1) {

        ATLog.e(TAG , "EVENT. onReaderReadTag(%s  " , s  );
    }


    @Override
    public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {

        ATLog.e(TAG , "EVENT. onReaderResult(%s, %s  " , s,  s1  );

        String epc = s;
        String data = s1;

        if (resultCode != ResultCode.NoError) {
            //adpReadValue.clear();
            //adpReadTidValue.clear();
            //adpReadUserValue.clear();

        } else {

            if (bank.equals( BankType.TID ) )
            {

                for (int i = 0;i < 5; i++)
                {
                    if (data.substring(4,8).toString().equals( ht.chipId[i] ))
                    {
                        htIndex = i;
                        break;
                    }else
                    {
                        htIndex = -1;
                    }
                }

                if (htIndex == -1)
                {
                    for (int i = 0;i < 10; i++)
                    {
                        if (data.substring(4,8).toString().equals( ht.anotherChipId[i] ))
                        {
                            anotherHtIndex = i;
                            break;
                        }else
                        {
                            anotherHtIndex = -1;
                        }
                    }
                    adpReadValue.setOffset(1);
                    adpReadValue.setValue(epc.substring(4));

                    if (anotherHtIndex == -1) {
                        textview_higgsType.setText(" " + "NK"); // Unknown
                    }else {
                        textview_higgsType.setText(" " + ht.anotherChip[anotherHtIndex]); // anotherChip
                    }

                    for (int i = 0 ; i < ht.arrayPc.length ; i++)
                    {
                        if (ht.arrayPc[i].equals(epc.substring(0,4)))
                        {
                            //textview_utidbits.setText("UTID : " + ht.bits[i] );
                            textview_epcbits.setText("EPC : " + ht.bits[i]);
                            ATLog.e(TAG, "###################11.  [%s] ", epc.substring(0, 4).toString() );
                            break;
                        }
                    }

                    ATLog.e(TAG, "###################22.  [%s] ", epc.substring(0, 4).toString() );

                    soundPlay();

                }else {

                    ATLog.i(TAG, "EVENT.  [%s] [%s]", data.substring(4, 8).toString(), ht.chipType[htIndex]);
                    adpReadTidValue.setOffset(1);
                    adpReadTidValue.setValue(data.substring(ht.uTidOffset[htIndex], ht.uTidLength[htIndex]));
                    textview_utidbits.setText("UTID : " + ht.utidBits[htIndex] + "bit");

                    // tag_type.setText(tag_type.getText().toString().substring(0,tag_type.getText().length()-1)  + ht.chip[htIndex]);
                    ATLog.e(TAG, "EVENT.  [%s] ", ht.chip[htIndex]);
                    textview_higgsType.setText(" " + ht.chip[htIndex]);
                    //textview_higgsType.setText (ht.chipType[htIndex].toString().substring(1) ) ;

                    maskEpc = "";
                    maskEpc = epc;


                    // epc
                    if (epc.substring(4).length() < Integer.parseInt(ht.epcBits[htIndex])) {
                        int j = (Integer.parseInt(ht.epcBits[htIndex]) - epc.substring(4).length() * 4) / 16;

                        ATLog.i(TAG, "############EPC  [%d] %d %d ", Integer.parseInt(ht.epcBits[htIndex]), epc.substring(4).length() * 4,
                                (Integer.parseInt(ht.epcBits[htIndex]) - epc.substring(4).length() * 4) / 16);

                        for (int i = 0; i < j; i++) {
                            epc = epc + "0000";
                        }
                    }

                    adpReadValue.setOffset(1);
                    adpReadValue.setValue(epc.substring(4));
                    //SysUtil.sleep(100);
                    //textview_epcbits.setText("EPC : " + ht.epcBits[htIndex] + "bit");

                    for (int i = 0 ; i < ht.arrayPc.length ; i++)
                    {
                        if (ht.arrayPc[i].equals(epc.substring(0,4)))
                        {
                            //textview_utidbits.setText("UTID : " + ht.bits[i] );
                            textview_epcbits.setText("EPC : " + ht.bits[i]);
                            ATLog.e(TAG, "###################11.  [%s] ", epc.substring(0, 4).toString() );
                            break;
                        }
                    }

                    txtSelection.setText(s);
                    txtMessage.setText(resultCode.toString() );

                }

            }


            /*
            if (bank.equals( BankType.EPC ) )
            {
                if ( false   )
                {
                    // bank = BankType.TID;
                }else {

                    if (epc.substring(4).length() < Integer.parseInt(ht.epcBits[htIndex])) {
                        int j = (Integer.parseInt(ht.epcBits[htIndex]) - epc.substring(4).length() * 4) / 16;

                        ATLog.i(TAG, "############EPC  [%d] %d %d ", Integer.parseInt(ht.epcBits[htIndex]), epc.substring(4).length() * 4,
                                (Integer.parseInt(ht.epcBits[htIndex]) - epc.substring(4).length() * 4) / 16);

                        for (int i = 0; i < j; i++) {
                            epc = epc + "0000";
                        }
                    }

                    adpReadValue.setOffset(1);
                    adpReadValue.setValue(epc.substring(4));
                    //SysUtil.sleep(100);
                    textview_epcbits.setText("EPC : " + ht.epcBits[htIndex] + "bit");

                    txtSelection.setText(epc);
                }

            }*/



            if (bank.equals( BankType.User ) )
            {
                if ( false )
                {
                    //bank = BankType.EPC;
                }else {
                    adpReadUserValue.setOffset(1);
                    adpReadUserValue.setValue(data);
                    textview_userbits.setText("USER DATA : " + ht.userBits[htIndex] + "bit");
                    soundPlay();
                }

                btnAction.setText(getResources().getString(R.string.action_read) );
            }


            if ( htIndex != -1 ) {
                if (bank.equals(BankType.TID)) {
                    //   startEpcAction();
                    // } else if (bank.equals(BankType.EPC)) {
                    startUserAction();
                    soundPlay();
                }
            }
        }

    }


    protected void startUserAction() {

        adpReadUserValue.clear();
        textview_userbits.setText("USER DATA : ");

    //    txtSelection.setText("");
    //    txtMessage.setText("");


        bank = BankType.User;
        action(bank);
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


    @Override
    protected void onDestroy() {

        if (soundManager != null) {
            soundManager.close();
        }

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }

        super.onDestroy();
    }


    protected void clear() {
        textview_epcbits.setText("EPC : ");
        textview_utidbits.setText("UTID : ");
        textview_userbits.setText("USER DATA : ");
        textview_higgsType.setText("");
        // tag_type.setText(tagType);
        read_memory_param.setText(R.string.read_memory_param);
        adpReadValue.clear();
        adpReadTidValue.clear();
        adpReadUserValue.clear();

        txtSelection.setText("");
        txtMessage.setText("" );

    }

    @Override
    public void onBackPressed() {

        if (ApplicationClass.getmReader().getAction() != ActionState.Stop) {
            ApplicationClass.getmReader().stop();

        }


        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
