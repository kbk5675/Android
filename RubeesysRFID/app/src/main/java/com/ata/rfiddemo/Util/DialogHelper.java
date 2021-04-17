package com.ata.rfiddemo.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ata.rfiddemo.R;

public class DialogHelper {

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private DialogInterface.OnClickListener mPositiveClickListener;

    public DialogHelper(DialogInterface.OnClickListener positiveClickListener) {
        this.mPositiveClickListener = positiveClickListener;
    }

    public void DialogShow(Context context, String title, String message) {
        alertDialogBuilder = new AlertDialog.Builder(context, R.style.DefaultAlertDialog);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.ok), mPositiveClickListener);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.black));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.RE43F4A));
    }

    public void DialogDismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}
