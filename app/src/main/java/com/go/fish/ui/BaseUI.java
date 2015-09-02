package com.go.fish.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.data.DataMgr;
import com.go.fish.data.FNewsData;
import com.go.fish.data.FPlaceData;
import com.go.fish.ui.pic.ImageViewUI;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.NetTool;
import com.go.fish.view.AdapterExt;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.FPlaceListAdapter;
import com.go.fish.view.IBaseData;
import com.go.fish.view.IMETools;
import com.go.fish.view.Switcher;
import com.go.fish.view.ViewHelper;

import org.json.JSONArray;
import org.json.JSONException;

public class BaseUI extends FragmentActivity implements IHasHeadBar, IHasTag {

	 public void onPreCreate(Bundle savedInstanceState){
	    	
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        onPreCreate(savedInstanceState);
        int layout_id = getIntent().getIntExtra(Const.LAYOUT_ID, 0);
        if (layout_id != 0) setContentView(layout_id);
        switch (layout_id) {
            case R.layout.ui_webview_left_close:
                onCreateWebview();
                break;
            case R.layout.ui_my_f_news:
                onCreateMyFNewsView();
                break;
            case R.layout.ui_near_fplace:
                onCreateNearFPlace();
                break;
            case R.layout.ui_near_friends:
                onCreateNearFriend();
                break;
            case R.layout.ui_my_care:
                onCreateMyCare();
                break;
            case R.layout.ui_comment_publish:
                onCreateCommentPublish();
                break;
            default:
                break;
        }
    }

    private void onCreateCommentPublish() {//创建钓播发布页面
        final EditText et = (EditText) findViewById(R.id.comment_text);
        TextView t = (TextView)findViewById(R.id.comment_has_len);
        et.setSelection(et.getText().length());
        final int maxCount = getResources().getInteger(R.integer.comment_max_count);
        t.setText(String.format(getString(R.string.input_text_count),maxCount - et.getText().length() ));
        IMETools.showIME(et);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView t = (TextView) findViewById(R.id.comment_has_len);
                int lessCount = 0;
                if (et.getText().length() >= maxCount) {
                    et.removeTextChangedListener(this);
                    et.setText(et.getText().subSequence(0, maxCount));
                    et.setSelection(et.getText().length());
                    et.addTextChangedListener(this);
                }
                t.setText(String.format(getString(R.string.input_text_count), maxCount - et.getText().length()));
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        }) ;
    }
    private void onCreateMyCare() {//创建我的关注 钓场 钓友
        ViewGroup vg = (ViewGroup)findViewById(R.id.ui_my_care_list_root);
        {//钓播
            final ListView fNewsList = new ListView(this);
            fNewsList.setVisibility(View.GONE);
            vg.addView(fNewsList);
            //本地先获取显示
            String careFPlace = LocalMgr.loadData(Const.DB_MY_CARE_FNEWS);
            ArrayList<IBaseData> arr = new ArrayList<IBaseData>();
            JSONArray jsonArr = null;
            try {
                if(!TextUtils.isEmpty(careFPlace)){
                    jsonArr = new JSONArray(careFPlace);
                }else{
                    jsonArr = new JSONArray();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //friend界面此时最接近
            final AdapterExt mListAdapter = AdapterExt.newInstance(BaseUI.this, jsonArr, R.layout.listitem_friend);
            fNewsList.setAdapter(mListAdapter);
            //网络数据抓取,进行更新
            NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
                @Override
                public void onStart() {
                	ViewHelper.showGlobalWaiting(BaseUI.this, null);
                }

                @Override
                public void onEnd(byte[] data) {
                    try {
                        String str = new String(data,"utf-8");
                        JSONArray arr= new JSONArray(str);
                        ArrayList<IBaseData> arrBaseData = new ArrayList<IBaseData>();
                        arrBaseData.add(FNewsData.newInstance(""));
                        arrBaseData.add(FNewsData.newInstance(""));
                        mListAdapter.updateAdapter(arrBaseData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        ViewHelper.closeGlobalWaiting();
                    }
                }
            }, "my_friend");
            NetTool.httpGet(rd.syncCallback());
        }
        {//钓场
            ListView fPlaceList = new ListView(this);
            vg.addView(fPlaceList);
            //本地先获取显示
            String careFPlace = LocalMgr.loadData(Const.DB_MY_CARE_FPLACE);
            JSONArray jsonArr = null;
            try {
                if(!TextUtils.isEmpty(careFPlace)){
                    jsonArr = new JSONArray(careFPlace);
                }else{
                    jsonArr = new JSONArray();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(R.layout.listitem_fpalce, jsonArr);
            final FPlaceListAdapter mListAdapter = new FPlaceListAdapter(this, fPlaceArr);
            fPlaceList.setAdapter(mListAdapter);
            //网络数据抓取,进行更新
            NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onEnd(byte[] data) {
                    try {
                        String str = new String(data,"utf-8");
                        mListAdapter.updateAdapter(DataMgr.makeFPlaceDatas(R.layout.listitem_fpalce, new JSONArray(str).getJSONArray(0)));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, "fishing_place_near");
            NetTool.httpGet(rd.syncCallback());
        }
    }

    private void onCreateNearFPlace() {//创建附近钓场
        String[] tabItemsTitle = getResources().getStringArray(R.array.hfs_splace_type);
        ViewGroup vg = (ViewGroup) findViewById(R.id.ui_near_fplace_root);
        final ViewGroup mainVG = ViewHelper.newMainView(this, getSupportFragmentManager(), new BaseFragment.ResultForActivityCallback() {
            @Override
            public void onItemClick(View view, FPlaceData data) {

            }
        }, tabItemsTitle);
        vg.addView(mainVG);
        BaseFragmentPagerAdapter.initAdapterByNetData((ViewPager) mainVG.findViewById(R.id.search_viewpager),R.layout.listitem_fpalce);
    }

    private void onCreateNearFriend() {//创建附近钓友
        NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
            @Override
            public void onStart() {
            	ViewHelper.showGlobalWaiting(BaseUI.this, null);
            }

            @Override
            public void onEnd(byte[] data) {
                ListView list = (ListView)findViewById(R.id.ui_near_f_friends_listview);
                JSONArray arr = null;
                try {
                    arr = new JSONArray(new String(data,"utf-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                list.setAdapter(AdapterExt.newInstance(BaseUI.this,arr,R.layout.listitem_friend ));
                ViewHelper.closeGlobalWaiting();
            }
        },"near_friend");
        NetTool.httpGet(rd.syncCallback());
    }
    private void onCreateMyFNewsView(){//创建我的钓播
        NetTool.RequestData rd = NetTool.RequestData.newInstance(new NetTool.RequestListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onEnd(byte[] data) {
                ListView list = (ListView)findViewById(R.id.my_f_news_list);
                JSONArray arr = null;
                try {
                    arr = new JSONArray(new String(data,"utf-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                list.setAdapter(AdapterExt.newInstance(BaseUI.this,arr,R.layout.listitem_fnews ));
            }
        },"my_fnews");
        NetTool.httpGet(rd.syncCallback());
    }
   

    public void onIconClick(View view) {
//    	UIMgr.showActivity(this,R.layout.ui_pic_selecte,PicUI.class.getName());
    	UIMgr.showActivity(this,0,ImageViewUI.class.getName());
    }
    public void onHeadClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.base_ui_close: {
                finish();
                break;
            }
            case R.id.base_ui_help: {
                Intent i = new Intent();
                i.putExtra(Const.LAYOUT_ID, R.layout.ui_webview_left_close);
                i.putExtra(Const.URL, getResources().getString(R.string.help_link));
                i.putExtra(Const.TITLE, getResources().getString(R.string.help));
                UIMgr.showActivity(BaseUI.this, i);
                break;
            }
            case R.id.base_ui_publish :{
                finish();
                break;
            }
            case R.id.base_head_bar_publish:{
                UIMgr.showActivity(this, R.layout.ui_comment_publish);
                break;
            }
            case R.id.ui_my_care_head_fnews:{
                boolean f = "true".equals(view.getTag());
                if(!f){
                    view.setBackgroundResource(R.drawable.title_btn_background_selected);
                    view.setTag("true");
                    {
                        View fPlace = findViewById(R.id.ui_my_care_head_fplace);
                        fPlace.setBackgroundResource(R.drawable.title_btn_background);
                        fPlace.setTag(false);
                    }
                    {
                        ViewGroup vg = (ViewGroup)findViewById(R.id.ui_my_care_list_root);
                        vg.getChildAt(0).setVisibility(View.VISIBLE);
                        vg.getChildAt(1).setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.ui_my_care_head_fplace:{
                boolean f = "true".equals(view.getTag());
                if(!f){
                    view.setBackgroundResource(R.drawable.title_btn_background_selected);
                    view.setTag("true");
                    {
                        View fNews = findViewById(R.id.ui_my_care_head_fnews);
                        fNews.setBackgroundResource(R.drawable.title_btn_background);
                        fNews.setTag(false);
                    }
                    {
                        ViewGroup vg = (ViewGroup)findViewById(R.id.ui_my_care_list_root);
                        vg.getChildAt(1).setVisibility(View.VISIBLE);
                        vg.getChildAt(0).setVisibility(View.GONE);
                    }
                }
                break;
            }
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.login_pswd_forget: {
                UIMgr.showActivity(BaseUI.this, R.layout.ui_forget_pswd);
                break;
            }
            case R.id.login_login_btn: {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(Const.LAYOUT_ID, R.layout.ui_main);
                UIMgr.showActivity(BaseUI.this, i, HomeUI.class.getName());
                finish();
                break;
            }
            case R.id.ui_forget_pswd_save_pswd:{
                Switcher switcher = (Switcher) findViewById(R.id.ui_forget_pswd_save_pswd);
                switcher.change();
                break;
            }
            case R.id.login_reg_new_account_btn: {
                Intent i = new Intent();
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(Const.LAYOUT_ID, R.layout.ui_reg);
                UIMgr.showActivity(this, i, RegisterUI.class.getName());
                break;
            }
            case R.id.ui_my_sec_next_skip_btn: {
                finish();
                //应该还要触发显示附近钓场
                break;
            }
        }

    }

    private void onCreateWebview() {
        WebView wb = (WebView) findViewById(R.id.base_ui_webview);
        WebSettings setting = wb.getSettings();
        wb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
            	// TODO Auto-generated method stub
            	super.onPageFinished(view, url);
            	ViewHelper.closeGlobalWaiting();
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            	// TODO Auto-generated method stub
            	super.onPageStarted(view, url, favicon);
            	ViewHelper.showGlobalWaiting(BaseUI.this, null);
            }
        });
        setting.setJavaScriptEnabled(true);
        String url = getIntent().getStringExtra(Const.URL);
        wb.loadUrl(url);
        String title = getIntent().getStringExtra(Const.TITLE);
        TextView titleView = (TextView) findViewById(R.id.base_ui_webview_title);
        titleView.setText(title);
    }

    @Override
    public void onTagClick(View view) {
//        view self seting background child[1] setVisible
        View child = ((ViewGroup)view).getChildAt(1);
        if(child.getVisibility() == View.GONE){
            view.setBackgroundResource(R.drawable.tag_selected_background);
            child.setVisibility(View.VISIBLE);
        }else{
            view.setBackgroundResource(R.drawable.tag_background);
            child.setVisibility(View.GONE);
        }
    }
}
