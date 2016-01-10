package com.go.fish.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.go.fish.data.DataMgr;
import com.go.fish.data.FieldData;
import com.go.fish.data.load.FieldDataLoader;
import com.go.fish.user.User;
import com.go.fish.util.Const;
import com.go.fish.util.LocalMgr;
import com.go.fish.util.LogUtils;
import com.go.fish.util.NetTool;
import com.go.fish.util.UrlUtils;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
	public ArrayList<IViewPagerChanged> mAllChangeListener;
    private ArrayList<Fragment> fragmentsList;
    Context mContext;
    public BaseFragmentPagerAdapter(Context context,FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mContext = context;
        this.fragmentsList = fragments;
        mAllChangeListener = new ArrayList<IViewPagerChanged>();
    }
    
    public void addOnPageChangeListener(IViewPagerChanged listener){
    	mAllChangeListener.add(listener);
    }
    public void setOnPageChangeListener(ViewPager viewPager){
    	viewPager.setAdapter(this);
    	viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				for(IViewPagerChanged listener : mAllChangeListener){
					listener.onPageSelected(position);
				}
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
    	});
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }
 
    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }


    /**
     * 联网获取数据，更新钓场数据
     * @param viewPager
     * @param listitemLayoutid
     * @param searchTitle TODO
     * @param defaultIndex TODO
     */
    public static void initAdapterByNetData(final ViewPager viewPager,final int listitemLayoutid,final String searchTitle,final int defaultIndex){
    	BaseFragmentPagerAdapter adapter = (BaseFragmentPagerAdapter) viewPager.getAdapter();
    	adapter.addOnPageChangeListener(new IViewPagerChanged() {
			@Override
			public void onPageSelected(int index) {
				FieldDataLoader.loadNetData(viewPager, listitemLayoutid, searchTitle, index);
			}
		});
    	FieldDataLoader.loadNetData(viewPager, listitemLayoutid, searchTitle, defaultIndex);//刚进来没必要去加载
    }

    
}
