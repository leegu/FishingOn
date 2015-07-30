package com.go.fish.ui;

import java.util.HashMap;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.view.MainView;


public class HomeUI extends FragmentActivity {
    private static final String TAG = "HomeUI";
    private ViewPager mPager;
    private HashMap<String,Fragment> fragmentsList = new HashMap<String, Fragment>();
    private ImageView ivBottomLine;
    private TextView tvTabActivity, tvTabGroups, tvTabFriends, tvTabChat;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int position_two;
    private int position_three;
    private Resources resources;

    FragmentManager fragmentMgr = null;
    FragmentTransaction fragmentTransaction = null;
    private static  String F_TAG_FP = "fp";
    private static  String F_TAG_CARE = "care";
    private static  String F_TAG_APPEAR = "appear";
    private static  String F_TAG_FN = "fn";
    private static  String F_TAG_MY = "my";
//    private Fragment fishingPlaceFragment;
//    private Fragment careFragment;
//    private Fragment appearFragment;
//    private Fragment fishingNewsFragment;
//    private Fragment muFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout_id = getIntent().getIntExtra(BaseUI.LAYOUT_ID, 0);
        setContentView(layout_id);
        fragmentMgr = getSupportFragmentManager();
        fragmentTransaction = fragmentMgr.beginTransaction();
//        {
//	        Fragment fragment = getFragment(F_TAG_FP);
//	        fragmentTransaction.add(R.id.foot_bar_fishing_place, fragment);
//        }
//        {
//	        Fragment fragment = getFragment(F_TAG_CARE);
//	        fragmentTransaction.add(R.id.foot_bar_care, fragment);
//	        fragmentTransaction.hide(fragment);
//        }
//        {
//        	Fragment fragment = getFragment(F_TAG_APPEAR);
//        	fragmentTransaction.add(R.id.foot_bar_appear, fragment);
//        	fragmentTransaction.hide(fragment);
//        }
//        {
//        	Fragment fragment = getFragment(F_TAG_FN);
//        	fragmentTransaction.add(R.id.foot_bar_fishing_news, fragment);
//        	fragmentTransaction.hide(fragment);
//        }
//        {
//        	Fragment fragment = getFragment(F_TAG_MY);
//        	fragmentTransaction.add(R.id.foot_bar_my, fragment);
//        	fragmentTransaction.hide(fragment);
//        }
//        fragmentTransaction.commit();
//        careFragment = initFragment(F_TAG_CARE);

        
//        ft.show(fishingPlaceFragment);
//        resources = getResources();
//        InitWidth();
//        InitTextView();
//        InitViewPager();
    }

    private Fragment getFragment(String tag){
        Fragment fragment = fragmentsList.get(tag);
        if(fragment == null) {
        	fragment = new MainView();
            Bundle bundle = new Bundle();
            int layoutId = 0;
            if(tag.equals(F_TAG_FP)){
                layoutId = R.layout.fishing_place;
            }else if(tag.equals(F_TAG_CARE)){
                layoutId = R.layout.care;
            }else if(tag.equals(F_TAG_APPEAR)){
                layoutId = R.layout.appear;
            }else if(tag.equals(F_TAG_FN)){
                layoutId = R.layout.fishing_news;
            }else if(tag.equals(F_TAG_MY)){
                layoutId = R.layout.my;
            }
            bundle.putInt(BaseUI.LAYOUT_ID, layoutId);
            fragment.setArguments(bundle);
            fragmentsList.put(tag, fragment);
//            fragmentTransaction.add(fragment, tag);
        }
        
        return fragment;
    }
    int[] ids = new int[]{R.id.home_fishing_place,R.id.home_care,R.id.home_appear,R.id.home_fishing_news,R.id.home_my};
    public void showFragment(int fid){
    	 FragmentTransaction ft = fragmentMgr.beginTransaction();
         ft.show(fragmentMgr.findFragmentById(fid));
         for(int id : ids){
        	 if(id != fid){
        		 ft.hide(fragmentMgr.findFragmentById(id));
        	 }
         }
         ft.commit();
    }
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.foot_bar_fishing_place:
            	showFragment(R.id.home_fishing_place);
                break;
            case R.id.foot_bar_care:
            	showFragment(R.id.home_care);
                break;
            case R.id.foot_bar_appear:
            	showFragment(R.id.home_appear);
                break;
            case R.id.foot_bar_fishing_news:
            	showFragment(R.id.home_fishing_news);
                break;
            case R.id.foot_bar_my:
            	showFragment(R.id.home_my);
                break;
        }
    }
//    private void InitTextView() {
//        tvTabActivity = (TextView) findViewById(R.id.tv_tab_activity);
//        tvTabGroups = (TextView) findViewById(R.id.tv_tab_groups);
//        tvTabFriends = (TextView) findViewById(R.id.tv_tab_friends);
//        tvTabChat = (TextView) findViewById(R.id.tv_tab_chat);
//
//        tvTabActivity.setOnClickListener(new MyOnClickListener(0));
//        tvTabGroups.setOnClickListener(new MyOnClickListener(1));
//        tvTabFriends.setOnClickListener(new MyOnClickListener(2));
//        tvTabChat.setOnClickListener(new MyOnClickListener(3));
//    }

//    private void InitViewPager() {
//        mPager = (ViewPager) findViewById(R.id.main_viewpager);
//        fragmentsList = new ArrayList<Fragment>();
//
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("userid", "С��");
//        paramMap.put("age", 23);
//
//
//        Map<String, Object> paramMap2 = new HashMap<String, Object>();
//        paramMap2.put("userid", "vatty");
//        paramMap2.put("age", 24);
//
//        Map<String, Object> paramMap3 = new HashMap<String, Object>();
//        paramMap3.put("userid", "С��");
//        paramMap3.put("age", 25);
//
//
//        Map<String, Object> paramMap4 = new HashMap<String, Object>();
//        paramMap4.put("userid", "hongshengpeng.com");
//        paramMap4.put("age", 26);
//
//
//        Fragment activityfragment = MainView.newInstance("Hello Activity.", paramMap);
//        Fragment groupFragment = MainView.newInstance("Hello Group.", paramMap2);
//        Fragment friendsFragment = MainView.newInstance("Hello Friends.", paramMap3);
//        Fragment chatFragment = MainView.newInstance("Hello Chat.", paramMap4);
//
//        fragmentsList.add(activityfragment);
//        fragmentsList.add(groupFragment);
//        fragmentsList.add(friendsFragment);
//        fragmentsList.add(chatFragment);
//
//        mPager.setAdapter(new MainViewAdapter(getSupportFragmentManager(), fragmentsList));
//        mPager.setCurrentItem(0);
//        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
//    }
//
//    private void InitWidth() {
//        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
//        bottomLineWidth = ivBottomLine.getLayoutParams().width;
//        Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenW = dm.widthPixels;
//        offset = (int) ((screenW / 4.0 - bottomLineWidth) / 2);
//        Log.i("MainActivity", "offset=" + offset);
//
//        position_one = (int) (screenW / 4.0);
//        position_two = position_one * 2;
//        position_three = position_one * 3;
//    }
//
//    public class MyOnClickListener implements View.OnClickListener {
//        private int index = 0;
//
//        public MyOnClickListener(int i) {
//            index = i;
//        }
//
//        @Override
//        public void onClick(View v) {
//            mPager.setCurrentItem(index);
//        }
//    }
//
//    ;
//
//    public class MyOnPageChangeListener implements OnPageChangeListener {
//
//        @Override
//        public void onPageSelected(int arg0) {
//            Animation animation = null;
//            switch (arg0) {
//                case 0:
//                    if (currIndex == 1) {
//                        animation = new TranslateAnimation(position_one, 0, 0, 0);
//                        tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 2) {
//                        animation = new TranslateAnimation(position_two, 0, 0, 0);
//                        tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 3) {
//                        animation = new TranslateAnimation(position_three, 0, 0, 0);
//                        tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
//                    }
//                    tvTabActivity.setTextColor(resources.getColor(R.color.white));
//                    break;
//                case 1:
//                    if (currIndex == 0) {
//                        animation = new TranslateAnimation(0, position_one, 0, 0);
//                        tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 2) {
//                        animation = new TranslateAnimation(position_two, position_one, 0, 0);
//                        tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 3) {
//                        animation = new TranslateAnimation(position_three, position_one, 0, 0);
//                        tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
//                    }
//                    tvTabGroups.setTextColor(resources.getColor(R.color.white));
//                    break;
//                case 2:
//                    if (currIndex == 0) {
//                        animation = new TranslateAnimation(0, position_two, 0, 0);
//                        tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 1) {
//                        animation = new TranslateAnimation(position_one, position_two, 0, 0);
//                        tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 3) {
//                        animation = new TranslateAnimation(position_three, position_two, 0, 0);
//                        tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
//                    }
//                    tvTabFriends.setTextColor(resources.getColor(R.color.white));
//                    break;
//                case 3:
//                    if (currIndex == 0) {
//                        animation = new TranslateAnimation(0, position_three, 0, 0);
//                        tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 1) {
//                        animation = new TranslateAnimation(position_one, position_three, 0, 0);
//                        tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
//                    } else if (currIndex == 2) {
//                        animation = new TranslateAnimation(position_two, position_three, 0, 0);
//                        tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
//                    }
//                    tvTabChat.setTextColor(resources.getColor(R.color.white));
//                    break;
//            }
//            currIndex = arg0;
//            animation.setFillAfter(true);
//            animation.setDuration(300);
//            ivBottomLine.startAnimation(animation);
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
    }