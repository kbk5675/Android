package com.ata.rfiddemo.TagScan.Adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ata.rfiddemo.R;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class ListViewAdapter extends BaseAdapter {

    private static final String LOG_PATH = "Log";
    private static final boolean ENABLE_LOG = false;


    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView epc = (TextView) convertView.findViewById(R.id.epc) ;
        TextView readCount = (TextView) convertView.findViewById(R.id.readCount) ;
        TextView no = (TextView) convertView.findViewById(R.id.no) ;



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        epc.setText(listViewItem.getEpc());

        readCount.setText( listViewItem.getReadCount());

        no.setText(   String.format( "%d", listViewItem.getNo() )  );

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void clear() {
        this.listViewItemList.clear();
        this.notifyDataSetChanged();
    }

    public void setReadCount(int i, int readCount)
    {
        listViewItemList.get(i).setReadCount( String.format("%d", readCount) );
    }


    public int getReadCount(int i)
    {
        return   Integer.parseInt( listViewItemList.get(i).getReadCount() );
    }

    public String getEpc (int i)
    {
        return listViewItemList.get(i).getEpc();
    }


    public void setEpc (int i, String Epc)
    {
        listViewItemList.get(i).setEpc(Epc);
    }


    public String getAsciiEpc (int i)
    {
        return listViewItemList.get(i).getAsciiEpc();
    }


    public void setAsciiEpc (int i, String asciiEpc)
    {
        listViewItemList.get(i).setAsciiEpc(asciiEpc);
    }


    public String getHexEpc (int i)
    {
        return listViewItemList.get(i).getHexEpc();
    }


    public void setHexEpc (int i, String hexEpc)
    {
        listViewItemList.get(i).setHexEpc(hexEpc);
    }


    public String getAsciiPcEpc (int i)
    {
        return listViewItemList.get(i).getAsciiPcEpc();
    }


    public void setAsciiPcEpc (int i, String asciiPcEpc)
    {
        listViewItemList.get(i).setAsciiPcEpc(asciiPcEpc);
    }


    public String getHexPcEpc (int i)
    {
        return listViewItemList.get(i).getHexPcEpc();
    }


    public void setHexPcEpc (int i, String hexPcEpc)
    {
        listViewItemList.get(i).setHexPcEpc(hexPcEpc);
    }



    public void addItem(String epc, int readCount, int no)
    {
        ListViewItem item = new ListViewItem();

        item.setEpc(epc);
        item.setReadCount(  String.format("%d", readCount) );
        item.setNo(no);

        listViewItemList.add(item);

    }

    public void addItem(String epc, int no)
    {
        ListViewItem item = new ListViewItem();

        item.setEpc(epc);
        item.setNo(no);
        item.setReadCount( "" );

        listViewItemList.add(item);

    }



    public void excelSave(String workname)
    {


        try {
            WritableWorkbook workbook = null;
            WritableSheet sheet = null;
            Label label = null;
            File file = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS )  +"/"+  workname + ".xls");

            //List list = new ArrayList();

            // 파일 생성
            workbook = Workbook.createWorkbook(file);
            // 시트 생성
            workbook.createSheet(workname, 0);
            sheet = workbook.getSheet(0);

            // 셀에 쓰기
            label = new Label(0, 0, "No");
            sheet.addCell(label);

            label = new Label(1, 0, "epc");
            sheet.addCell(label);

            label = new Label(2, 0, "count");
            sheet.addCell(label);



            // 데이터 삽입
            for (int i = 0; i < listViewItemList.size() ; i++) {
                // HashMap rs = (HashMap) list.get(i);

                // no
                label = new Label(0, (i + 1),  String.format("%d", listViewItemList.get(i).getNo()   )   );
                sheet.addCell(label);
                // boxnum
                label = new Label(1, (i + 1), listViewItemList.get(i).getEpc() );
                sheet.addCell(label);

                label = new Label(2, (i + 1), listViewItemList.get(i).getReadCount()  );
                sheet.addCell(label);

            }
            workbook.write();
            workbook.close();
        }catch  (Exception e) {


            Log.e(" excelSave  DATA  ###########################  ",   ":"   + String.format( "|  %s ",   e.toString()  ));
        }


    }

}
