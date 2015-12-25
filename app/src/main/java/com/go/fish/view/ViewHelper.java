package com.go.fish.view;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
	
//	private static ViewGroup newCustomeTabGroup(Context context,String[] itemLabels){
//		LayoutInflater inflater = LayoutInflater.from(context);
//		ViewGroup tab = (ViewGroup)inflater.inflate(R.layout.tab_custome, null);
//		LinearLayout tabContent = (LinearLayout) tab.findViewById(R.id.search_tab_content);
//		Resources res = context.getResources();
//		int sw = res.getDisplayMetrics().widthPixels;
//		int leftOrRightWidth = res.getDimensionPixelSize(R.dimen.tab_arrow_left) + res.getDimensionPixelSize(R.dimen.tab_arrow_right);
//		int tabItemWidth = (sw - leftOrRightWidth) / 4;
//		for (String t : itemLabels) {
//			ViewGroup tabitemLinear = (ViewGroup) inflater.inflate(
//				R.layout.tabitem_fplace_type, null);
//			{
//				TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
//				tabitem.setText(t);
//			}
//			{
//				TextView status = (TextView) tabitemLinear.getChildAt(1);
//				status.setBackgroundColor(Color.GRAY);
//			}
//			tabContent.addView(tabitemLinear, new LayoutParams(
//				tabItemWidth, LayoutParams.MATCH_PARENT));
//		}
//		tab.setLayoutParams(new LayoutParams(-1,res.getDimensionPixelSize(R.dimen.tab_height)));
//		return tab;
//	}
	
	/**
	 * 
	 * @param context
	 * @param fm
	 * @param callback item回调接口
	 * @param itemLabels tabitem 内容
	 * @return
	 */
	public static ViewGroup newMainView(final Context context, FragmentManager fm, ResultForActivityCallback callback, String[] itemLabels){
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.view_main_content, null);//构造主要 窗口
		final LinearLayout tabContent = (LinearLayout) vg.findViewById(R.id.search_tab_content);
		((HorizontalScrollView)tabContent.getParent()).setHorizontalFadingEdgeEnabled(false);
		Resources res = context.getResources();
		int sw = res.getDisplayMetrics().widthPixels;
		int leftOrRightWidth = res.getDimensionPixelSize(R.dimen.base_space) * 8;
		int tabItemWidth = (sw - leftOrRightWidth) / 4 - 6;
		final ViewPager viewPager = (ViewPager) vg.findViewById(R.id.search_viewpager);
		final ArrayList<Fragment> fragmentArr = new ArrayList<Fragment>();
		//初始化标题按钮item listener监听
		View.OnClickListener tabItemListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = Integer.parseInt(String.valueOf(v.getTag()));
				viewPager.setTag("true");
				viewPager.setCurrentItem(position);
			}
		};
		for (int i = 0; i < itemLabels.length; i++) {//标题
			String title = itemLabels[i];
			{
				ViewGroup tabitemLinear = (ViewGroup) inflater.inflate( R.layout.tabitem_fplace_type, null);
				{//设置tabitem显示名称
					TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
					tabitem.setText(title);
					tabitem.setTag(i);
					tabitem.setOnClickListener(tabItemListener);
					tabitem.setClickable(true);
					if(i == 0) {//设置焦点状态
						tabitem.setTextColor(context.getResources().getColor(R.color.tabitem_foucs_color));
					}
				}
				if(i == 0){//设置焦点状态
					tabContent.setTag(tabitemLinear);
					TextView status = (TextView) tabitemLinear.getChildAt(1);
					status.setVisibility(View.VISIBLE);
				}
				tabContent.addView(tabitemLinear, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			}
			{// 钓场 list Fragment初始化
				FPlaceListFragment listFragment = FPlaceListFragment.newInstance(callback,R.layout.list_fragment);
				listFragment.name = title;
//				fm.beginTransaction().add(listFragment, null);
				fragmentArr.add(listFragment);
			}
		}
		//初始化ViewPager Adapter
		BaseFragmentPagerAdapter bfpa = new BaseFragmentPagerAdapter(context,fm, fragmentArr);
		bfpa.setOnPageChangeListener(viewPager);
		//设置viewPager 切换事件
		bfpa.addOnPageChangeListener(new IViewPagerChanged() {
			int lastPosition = 0;
			@Override
			public void onPageSelected(int position) {
				if (tabContent.getChildAt(position) != tabContent.getTag()) {
					{//修改老焦点
						ViewGroup tabitemLinear = (ViewGroup) tabContent.getTag();
						TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
						tabitem.setTextColor(context.getResources().getColor(R.color.text_btn_color));
						TextView tabitemStatus = (TextView) tabitemLinear.getChildAt(1);
						tabitemStatus.setVisibility(View.GONE);
					}
					{//设置新焦点
						ViewGroup tabitemLinear = (ViewGroup) tabContent.getChildAt(position);
						TextView tabitem = (TextView) tabitemLinear.getChildAt(0);
						tabitem.setTextColor(context.getResources().getColor(R.color.tabitem_foucs_color));
						TextView tabitemStatus = (TextView) tabitemLinear.getChildAt(1);
						tabitemStatus.setVisibility(View.VISIBLE);
						tabContent.setTag(tabitemLinear);
						if(!"true".equals(viewPager.getTag())) {
							ViewGroup hLiner = tabContent;
							HorizontalScrollView hsv = ((HorizontalScrollView)tabContent.getParent());
							int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
							int displayWidth = screenWidth - tabContent.getPaddingLeft() - tabContent.getPaddingRight();
							int scrollX = hsv.getScrollX();
							displayWidth -= hLiner.getPaddingLeft() - hLiner.getPaddingRight();
							int focusIndex = lastPosition;
							int fiWidth = 0;
							if(position < lastPosition){
								focusIndex--;
//								-1
//								for(int i = 0; i <= focusIndex; i++){
//									fiWidth += hLiner.getChildAt(i).getWidth();
//								}
//								scrollX = fiWidth - displayWidth;
//								if(scrollX < 0){
//									scrollX = 0;
//								}
							}else if(position > lastPosition){
//								+1
								focusIndex++;
							}
							for(int i = 0; i <= focusIndex; i++){
								fiWidth += hLiner.getChildAt(i).getWidth();
							}
							scrollX = fiWidth - displayWidth;
							if(focusIndex != hLiner.getChildCount() - 1){//露出尾方便认识后边还有值
								scrollX += hLiner.getChildAt(focusIndex + 1).getWidth() / 3;
							}
							if(scrollX < 0){
								scrollX = 0;
							}
							Log.d("scrollX", "scrollX=" + scrollX);
							hsv.smoothScrollTo(scrollX,0);
						}else{
							viewPager.setTag(null);
							lastPosition = position;
						}
					}
					((FPlaceListFragment) fragmentArr.get(position)).updateAdapter();
					lastPosition = position;
				}
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

	public static PopupWindow showPopupWindow(Context context, PopupWindow popupWindow, View anchor, View contentView){
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
		popupWindow.showAsDropDown(anchor, -(width  + context.getResources().getDimensionPixelSize(R.dimen.base_space_2) ), -anchor.getHeight()/* * 3 / 4*/);
		return popupWindow;
	}

	public static void showConfrim(Context context,int layoutId,final OnClickListener listener){
		showConfrim(context, layoutId, listener, null, null, null);
	}
	public static void showConfrim(Context context,int layoutId,final OnClickListener listener,String title,String leftBtn,String rightBtn){
		final CustomeDialog dialog = new CustomeDialog(context, layoutId);
		View view = dialog.getRootView();
		OnClickListener l = new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				listener.onClick(v);
			}
		};
		TextView titleView = ((TextView)view.findViewById(R.id.confirm_dialog_title));
		if(!TextUtils.isEmpty(title)){
			titleView.setText(title);
		}
		TextView leftBtnView = ((TextView)view.findViewById(R.id.confirm_dialog_btn_left));
		if(!TextUtils.isEmpty(leftBtn)){
			leftBtnView.setText(leftBtn);
		}
		leftBtnView.setOnClickListener(l);
		TextView rightBtnView = ((TextView)view.findViewById(R.id.confirm_dialog_btn_right));
		if(!TextUtils.isEmpty(rightBtn)){
			rightBtnView.setText(rightBtn);
		}
		rightBtnView.setOnClickListener(l);
		dialog.show();
	}
	/**
	 *
	 * 创建DefaultDialog,并设置slide-in-top\slide-out-top动画
	 * @param context
	 * @param alpha
	 * @return
	 */
	public static Dialog createDefaultDialog(Context context, View contentView, int w, int h, float alpha){
		final Dialog d = new Dialog(context,R.style.dialog_window_in_out);
		d.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		d.setContentView(contentView);
		d.setCanceledOnTouchOutside(false);
//        //设置触摸对话框意外的地方取消对话框
//        setCanceledOnTouchOutside(true);

		Window window = d.getWindow(); //得到对话框
//        window.setBackgroundDrawable(CanvasHelper.getDrawable(0xff000000));
		window.setWindowAnimations(R.style.dialog_window_in_out); //设置窗口弹出动画
//        window.setBackgroundDrawableResource(R.color.dcloud_defalut_dialog_bg_color); //设置对话框背景为透明
		WindowManager.LayoutParams wl = window.getAttributes();
//        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
//        wl.verticalMargin = 0;
		//根据x，y坐标设置窗口需要显示的位置
		wl.x = 0; //x小于0左移，大于0右移
		wl.y = 0; //y小于0上移，大于0下移
		wl.width = (w == -1 ? context.getResources().getDisplayMetrics().widthPixels : w);
//		wl.height = -2;
        wl.height = h == -1 ? (context.getResources().getDisplayMetrics().heightPixels * 2 / 3) : h;
        wl.alpha = alpha; //设置透明度
		wl.gravity = Gravity.BOTTOM; //设置重力
		window.setAttributes(wl);
		return d;
	}

	public static void showToast(Context context,String text){
		Toast.makeText(context,text,Toast.LENGTH_LONG).show();
	}
	public static ProgressDialog sPopupWindow = null;
	public static void showGlobalWaiting(Context context,ProgressDialog.OnDismissListener listener){
		showGlobalWaiting(context, listener,"数据加载中...",-1);
	}
	public static void showGlobalWaiting(Context context,ProgressDialog.OnDismissListener listener,String text){
		showGlobalWaiting(context, listener, text, -1);
	}
	public static void showGlobalWaiting(Context context,ProgressDialog.OnDismissListener listener,String text,int timeoutTime){
		if(sPopupWindow != null){
			sPopupWindow.dismiss();
		}
		Log.d("viewHelper","showGlobalWaiting " + text);
//		sPopupWindow = new ProgressDialog(context);
//		LayoutInflater li = LayoutInflater.from(context);
//		sPopupWindow.addContentView(li.inflate(R.layout.ui_waiting, null),new LayoutParams(-1, -1));
//		sPopupWindow.setOnDismissListener(listener);
		try {
			sPopupWindow = ProgressDialog.show(context,null,text);
			sPopupWindow.setCanceledOnTouchOutside(false);
			sPopupWindow.setCancelable(true);
			if(timeoutTime > 0){
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeGlobalWaiting();
                    }
                },timeoutTime);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeGlobalWaiting(){
		Log.d("viewHelper", "closeGlobalWaiting ");
		if(sPopupWindow != null && sPopupWindow.isShowing()){
			sPopupWindow.dismiss();
		}
	}
	
	public static void load(ImageView view,String url){
		load(view, url, true);
	}
	public static void load(ImageView view,String url,boolean allowNetLoad){
		load(view, url, allowNetLoad, true);
	}
	public static void load(ImageView view,String url,boolean allowNetLoad,final boolean forBg){
		Bitmap bm = ImageLoader.self().getBitmapFromLruCache(url);
		if(bm == null && allowNetLoad){//从缓存取
			if(bm == null){
				bm = LocalMgr.self().getBitmap(url);
			}
			if(bm == null && url.startsWith(LocalMgr.sRootPath)){
				bm = LocalMgr.self().getLowBitmap(url);
				if(bm != null){
					if(forBg){
						((ImageView)view).setBackgroundDrawable(new BitmapDrawable(bm));
					}else{
						((ImageView)view).setImageBitmap(bm);
					}
					ImageLoader.self().addBitmapToLruCache(url, bm);
				}
				return ;
			}
			if(bm == null){//从本地取
				ImageLoader.self().loadNetImage(new ImageLoader.DownloadTask<ImageView>(url, view){
					@Override
					public void onStart() {
//						taskReceiver.setBackgroundResource(R.drawable.pic);
					}
					@Override
					public void onEnd(String downUrl, Bitmap bitmap) {
						if(bitmap != null){
							if(taskReceiver instanceof ImageView){
								if(forBg){
									((ImageView)taskReceiver).setBackgroundDrawable(new BitmapDrawable(bitmap));
								}else{
									((ImageView)taskReceiver).setImageBitmap(bitmap);
								}
								LocalMgr.self().save(downUrl, bitmap);
							}
							ImageLoader.self().addBitmapToLruCache(downUrl, bitmap);
						}
					}
				});
			}else{
				if(forBg){
					view.setBackgroundDrawable(new BitmapDrawable(bm));
				}else{
					view.setImageBitmap(bm);
				}
			}
		}else{
			if(forBg){
				view.setBackgroundDrawable(new BitmapDrawable(bm));
			}else{
				view.setImageBitmap(bm);
			}
		}
	}

	public static void share(Activity activity,String text){
		Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.setType("text/plain");
		activity.startActivity(Intent.createChooser(sendIntent, Const.DEFT_SHARE_TO));
	}
	
}
