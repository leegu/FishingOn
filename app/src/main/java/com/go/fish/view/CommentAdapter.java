package com.go.fish.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.CommentData;

/**
 * Created by DCloud on 2015/8/23.
 */
public class CommentAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    ArrayList<CommentData> listDatas = null;
    int itemHeight = 0;
    private CommentAdapter(ArrayList<CommentData> datas){
        listDatas = datas;
    }
    public static void fillToListView(ListView list,ArrayList<CommentData> datas){
        CommentAdapter ma = new CommentAdapter(datas);
        ma.inflater = LayoutInflater.from(list.getContext());
        //ma.itemHeight = (int)list.getContext().getResources().getDimension(R.dimen.list_item_height);
        list.setAdapter(ma);
    }
    public static void fillToListView(ListView list,JSONArray arr){
        ArrayList<CommentData> datas = new ArrayList<CommentData>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                CommentData itemData = CommentData.newInstance(arr.getJSONObject(i));
                datas.add(itemData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fillToListView(list, datas);
    }

    @Override
    public int getCount() {
        return listDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return listDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup item = null;
        TextView mainLabeView = null;
        if(convertView == null){
            item = (ViewGroup)inflater.inflate(R.layout.listitem_comment,null);
            ViewHolder holder = new ViewHolder();
            holder.rootView = item;
            item.setTag(holder);
//            item.setLayoutParams(new ListView.LayoutParams(-1,itemHeight));
        }else{
            item = (ViewGroup)((ViewHolder)convertView.getTag()).rootView;
        }
        mainLabeView = (TextView)item.findViewById(R.id.comment_listitem_name);
        CommentData itemData = listDatas.get(position);
        mainLabeView.setText(itemData.uname);
        TextView commentText = (TextView)item.findViewById(R.id.comment_listitem_text);
        commentText.setText(itemData.text);
        TextView timeView = (TextView)item.findViewById(R.id.comment_listitem_time);
        timeView.setText(""+itemData.commentTime);
        return item;
    }
}
