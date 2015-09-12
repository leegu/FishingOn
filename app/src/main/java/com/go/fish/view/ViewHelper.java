package com.go.fish.view;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.go.fish.R;
import com.go.fish.util.Const;
import com.go.fish.util.ImageLoader;
import com.go.fish.util.LocalMgr;
import com.go.fish.view.BaseFragment.ResultForActivityCallback;

public class ViewHelper {

	
	public static void updateChildrenBackground(ViewGroup views,int position,int newResId,int oldResId){
		for(int i = 0; i < views.getChildCount(); i++){
			if(position == i){
				views.getChildAt(i).setBackgroundResource(newResId);
			}else{
				views.getChildAt(i).setBackgroundResource(oldResId);
			}
		}
	}
	public static void updateChildrenBackground(ViewGroup views,final int depth,int operateChildIndex,int position,int newRes,int oldRes){
		for(int i = 0; i < views.getChildCount(); i++){
			View v = views.getChildAt(i);
			int t_depath = depth;
			while(t_depath > 0){
				v = ((ViewGroup)v).getChildAt(operateChildIndex);
				t_depath --;
			}
			try {
				if(position == i){
					v.setVisibility(View.VISIBLE);
					v.setBackgroundResource(newRes);
				}else{
					v.setVisibility(View.INVISIBLE);
					v.setBackgroundResource(oldRes);
				}
			} catch (Resources.NotFoundException e) {
				if(position == i){
					v.setBackgroundColor(newRes);
				}else{
					v.setBackgroundColor(oldRes);
				}
			}
		}
	}
	
	public static void updateViewArrayBackground(ArrayList<View> views,int position,int newResId,int oldResId){
		for(int i = 0; i < views.size(); i++){
			if(position == i){
				views.get(i).setBackgroundResource(newResId);
			}else{
				views.get(i).setBackgroundResource(oldResId);
			}
		}
	}
	
	private static ViewGroup newCustomeTabGroup(Context context,String[] itemLabels){
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.tab_custome, null);
		LinearLayout tabContent = (LinearLayout) tab.findViewById(R.id.search_tab_content);
		Resources res = context.getResources();
		int sw = res.getDisplayMetrics().widthPixels;
		int leftOrRightWidth = res.getDimensionPixelSize(R.dimen.tab_arrow_left) + res.getDimensionPixelSize(R.dimen.tab_arrow_right);
		int tabItemWidth = (sw - leftOrRightWidth) / 4;
		for (String t : itemLabels) {
			ViewGroup tabitemLinear = (ViewGroup) inflater.inflate(
				R.layout.tabitem_fplace_type, null);
			{
				TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
				tabitem.setText(t);
			}
			{
				TextView status = (TextView) tabitemLinear.getChildAt(1);
				status.setBackgroundColor(Color.GRAY);
			}
			tabContent.addView(tabitemLinear, new LayoutParams(
				tabItemWidth, LayoutParams.MATCH_PARENT));
		}
		tab.setLayoutParams(new LayoutParams(-1,res.getDimensionPixelSize(R.dimen.tab_height)));
		return tab;
	}
	
	public static ViewGroup newMainView(Context context, FragmentManager fm, ResultForActivityCallback callback, String[] itemLabels){
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.view_main_content, null);
		final LinearLayout tabContent = (LinearLayout) vg.findViewById(R.id.search_tab_content);
		Resources res = context.getResources();
		int sw = res.getDisplayMetrics().widthPixels;
		int leftOrRightWidth = res.getDimensionPixelSize(R.dimen.tab_arrow_left) + res.getDimensionPixelSize(R.dimen.tab_arrow_right);
		int tabItemWidth = (sw - leftOrRightWidth) / 4;
		final ViewPager viewPager = (ViewPager) vg.findViewById(R.id.search_viewpager);
		final ArrayList<Fragment> fragmentArr = new ArrayList<Fragment>();
		View.OnClickListener tabItemListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = Integer.parseInt(String.valueOf(v.getTag()));
//				updateChildrenBackground(tabContent, 1, 1, position, Color.GRAY, Color.WHITE);//触发viewpager的选择事件
				viewPager.setCurrentItem(position);
			}
		};
		for (int i = 0; i < itemLabels.length; i++) {
			String title = itemLabels[i];
			{
				ViewGroup tabitemLinear = (ViewGroup) inflater.inflate(
						R.layout.tabitem_fplace_type, null);
				{//设置tabitem显示名称
					TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
					tabitem.setText(title);
					tabitem.setTag(i);
					tabitem.setOnClickListener(tabItemListener);
					tabitem.setClickable(true);
				}
				if(i == 0){//设置焦点状态
					TextView status = (TextView) tabitemLinear.getChildAt(1);
					status.setBackgroundColor(Color.GRAY);
				}
				tabContent.addView(tabitemLinear, new LayoutParams(
						tabItemWidth, LayoutParams.MATCH_PARENT));
			}
			{// listFragment初始化
				FPlaceListFragment listFragment = FPlaceListFragment.newInstance(callback,R.layout.list_fragment);
				listFragment.name = title;
//				fm.beginTransaction().add(listFragment, null);
				fragmentArr.add(listFragment);
			}
		}
		BaseFragmentPagerAdapter bfpa = new BaseFragmentPagerAdapter(fm, fragmentArr);
		viewPager.setAdapter(bfpa);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				updateChildrenBackground(tabContent, 1, 1, position, Color.GRAY, Color.WHITE);
				((FPlaceListFragment) fragmentArr.get(position)).updateAdapter();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				System.out.println("onPageScrolled =" + arg0 + ";" + arg1 + ";" + arg2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
//				System.out.println("onPageScrollStateChanged =" + arg0);
			}
		});

		return vg;
	}
	
//	public static void showMarkerInfo(ViewGroup parentView,Bundle data,int x,int y){
//		View floatView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.listitem_float_view_in_map, null);
//		FrameLayout f = new FrameLayout(parentView.getContext());
//		f.addView(floatView);
//		TextView detail_text = (TextView) floatView.findViewById(R.id.float_view_title);
//		int orderId = data.getInt(Const.ORDER_ID);
//		detail_text.setText(orderId + "." + data.getString(Const.TEXT));
//		parentView.addView(f);
//		f.setPadding(parentView.getPaddingLeft(), y, parentView.getRight(), parentView.getPaddingBottom());
//	}
	public static void showMarkerInfo(Context context,View view,Bundle data,int x,int y){
		View floatView = LayoutInflater.from(context).inflate(R.layout.listitem_float_view_in_map, null);
		TextView detail_text = (TextView) floatView.findViewById(R.id.float_view_title);
		int orderId = data.getInt(Const.STA_ORDER_ID);
		detail_text.setText(orderId + "." + data.getString(Const.STA_TEXT));
		PopupWindow popupWindow = new PopupWindow();
//		int w = context.getResources().getDisplayMetrics().widthPixels;
		popupWindow.setWidth(-1);
		popupWindow.setHeight(-2);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		FrameLayout f = new FrameLayout(context);
		f.addView(floatView);
		f.setPadding(50, 0, 50, 0);
		popupWindow.setContentView(f);
		popupWindow.showAtLocation(view, 0, x, y + floatView.getHeight());//(anchor, -width, -anchor.getHeight() * 3 / 4);
	}

	public static PopupWindow showPopupWindow(Context context,PopupWindow popupWindow,View anchor,View contentView){
		int width = context.getResources().getDimensionPixelSize(R.dimen.pop_win_list_width);
		if(popupWindow == null){
			popupWindow = new PopupWindow();
			popupWindow.setWidth(width);
			popupWindow.setHeight(-2);
			// 使其聚集
			popupWindow.setFocusable(true);
			// 设置允许在外点击消失
			popupWindow.setOutsideTouchable(true);
			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		popupWindow.setContentView(contentView);
		popupWindow.showAsDropDown(anchor, -width, -anchor.getHeight() * 3 / 4);
		return popupWindow;
	}


	public static ProgressDialog sPopupWindow = null;
	public static void showGlobalWaiting(Context context,ProgressDialog.OnDismissListener listener){
		if(sPopupWindow != null){
			sPopupWindow.dismiss();
		}
//		sPopupWindow = new ProgressDialog(context);
//		LayoutInflater li = LayoutInflater.from(context);
//		sPopupWindow.addContentView(li.inflate(R.layout.ui_waiting, null),new LayoutParams(-1, -1));
//		sPopupWindow.setOnDismissListener(listener);
		sPopupWindow = ProgressDialog.show(context,null,"数据加载中...");
		sPopupWindow.setCanceledOnTouchOutside(false);
		sPopupWindow.setCancelable(true);
	}

	public static void closeGlobalWaiting(){
		if(sPopupWindow != null){
			sPopupWindow.dismiss();
		}
	}
	
	public static void load(ImageView view,String url){
		Bitmap bm = ImageLoader.self().getBitmapFromLruCache(url);
		if(bm == null){//从缓存取
			bm = LocalMgr.self().getBitmap(url);
			if(bm == null){//从本地取
				ImageLoader.self().loadNetImage(new ImageLoader.DownloadTask<ImageView>(url, view){
					@Override
					public void onEnd(String downUrl, Bitmap bitmap) {
						if(bitmap != null){
							if(taskReceiver instanceof ImageView){
	//							((ImageView)taskReceiver).setImageBitmap(bitmap);
								((ImageView)taskReceiver).setBackgroundDrawable(new BitmapDrawable(bitmap));
								LocalMgr.self().save(downUrl, bitmap);
							}
							ImageLoader.self().addBitmapToLruCache(downUrl, bitmap);
						}
					}
				});
			}else{
				view.setBackgroundDrawable(new BitmapDrawable(bm));
			}
		}else{
			view.setBackgroundDrawable(new BitmapDrawable(bm));
		}
	}

}
