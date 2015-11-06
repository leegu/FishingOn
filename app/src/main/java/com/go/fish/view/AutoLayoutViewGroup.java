package com.go.fish.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class AutoLayoutViewGroup extends ViewGroup {  
//    private int mCellWidth;  
//    private int mCellHeight;  
  
    public AutoLayoutViewGroup(Context context) {  
        super(context);  
    }  
  
    public AutoLayoutViewGroup(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public AutoLayoutViewGroup(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    /** 
     * 控制子控件的换行 
     */  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        int count = getChildCount();  
        int maxWidth = getWidth();
        int offLeft = 0,offTop = 0;
        int totalUseHeight = 0;
        int rowMaxHieght = 0;
        for (int j = 0; j < count; j++) {  
            final View childView = getChildAt(j);  
            // 获取子控件Child的宽高  
            int w = childView.getMeasuredWidth();  
            int h = childView.getMeasuredHeight();  
            // int left = x;  
            // int top = y;  
            // 布局子控件  
            rowMaxHieght = Math.max(rowMaxHieght, h);
            if(maxWidth - w >= offLeft){
            	
            }else{
            	offLeft = 0;
            	totalUseHeight += rowMaxHieght;
            	offTop = totalUseHeight;
            	rowMaxHieght = h;
            }
            childView.layout(offLeft + l, offTop + t, offLeft + l + w, offTop + t + h);  
            offLeft += w;
        }  
        postInvalidate();
    }  
  
    /** 
     * 计算控件及子控件所占区域 
     */  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // 创建测量参数  
    	LayoutParams lp = ((View)getParent()).getLayoutParams();
    	int maxWidth = lp.width;
    	if(maxWidth < 0){
    		maxWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    	}
        int cellWidthSpec = widthMeasureSpec;  
        int cellHeightSpec = heightMeasureSpec;  
        // 记录ViewGroup中Child的总个数  
        int count = getChildCount();  
        // 设置子空间Child的宽高  
        int measuredWidth = maxWidth;
        int measuredHeight = 0;
        int useWidth = 0;
        int lastMaxHeight = 0;
        int rowCount = 1;
        for (int i = 0; i < count; i++) {  
            View childView = getChildAt(i);  
            int ws = childView.getLayoutParams().width;
            int hs = childView.getLayoutParams().height;
            hs = hs > 0 ? (hs + childView.getPaddingTop() + childView.getPaddingBottom() ): cellHeightSpec;
            ws = ws > 0 ?( ws + childView.getPaddingLeft() + childView.getPaddingRight() ): cellWidthSpec;
            childView.measure(MeasureSpec.makeMeasureSpec(ws,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(hs,MeasureSpec.EXACTLY));
            int cw = childView.getMeasuredWidth() ;
            int ch = childView.getMeasuredHeight() ;
            lastMaxHeight = Math.max(lastMaxHeight, ch);
//            Log.d("yl", "1111useWidth=" + useWidth + ";cw=" + cw);
            if((maxWidth - useWidth) >= cw){//剩余空间可以容下
            	useWidth += cw;
            }else{
        		measuredHeight += lastMaxHeight;//记录前一行
        		
            	lastMaxHeight = ch;//重新记行
            	rowCount++;
            	useWidth = cw;
            }
            if(i == count - 1){//最后一行由最后一个child时候做决定
            	measuredHeight += lastMaxHeight;
            }
            Log.d("yl", rowCount + " useWidth=" + useWidth + ";cw=" + cw + ";lastMaxHeight=" + lastMaxHeight + ";ch=" + ch);
        }  
        Log.d("yl", "measuredHeight=" + measuredHeight);
        setMeasuredDimension(measuredWidth,measuredHeight );  
        // 不需要调用父类的方法  
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
    }  
  
//    /** 
//     * 为控件添加边框 
//     */  
//    @Override  
//    protected void dispatchDraw(Canvas canvas) {
//        // 获取布局控件宽高
//        int width = getWidth();
//        int height = getHeight();
//        // 创建画笔
//        Paint mPaint = new Paint();
//        // 设置画笔的各个属性
//        mPaint.setColor(Color.BLUE);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(10);
//        mPaint.setAntiAlias(true);
//        // 创建矩形框
//        Rect mRect = new Rect(0, 0, width, height);
//        // 绘制边框
//        canvas.drawRect(mRect, mPaint);
//        // 最后必须调用父类的方法
//        super.dispatchDraw(canvas);
//    }
  
}  