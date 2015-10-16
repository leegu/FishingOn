package com.go.fish.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.go.fish.R;
import com.go.fish.view.AutoLayoutViewGroup;
import com.go.fish.view.ViewHelper;

/**
 * Created by DCloud on 2015/10/11.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutoLayoutViewGroup autoLayout = new AutoLayoutViewGroup(this);
        autoLayout.setBackgroundColor(0x940565);
        ImageView t = new ImageView(this);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.showToast(TestActivity.this, "show a");
            }
        });
        t.setPadding(10, 10, 10, 10);
        t.setBackgroundResource(R.drawable.p);
        int w = (getResources().getDisplayMetrics().widthPixels - 30 * 4 ) / 3;
        autoLayout.addView(t, w,w);
        setContentView(autoLayout);
    }
}
