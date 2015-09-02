package com.go.fish.view;

/**
 * Created by DCloud on 2015/8/23.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;

import java.util.ArrayList;

public class PopWinListItemAdapter extends BaseAdapter {

    ArrayList<String> itemsData = null;
    Context context = null;
    int itemResId ;
    public static PopWinListItemAdapter newInstance(Context context,ListView list,int datasResId,int itemResId){
        String[] sDatas = context.getResources().getStringArray(datasResId);
        return new PopWinListItemAdapter(context,list,sDatas,itemResId);
    }
    public static PopWinListItemAdapter newInstance(Context context,ListView list,String[] datas){
        return new PopWinListItemAdapter(context,list,datas,0);
    }
    private PopWinListItemAdapter(Context context,ListView list,String[] sDatas,int itemResId){
        this.context = context;
        this.itemResId = itemResId;
        ArrayList<String> datas = new ArrayList<String>();
        for(String s : sDatas){
            datas.add(s);
        }
        itemsData = datas;
        list.setAdapter(this);
        list.setDivider(new ColorDrawable(Color.GRAY));
        list.setDividerHeight(context.getResources().getDimensionPixelSize(R.dimen.list_item_divider));
    }
    @Override
    public int getCount() {
        return itemsData.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        String data = itemsData.get(position);
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_pop_win, null);
            ((TextView)view.findViewById(R.id.pop_list_item_text)).setText(data);
            TextView status =(TextView)view.findViewById(R.id.pop_list_item_status_text);
            if(itemResId == R.layout.listitem_pop_win) {
                status.setText("√");
                status.setTextColor(Color.GRAY);
            }else{
                status.setVisibility(View.GONE);
            }
        }else{
            view = convertView;
        }
        view.setTag(data);
        return view;
    }

}