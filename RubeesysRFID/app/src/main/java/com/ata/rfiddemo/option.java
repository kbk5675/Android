package com.ata.rfiddemo;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ata.rfiddemo.Util.ApplicationClass;
import com.wsmr.lib.dev.rfid.exception.ATRfidReaderException;
import com.wsmr.lib.dev.rfid.param.RangeValue;

public class option extends AppCompatActivity {


    private RangeValue mPowerRange;
    private TextView textView_q_max;
    private TextView textView_q_min;

    public option() {
        super();
        mPowerRange = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //꺼짐방지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textView_q_max  = findViewById(R.id.q_max);
        textView_q_min  = findViewById(R.id.q_min);

        try  {
            mPowerRange = ApplicationClass.getmReader().getPowerRange();

            textView_q_max.setText(String.format(getResources().getString(R.string.count), mPowerRange.getMax()  ) );
            textView_q_min.setText(String.format(getResources().getString(R.string.count), mPowerRange.getMin() + 50  )  );

        } catch (ATRfidReaderException e) {



        }






    }

    @Override
    protected void onStart() {
        //WaitDialog.show(this, R.string.load_option);
        super.onStart();
    }




}
