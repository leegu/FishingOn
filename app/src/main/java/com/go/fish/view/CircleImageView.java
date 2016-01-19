package com.go.fish.view;

import com.go.fish.util.LogUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

//	public CircleImageView(Context context) {
//		super(context);
//	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

//	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}

	Path mPath = new Path();
	Paint paint = new Paint();
	int circleX,circleY;
	int radius;
	
	@Override
	public void draw(Canvas canvas) {
		if(getDrawable() != null){
			if(Build.VERSION.SDK_INT <= 16){
				
			}else{
				canvas.save();
				canvas.clipPath(mPath);
			}
	//		canvas.clipPath(mPath,Op.REPLACE);
//			canvas.drawColor(0xff00CCCC);
//			canvas.drawColor(0xffFFFFFF);
			super.draw(canvas);
//			if(Build.VERSION.SDK_INT <= 16){
////				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
////				canvas.clipPath(mPath,Op.REPLACE);
//				canvas.drawColor(0xffFFFFFF);
//			}
			if(Build.VERSION.SDK_INT <= 16){
				canvas.save();
				canvas.clipPath(mPath);
			}else{
				
			}
				
			canvas.restore();
		}
	}
//	@Override
//		protected void dispatchDraw(Canvas canvas) {
//			super.dispatchDraw(canvas);
//		}
//	@Override
//		protected void onDraw(Canvas canvas) {
//			super.onDraw(canvas);
//			if(Build.VERSION.SDK_INT <= 16){
//				canvas.drawColor(0xffFF00FF);
//			}
//		}
	@Override
	public void layout(int l, int t, int r, int b) {
		super.layout(l, t, r, b);
		radius = getWidth() / 2;
		circleX = radius;
		circleY = radius;
		mPath.addCircle(circleX, circleY, radius, Path.Direction.CCW);
//		mPath.addRect(l, t, r, b, Direction.CCW);
	}
}
