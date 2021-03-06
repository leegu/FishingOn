package com.go.fish.view;

import java.util.ArrayList;

import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.MyListitemData;

/**
 * Created by DCloud on 2015/8/23.
 */
public class MyListitemAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    ArrayList<MyListitemData> listDatas = null;
    int itemHeight = 0;
    private MyListitemAdapter(ArrayList<MyListitemData> datas){
        listDatas = datas;
    }
    public static void fillToListView(ListView list,ArrayList<MyListitemData> datas){
        MyListitemAdapter ma = new MyListitemAdapter(datas);
        ma.inflater = LayoutInflater.from(list.getContext());
        ma.itemHeight = (int)list.getContext().getResources().getDimension(R.dimen.list_item_height);
        list.setAdapter(ma);
    }
    public static void fillToListView(ListView list,String[] strArr,int[] iconArr){
        ArrayList<MyListitemData> datas = new ArrayList<MyListitemData>();
        for (int i = 0; i < strArr.length; i++) {
            MyListitemData itemData = new MyListitemData();
            itemData.label = strArr[i];
            itemData.leftIconId = iconArr[i];
            datas.add(itemData);
        }
        fillToListView(list, datas);
    }
    public static void fillToListView(ListView list, int labelsResId, int leftIconsResId){
        String[] strArr = list.getContext().getResources().getStringArray(labelsResId);
        TypedArray ar = list.getContext().getResources().obtainTypedArray(leftIconsResId);
        int len = ar.length();
        int[] icons = new int[len];
        for (int i = 0; i < len; i++)
            icons[i] = ar.getResourceId(i, 0);
        ar.recycle();
        fillToListView(list,strArr,icons);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listDatas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    class MyListItemViewHolder{
        TextView labelView,bedgerNumberView,sublabelView,lineView;
        TextView leftIcon;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        MyListItemViewHolder viewHolder = null;
        if(item == null){
            item = (ViewGroup)inflater.inflate(R.layout.listitem_my,null);
            item.setLayoutParams(new ListView.LayoutParams(-1,itemHeight));
            viewHolder = new MyListItemViewHolder();
            item.setTag(viewHolder);
            viewHolder.labelView = (TextView)item.findViewById(R.id.ui_f_my_listitem_label);
            viewHolder.leftIcon = (TextView)item.findViewById(R.id.ui_f_my_listitem_pic_left);
            viewHolder.bedgerNumberView = (TextView)item.findViewById(R.id.ui_f_my_listitem_bedger);
            viewHolder.sublabelView = (TextView)item.findViewById(R.id.ui_f_my_listitem_sublabel);
            viewHolder.lineView = (TextView)item.findViewById(R.id.line);
        }else{
            viewHolder = (MyListItemViewHolder)convertView.getTag();
        }
        MyListitemData itemData = listDatas.get(position);
        viewHolder.labelView.setText(itemData.label);

        if(position == listDatas.size() - 1){
            viewHolder.lineView.setVisibility(View.INVISIBLE);
        }
        if(itemData.leftIconId > 0){
            viewHolder.leftIcon.setBackgroundResource(itemData.leftIconId);
        }else if(itemData.leftIconBitmap != null && !itemData.leftIconBitmap.isRecycled()){
            viewHolder.leftIcon.setBackgroundDrawable(new BitmapDrawable(itemData.leftIconBitmap));
        }
        if(itemData.bedgerNumber > 0){
            TextView label = viewHolder.bedgerNumberView;
            label.setVisibility(View.VISIBLE);
            label.setText(String.valueOf(itemData.bedgerNumber));
        }
        if(!TextUtils.isEmpty(itemData.subLabel)){
            TextView subLabel = viewHolder.sublabelView;
            subLabel.setVisibility(View.VISIBLE);
            subLabel.setText(itemData.subLabel);
        }
        return item;
    }
}
