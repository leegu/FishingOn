package com.go.fish.view;

import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DCloud on 2015/8/30.
 */
public class IMETools {
    /**
     * 隐藏输入法软键盘
     * @param view
     */
    public static void hideIME(View view){
        android.view.inputmethod.InputMethodManager methodmanager = (android.view.inputmethod.InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            IBinder ib = view.getWindowToken();
            if (ib != null) {
                methodmanager.hideSoftInputFromWindow(ib,
                        android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 显示软键盘
     * @param view
     */
    public static void showIME(final View view){
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){  //
            public void run() {
                android.view.inputmethod.InputMethodManager methodmanager = (android.view.inputmethod.InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (view != null) {
//		        				CompletionInfo[] info = new CompletionInfo[435];
//		        				info[0] = new CompletionInfo(500, 0, "ouiuioqweruioqwe");
//		        				methodmanager.displayCompletions(view, info);
                    methodmanager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }}, 250); //慢一些执行显示
    }
}
