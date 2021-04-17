package com.ata.rfiddemo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ata.rfiddemo.R;
import com.wsmr.lib.diagnostics.ATLog;
import com.wsmr.lib.util.StringUtil;

import java.util.ArrayList;
import java.util.Locale;

public class MemoryListAdapter extends BaseAdapter {

    private static final String TAG = "MemooryListAdapter";
    private static final boolean DEBUG_ENABLED = false;

    private static final int MAX_COL = 4;
    private static final int WORD_LENGTH = 16;

    private LayoutInflater mInflater;
    private ArrayList<MemoryListItem> mList;
    private int mOffset;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    public MemoryListAdapter(Context context) {
        super();

        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = new ArrayList<MemoryListItem>();
        mOffset = 0;
        MemoryListItem item = new MemoryListItem();
        mList.add(item);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // Clear Adapter List
    public void clear() {
        mOffset = 0;
        mList.clear();
        MemoryListItem item = new MemoryListItem();

        // mino 초기화 하기  //
        //mList.add(item);
        notifyDataSetChanged();
        if(DEBUG_ENABLED)
            ATLog.d(TAG, "DEBUG. clear([%s])", item);
    }

    // Set Display Offset
    public void setOffset(int offset) {
        mOffset = offset * WORD_LENGTH;
    }

    // Set Memory Value
    public void setValue(String tag) {
        int row = tag.length() / MemoryListItem.MAX_DISPLAY_LENGTH;
        int i = 0;

        mList.clear();
        //ATLog.e(TAG, "##########setValue : [%s], [%d], [%d] ", tag ,  tag.length()  , tag.length() / MemoryListItem.MAX_DISPLAY_LENGTH  );

        int j = 0;
        for (i = 0; i < row; i++) {
            mList.add(new MemoryListItem((i*4 * WORD_LENGTH) + mOffset, tag.substring(i * MemoryListItem.MAX_DISPLAY_LENGTH,  (i * MemoryListItem.MAX_DISPLAY_LENGTH)  + MemoryListItem.MAX_DISPLAY_LENGTH)));
        }

        if (tag.length() % MemoryListItem.MAX_DISPLAY_LENGTH != 0) {
           //s ATLog.e(TAG, "##########setValue IF  ???????????????????????" );
            mList.add(new MemoryListItem(( i * 4 * WORD_LENGTH) + mOffset, tag .substring(i * MemoryListItem.MAX_DISPLAY_LENGTH)));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MemoryListViewHolder holder;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.item_memory_list, parent,
                    false);
            holder = new MemoryListViewHolder(convertView);
        } else {
            holder = (MemoryListViewHolder) convertView.getTag();
        }
        holder.setItem(mList.get(position));
        if(DEBUG_ENABLED)
            ATLog.d(TAG, "DEBUG. getView([%s])", holder);
        return convertView;
    }

    // ------------------------------------------------------------------------
    // Internal Class MemoryListListItem
    // ------------------------------------------------------------------------

    private class MemoryListItem {

        public static final int MAX_DISPLAY_LENGTH = 16;
        private static final int DISPLAY_VALUE_LENGTH = 4;

        private String[] address;
        private String[] value;

        public MemoryListItem() {
            address = new String[] { "16bit", "32bit", "64bit", "80bit" };
            value = new String[] { "0000", "0000", "0000", "0000" };
        }

        public MemoryListItem(int offset, String tag) {
            String data = StringUtil.padRight(tag, MAX_DISPLAY_LENGTH, '0');

            address = new String[MAX_COL];
            value = new String[MAX_COL];

            for (int i = 0; i < ( tag.length() / 4 ) ; i++) {

                //ATLog.e(TAG, "##########MemoryListItem : [%d], [%d] [%s] ", offset, offset + (i * WORD_LENGTH), tag );

                address[i] = String.format(Locale.US, "%dbit", offset + (i * WORD_LENGTH));
                value[i] = data.substring(i * MAX_COL, (i * MAX_COL)
                        + DISPLAY_VALUE_LENGTH);
            }
        }

        public String getAddress(int index) {
            return address[index];
        }

        public String getValue(int index) {
            return value[index];
        }

        @Override
        public String toString() {
            return String.format(Locale.US,
                    "{{[%s], [%s], [%s], [%s]}, {[%s], [%s], [%s], [%s]}}",
                    address[0], address[1], address[2], address[3], value[0],
                    value[1], value[2], value[3]);
        }
    }

    // ------------------------------------------------------------------------
    // Internal Class MemoryListViewHolder
    // ------------------------------------------------------------------------

    private class MemoryListViewHolder {

        private TextView[] address;
        private TextView[] value;

        public MemoryListViewHolder(View parent) {
            address = new TextView[] {
                    (TextView) parent.findViewById(R.id.address1),
                    (TextView) parent.findViewById(R.id.address2),
                    (TextView) parent.findViewById(R.id.address3),
                    (TextView) parent.findViewById(R.id.address4) };
            value = new TextView[] {
                    (TextView) parent.findViewById(R.id.value1),
                    (TextView) parent.findViewById(R.id.value2),
                    (TextView) parent.findViewById(R.id.value3),
                    (TextView) parent.findViewById(R.id.value4) };
            parent.setTag(this);
        }

        public void setItem(MemoryListItem item) {
            if(DEBUG_ENABLED)
                ATLog.d(TAG, "DEBUG. setItem(%s)", item);

            for (int i = 0; i < MAX_COL; i++) {
                address[i].setText(item.getAddress(i));
                value[i].setText(item.getValue(i));
            }
        }

        @Override
        public String toString() {
            return String.format(Locale.US,
                    "{{[%s], [%s], [%s], [%s]}, {[%s], [%s], [%s], [%s]}}",
                    address[0].getText(), address[1].getText(),
                    address[2].getText(), address[3].getText(),
                    value[0].getText(), value[1].getText(), value[2].getText(),
                    value[3].getText());
        }
    }
}
