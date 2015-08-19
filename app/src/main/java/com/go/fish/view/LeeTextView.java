package com.go.fish.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.go.fish.R;

public class LeeTextView extends android.widget.TextView{


	int textColor = Color.BLACK;
	int textColor_pressed = Color.WHITE;
	int background = 0x00CCCCCC;
	int background_pressed = 0xFFCCCCCC;
	int backgroundGravity = 0;
	int paintTextColor = textColor;
	int paintBackground = background;
	String text = null;
	Paint paint = new Paint();
	
	public LeeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LeeTextView); 
		backgroundGravity = a.getInt(R.styleable.LeeTextView_background_gravity, backgroundGravity);
		textColor = a.getInt(R.styleable.LeeTextView_textColor, textColor);
		textColor_pressed = a.getInt(R.styleable.LeeTextView_textColor_pressed, textColor_pressed);
		background = a.getInt(R.styleable.LeeTextView_background, background);
		background_pressed = a.getInt(R.styleable.LeeTextView_background_pressed, background_pressed);
		text = a.getString(R.styleable.LeeTextView_text);
		a.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int a = event.getAction();
		if(a == MotionEvent.ACTION_DOWN){
			paintBackground = background_pressed;
			paintTextColor = textColor_pressed;
			return true;
		}else if(a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_OUTSIDE){
			paintBackground = textColor;
			paintTextColor = background;
			return true;
		}
		return super.onTouchEvent(event);
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		
//		setMeasuredDimension(measuredWidth, measuredHeight);
//	}
	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		paint.setColor(paintBackground);
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
		paint.setColor(textColor);
		canvas.drawText(text, getLeft(), getTop(), paint);
	}
}
