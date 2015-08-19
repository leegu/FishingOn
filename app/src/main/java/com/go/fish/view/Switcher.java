package com.go.fish.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class Switcher extends TextView {

	int turn_on_color = Color.GREEN;
	int turn_off_color = Color.GRAY;
	int state_color = Color.WHITE;
	int space = 2;
	boolean turnOn = true;
	int state_offest = 0;//
	int state_offest_min = 0;
	int state_offest_max = 0;
	int corner = 25;
	int state_circle_radius = 0;
	RectF rectF = null;
	Paint paint = new Paint();
	OnSwitcherChanged changedListener = null;
	public Switcher(Context context) {
		super(context);
//		setBackgroundColor(Color.LTGRAY);
//		setClickable(true);
//		setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				change();
//			}
//			
//		});
	}

	public Switcher(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		{
			paint.setStyle(Paint.Style.FILL);
			if(turnOn){
				paint.setColor(turn_on_color);
			}else{
				paint.setColor(turn_off_color);
			}
			paint.setAntiAlias(true);
			canvas.drawRoundRect(rectF, corner, corner, paint);
		}
		{
			paint.setColor(state_color);
			canvas.drawCircle(state_circle_radius + space + state_offest, space + state_circle_radius, state_circle_radius, paint);
			canvas.drawText("swer", 0, 0, paint);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		rectF = new RectF(0, 0, getWidth(), getHeight());
		state_circle_radius = getHeight() / 2 - space * 2;
		corner = getHeight() / 2;
		state_offest_max = getWidth() - state_circle_radius * 2 - space * 2;
		state_offest_min = 0;
		state_offest = state_offest_max;
	}
	float d_x = 0;
//	boolean mayBeClick = false;
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		int action = event.getAction();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			d_x = event.getX();
////			mayBeClick = true;
//			return true;
//		case MotionEvent.ACTION_UP:
//			float s = d_x - event.getX();
//			if(s < 0){
//				setTurnOn();
//			}else if(s > 0){
//				setTurnOff();
//			}else{
//				change();
//			}
////			if(mayBeClick && Math.abs(d_x - event.getX()) <= 10){
////				change();
////			}
//			return true;
//		case MotionEvent.ACTION_MOVE:
////			mayBeClick = false;
////			float m_x = event.getX();
////			changeOffset(m_x > d_x ? 1 : -1);
////			d_x = m_x;
////			return true;
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
	
//	private void changeOffset(int f){
//		state_offest += f;
//		if(state_offest <= state_offest_min){
//			state_offest = state_offest_min;
//		}else if(state_offest >= state_offest_max){
//			state_offest = state_offest_max;
//		}
//		invalidate();
//	}
	public void change() {
		if(turnOn){
			setTurnOff();
		}else{
			setTurnOn();
		}
	}
	private void setTurnOn(){
		if(!turnOn){
			turnOn = true;
			state_offest = state_offest_max;
			if(changedListener != null){
				changedListener.onChanage(this);
			}
		}
		invalidate();
	}
	private void setTurnOff(){
		state_offest = state_offest_min;
		if(turnOn){
			turnOn = false;
			if(changedListener != null){
				changedListener.onChanage(this);
			}
		}
		invalidate();
	}
	
	public boolean isTurnOn(){
		return turnOn;
	}
	public void setChangedListener(OnSwitcherChanged changedListener){
		this.changedListener = changedListener;
	}
	public static interface OnSwitcherChanged{
		void onChanage(View view);
	}
}
