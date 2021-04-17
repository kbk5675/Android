package com.ata.rfiddemo.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ata.rfiddemo.ItemCheck.ItemCheckActivity;
import com.ata.rfiddemo.MainActivity;
import com.ata.rfiddemo.R;
import com.ata.rfiddemo.Setting.Adapter.LanguageAdapter;
import com.ata.rfiddemo.TagScan.TagScanActivity;
import com.ata.rfiddemo.Util.ApplicationClass;
import com.ata.rfiddemo.Util.Manager.ToolbarManager;
import com.ata.rfiddemo.WriteMemory;
import com.wsmr.lib.dev.rfid.exception.ATRfidReaderException;
import com.wsmr.lib.dev.rfid.param.RangeValue;
import com.wsmr.lib.util.SysUtil;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static android.os.SystemClock.sleep;

public class SettingActivity extends ToolbarManager implements BubbleSeekBar.OnProgressChangedListener, AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private BubbleSeekBar seekbar_power;
    private BubbleSeekBar write_power;


    private Spinner spinner_language;
    private TextView textView_version;
    private TextView textView_Rversion;
    private CheckBox checkBox_Ascii;
    private CheckBox checkBox_Beef;

    private TextView textView_Data;
    private TextView textGunBattery;
    private TextView textPdaBattery;


    private SpinnerAdapter spinnerAdapter;
    private ArrayList<String> languageList;

    private RangeValue mPowerRange;
    private String backPageCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);




        setToolbar(getResources().getString(R.string.setting));

        sharedpreferences  = getSharedPreferences("session", Context.MODE_PRIVATE);
        editor             = sharedpreferences.edit();

        seekbar_power    = findViewById(R.id.seekbar_power);
        write_power      = findViewById(R.id.write_power);

        spinner_language = findViewById(R.id.spinner_language);
        textView_version = findViewById(R.id.textView_version);
        //textView_Data = findViewById(R.id.textView_Data);
        checkBox_Ascii = findViewById(R.id.checkBox_Ascii);
        checkBox_Beef = findViewById(R.id.checkBox_Beef);
        textGunBattery = findViewById(R.id.gunBattery);
        textPdaBattery = findViewById(R.id.pdaBattery);

        //textView_Rversion = findViewById(R.id.textView_Rversion);

        backPageCheck = null;
        Intent intent = getIntent();
        backPageCheck = intent.getStringExtra("back");

        // language spinner
        languageList= new ArrayList<String>();
        Collections.addAll(languageList, getResources().getStringArray(R.array.language_array));
        spinnerAdapter = new LanguageAdapter(this, languageList);
        spinner_language.setAdapter(spinnerAdapter);

        Locale currentLocale = getResources().getConfiguration().getLocales().get(0);
        String language = currentLocale.getLanguage();

        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).equalsIgnoreCase(language)) {
                spinner_language.setSelection(i);
                break;
            }
        }

        seekbar_power.setOnProgressChangedListener(this);
        write_power.setOnProgressChangedListener(this);
        spinner_language.setOnItemSelectedListener(this);

        String asciiFlag = "";
        asciiFlag =  sharedpreferences.getString("asciiFlag", "true");
        if ("true".equals(asciiFlag) ) {
            checkBox_Ascii.setChecked(true);
        }

        checkBox_Ascii.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //ApplicationClass.setAsciiFlag(true);
                    editor.putString("asciiFlag", "true");
                    editor.commit();

                } else {
                    editor.putString("asciiFlag", "false");
                    editor.commit();
                }
            }
        });


        String beefFlag = "";
        beefFlag =  sharedpreferences.getString("beefFlag", "true");
        if ("true".equals(beefFlag) ) {
            checkBox_Beef.setChecked(true);
        }else  if ("".equals(beefFlag))
        {
            checkBox_Beef.setChecked(true);
        }


        checkBox_Beef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //ApplicationClass.setAsciiFlag(true);
                    editor.putString("beefFlag", "true");
                    editor.commit();

                } else {
                    editor.putString("beefFlag", "false");
                    editor.commit();
                }
            }
        });


        //set gun battery
        textGunBattery.setText( SysUtil.remainBattery()  );
        //set pda battery
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float)scale ) ;
        float p = batteryPct * 100;
        textPdaBattery.setText("Battery : " + String.valueOf(Math.round(p)) + " %");

    }

    @Override
    protected void onStart() {
        super.onStart();

        // app version
        String version = "";

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pi.versionName;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        textView_version.setText(String.format(getResources().getString(R.string.app_version), version));

       // textView_Rversion.setText(String.format(getResources().getString(R.string.app_version), ApplicationClass.getRfidVersion()  ));

       // textView_Data.setText (  String.format("Data :  %s" ,  ApplicationClass.getmReader().getState().toString()  )) ;

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ApplicationClass.getmReader() != null) {
            // power setting
            int power = 0;

            try {
                mPowerRange = ApplicationClass.getmReader().getPowerRange();
                power = ApplicationClass.getmReader().getPower();

                if(power == -1){
                    sleep(300);
                    power = ApplicationClass.getmReader().getPower();
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


            String swPower = "";
            swPower =  sharedpreferences.getString("write_power", "300");

            write_power.getConfigBuilder()
                    .max(mPowerRange.getMax())
                    .min(0) // 이하로 설정 시 에러
                    .progress( Integer.parseInt(swPower))
                    .build();

        }
    }

    @Override
    public void onBackPressed() {
        // 언어 설정 저장
        editor.putString("language", spinner_language.getSelectedItem().toString());
        editor.commit();

        if (backPageCheck != null) {

            if (backPageCheck.equals("RpidScan"))
            {
                startActivity(new Intent(this, ItemCheckActivity.class));
            }else if (backPageCheck.equals("WriteMemory"))  {
                startActivity(new Intent(this, WriteMemory.class));
            }
            else {
                startActivity(new Intent(this, TagScanActivity.class));
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    // seekbar
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

    // spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String code = spinner_language.getSelectedItem().toString();
        changeConfiguration(code);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void changeConfiguration(String code) {
        Locale mLocale = new Locale(code);
        Configuration config = new Configuration();
        config.setLocale(mLocale);
        getResources().updateConfiguration(config, null);
    }
}
