package com.go.fish.view;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.go.fish.data.DataMgr;
import com.go.fish.data.FPlaceData;
import com.go.fish.util.Const;
import com.go.fish.util.NetTool;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public BaseFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
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
     */
    public static void initAdapterByNetData(final ViewPager viewPager,final int listitemLayoutid){
        NetTool.RequestData rData = NetTool.RequestData.newInstance(
                new NetTool.RequestListener() {
                    @Override
                    public void onStart() {
                      ViewHelper.showGlobalWaiting(viewPager.getContext(),null);
                    }
                    @Override
                    public void onEnd(byte[] data) {
                        if (data != null) {
                        	try {
								JSONArray jsonArray = new JSONArray(new String(data, "utf-8"));
								int len = Math.min(viewPager.getAdapter().getCount(), jsonArray.length());
								for (int i = 0; i < len; i++) {
								    try {
								        JSONArray arr = jsonArray.getJSONArray(i);
								        BaseFragmentPagerAdapter adapter = (BaseFragmentPagerAdapter) viewPager.getAdapter();
								        final FPlaceListFragment listFragment = (FPlaceListFragment) adapter.getItem(i);
								        if (viewPager.getCurrentItem() == i) {
								            ArrayList<FPlaceData> fPlaceArr = DataMgr.makeFPlaceDatas(listitemLayoutid, arr);
								            listFragment.updateData(fPlaceArr);
								        } else {
								            Bundle bundle = listFragment.getArguments();
								            bundle.putInt(Const.PRI_EXTRA_LAYOUT_ID,listitemLayoutid);
								            bundle.putString(Const.PRI_EXTRA_DATA, arr.toString());
								        }
								    } catch (JSONException e) {
								        e.printStackTrace();
								    }
								}
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                            ViewHelper.closeGlobalWaiting();
                        }
                    }
                }, "fishing_place_near"
        );
        NetTool.data().http(rData.syncCallback());
    }
}
