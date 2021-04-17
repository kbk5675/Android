package com.kbk.samplelistview;

import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.CheckBox;

public class ListViewItem {

    private Drawable drawable;
    private String text1;
    private String text2;
    private CheckBox checkBox;

    public CheckBox getCheckBox() { return checkBox; }
    public void setCheckBox(CheckBox checkBox) { this.checkBox = checkBox;}

    public Drawable getDrawable() { return drawable; }
    public String getText1() { return text1; }
    public String getText2() { return text2; }


    public void setDrawable(Drawable drawable) { this.drawable = drawable; }
    public void setText1(String text1) { this.text1 = text1; }
    public void setText2(String text2) { this.text2 = text2; }
}
