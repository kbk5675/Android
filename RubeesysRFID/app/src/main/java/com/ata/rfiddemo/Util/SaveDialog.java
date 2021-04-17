package com.ata.rfiddemo.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ata.rfiddemo.R;

public class SaveDialog extends Dialog {

    private TextView textView_title;
    private EditText editText_fileName;
    private TextView textView_save;
    private TextView textView_cancel;

    private String title;
    private String fileName;
    private View.OnClickListener mPositiveListener;

    public SaveDialog(Context context, String title, String fileName, View.OnClickListener positiveListener) {
        super(context);
        this.title             = title;
        this.fileName          = fileName;
        this.mPositiveListener = positiveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_file);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setAttributes(layoutParams);

        textView_title    = findViewById(R.id.textView_title);
        editText_fileName = findViewById(R.id.editText_fileName);
        textView_save     = findViewById(R.id.textView_save);
        textView_cancel   = findViewById(R.id.textView_cancel);

        textView_title.setText(title);
        editText_fileName.setText(fileName);
        textView_save.setOnClickListener(mPositiveListener);
        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowing()) {
                    dismiss();
                }
            }
        });
    }

    public String getFileName() {
        if (editText_fileName.getText() == null) {
            return null;
        }

        return editText_fileName.getText().toString();
    }
}
