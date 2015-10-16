package com.go.fish.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;

/**
 * Created by DCloud on 2015/9/26.
 */
public class ActionSheet{

    Dialog mDialog = null;
    Context context;
    ActionSheetListener listener = null;
    ListView mListView = null;
    View mContentView = null;
    public ActionSheet(Context context, final String[] items,String title,final ActionSheetListener listener){
        this.context = context;
        this.listener = listener;
        View view = LayoutInflater.from(context).inflate(R.layout.acion_sheet,null);
        mContentView = view;
        TextView tv = (TextView)view.findViewById(R.id.base_head_bar_title);
        tv.setText(title);
        mListView = (ListView)view.findViewById(R.id.action_sheet_list);
        mListView.setAdapter(new ActionSheetAdapter(context, items));
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        };
        view.findViewById(R.id.action_sheet_cancel).setOnClickListener(clickListener);
        view.findViewById(R.id.action_sheet_ok).setOnClickListener(clickListener);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onSelected(position, items[position]);
                mDialog.dismiss();
            }
        });
//        FrameLayout rootView = new FrameLayout(context);
        int h = (int)context.getResources().getDimension(R.dimen.list_item_height);
//        rootView.setBackgroundColor(0x00ff0f00);
//        rootView.addView(mContentView, new FrameLayout.LayoutParams(-1, h * Math.max(5, items.length)));
        mDialog = ViewHelper.createDefaultDialog(context, mContentView, -1, h * Math.max(5, items.length), 0.95f);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void showActionSheet(){
        mDialog.show();
    }

    class ActionSheetAdapter extends BaseAdapter {
        Context context;
        String[] itemContents = null;
        ActionSheetAdapter(Context context,String[] items){
            this.context = context;
            this.itemContents = items;
        }
        @Override
        public int getCount() {
            return itemContents.length;
        }

        @Override
        public Object getItem(int position) {
            return itemContents[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.acion_sheet_item,null);
                viewHolder = new ViewHolder();
                viewHolder.mContentView = (TextView)convertView.findViewById(R.id.action_sheet_item_content);
                convertView.setTag(viewHolder);
                int h = (int)context.getResources().getDimension(R.dimen.list_item_height);
                convertView.setLayoutParams(new ListView.LayoutParams(-1,h));
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.mContentView.setText(itemContents[position]);
            return convertView;
        }

        class ViewHolder{
            TextView mContentView = null;
        }
    }

    static public interface ActionSheetListener{
        void onSelected(int position, String item);
    }
}
