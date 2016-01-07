package com.go.fish.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.go.fish.R;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.IME;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;
import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;
import com.go.fish.view.BaseFragment;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;
import com.go.fish.view.BaseFragmentPagerAdapter;
import com.go.fish.view.FPlaceListAdapter;
import com.go.fish.view.ViewHelper;

public class SearchUI extends BaseUI implements ResultForActivityCallback,IHasHeadBar,IHasComment{
	FragmentManager fragmentMgr = null;
	BaseFragment searchFragment = null,detailFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentMgr = getSupportFragmentManager();
		setContentView(R.layout.search);
		int layout = getIntent().getIntExtra(Const.PRI_LAYOUT_ID, 0);
		if(layout != R.layout.ui_search_list){//
			initDetailFragment(layout);
			detailFragment.isFront = true;
			Bundle dataBundle = getIntent().getExtras();
			Bundle b = detailFragment.getArguments();
			b.putString(Const.PRI_JSON_DATA, dataBundle.getString(Const.PRI_JSON_DATA));
			detailFragment.setArguments(b);
			fragmentMgr.beginTransaction().add(R.id.search_content, detailFragment).commit();
		} else{//R.layout.ui_detail_field R.layout.ui_detail_podcast
			searchFragment = BaseFragment.newInstance(this, R.layout.ui_search_list);
			searchFragment.mFlag = getIntent().getIntExtra(Const.PRI_FPLACE_RESULT_TYPE, FPlaceListAdapter.FLAG_SEARCH_RESULT);
			searchFragment.isFront = true;
			fragmentMgr.beginTransaction().add(R.id.search_content, searchFragment).commit();
		} 
	}
	public  void onHeadClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.search_item_detail_head_back:
			case R.id.search_list_in_map_head_back:
				if(fragmentMgr.getFragments().size() == 1){
					finish();
				}else{
					fragmentMgr.popBackStack();
				}
				break;
			case R.id.search_head_back:
				finish();
				break;
			case R.id.search_item_detail_share:
				if(detailFragment != null) {
					Toast.makeText(this,"分享",Toast.LENGTH_LONG).show();
//					detailFragment.onHeadClick(view);
				}
				break;
			case R.id.search_head_btn://搜索按钮
				IME.hideIME(view);
				ViewPager viewPager = (ViewPager) searchFragment.getView().findViewById(R.id.search_viewpager);
				String searchTitle = ((TextView)findViewById(R.id.search_list_edit)).getText().toString();
				BaseFragmentPagerAdapter.initAdapterByNetData(viewPager,R.layout.listitem_fpalce, searchTitle, viewPager.getCurrentItem());
				break;
			default:
				super.onClick(view);
				break;
		}
	}
	private void initDetailFragment(int layoutId) {
		if(detailFragment == null){
			detailFragment = BaseFragment.newInstance(this,layoutId);
		}
		detailFragment.mFlag = getIntent().getIntExtra(Const.PRI_FPLACE_RESULT_TYPE, FPlaceListAdapter.FLAG_SEARCH_RESULT);
	}
	

	@Override
	public void onItemClick(View view, FPlaceData data) {
		initDetailFragment(R.layout.ui_detail_field);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Const.STA_FIELDID, data.sid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetTool.data().http(new RequestListener() {
			@Override
			public void onStart() {
				onStart(SearchUI.this);
			}

			@Override
			public void onEnd(byte[] data) {
				try {
					if (isOver()) {// 返回键取消联网之后不应该继续处理

					} else {
						JSONObject response = toJSONObject(data);
						if (isRight(SearchUI.this,response,true)) {
							String jsonStr = new String(data, "UTF-8");
							Bundle newBundle = new Bundle();
							newBundle.putString(Const.PRI_JSON_DATA, jsonStr);
							newBundle.putInt(Const.PRI_LAYOUT_ID, R.layout.ui_detail_field);//搜索列表进入到钓场详情页
//								Bundle bundle = searchFragment.getArguments();
//								bundle.putString(Const.PRI_JSON_DATA, jsonStr);
//								bundle.putInt(Const.PRI_EXTRA_LAYOUT_ID, R.layout.ui_f_search_item_detail);
							detailFragment.setArguments(newBundle);
							showFragment(detailFragment);
						} 
					}
					onEnd();
				} catch (Exception e) {
				}
			}
		}, jsonObject, UrlUtils.self().getFieldInfo());
		
	}
	
	private void showFragment(Fragment f){
		FragmentTransaction ft = fragmentMgr.beginTransaction();
		ft.add(R.id.search_content, f);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}
	public void onShare(View view) {
		ViewHelper.share(this, Const.DEFT_SHARE_CONTENT);
	}
	public void onMapFloatViewClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.float_view_care_text:
			break;
		case R.id.float_view_nav_btn:
			
			break;

		default:
			break;
		}
	}
}
