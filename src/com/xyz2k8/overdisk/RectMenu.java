package com.xyz2k8.overdisk;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

@SuppressLint("DrawAllocation")
public class RectMenu extends View {
	
    public class Point 
    {
    	public float x;
    	public float y;   	 
    	public Point (float x , float y)
    	{
    		this.x = x;
    		this.y = y;
    	}
    } 
    
    public interface OnPieceClickListener 
    {
        void onPieceClick(int whitchPiece);
    }
    
    private Paint mMenuTextPaint;	
	private Paint mRectPaint;
	
	private int mMenuTextHeight = 0;
	
	private int mPieceNumber = 3;
	private int mDividerWidth = 10;
	private int mPadding = 25;
	private int mRectMenuWidth = 0;
	private int mRectMenuHeight = 0;
	
	private int mSelectIndex = -2;
	private int mMenuTextNormalColor = 0xFFcacccc;  
	private int mMenuTextPressedColor = 0xff0597d2;
	
	private List<RectMenuItem> items;
	private OnPieceClickListener mListener;
	
    private boolean blHasRoot = false;
    
    public void setHasRoot(boolean blRooted)
    {
    	blHasRoot = blRooted;    	
    }
	
	public RectMenu(Context context) {
		super(context);
		init(context , null, 0);
	}

	public RectMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context , attrs , 0);
	}

	public RectMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context , attrs , defStyle);
	}

	
	private void init(Context c , AttributeSet attrs , int defStyle) {		
		mMenuTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMenuTextPaint.setAntiAlias(true);
		mMenuTextPaint.setTextAlign(Align.CENTER);	
		
		mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mRectPaint.setColor(mMenuTextPressedColor);
		mRectPaint.setStyle(Paint.Style.STROKE);
		mRectPaint.setAntiAlias(true);			
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		
		mRectMenuWidth = (width - 2*mPadding - mDividerWidth*mPieceNumber)/mPieceNumber;
		mRectMenuHeight = height/2 - 2*mPadding;
		
		mMenuTextPaint.setTextSize(Math.min(width/10, height/10));
		mMenuTextPaint.setTextAlign(Align.CENTER);
		String all_ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789~!@#$%^&*()_+-=;:'',.<>/?";
		Rect bounds = new Rect();
		mMenuTextPaint.getTextBounds(all_ch, 0, all_ch.length(),bounds);
		mMenuTextHeight = bounds.height();
		setMeasuredDimension(width, height);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
		
        
		for(int i = 0;i < mPieceNumber ; i++)
		{
			drawRectMenu(i, canvas);
		}
	}
	
	public void drawRectMenu(int index , Canvas canvas){
		
		
		int centerX = mPadding + (mRectMenuWidth + mDividerWidth) * (index) + mRectMenuWidth/2;
		int centerY = mPadding +  mPadding + mRectMenuHeight/2;
		
		if(index == mSelectIndex)
		{
			mMenuTextPaint.setColor(mMenuTextNormalColor);
			mRectPaint.setColor(mMenuTextNormalColor);
		}
		else
		{
			mMenuTextPaint.setColor(mMenuTextPressedColor);
			mRectPaint.setColor(mMenuTextPressedColor);
		}
		
		if(0 == index && !blHasRoot)
		{
			mMenuTextPaint.setColor(mMenuTextNormalColor);
			mRectPaint.setColor(mMenuTextNormalColor);
		}
		
		canvas.drawText(items.get(index).getTitle(), centerX, centerY - mMenuTextHeight/2 - 5, mMenuTextPaint);
		
		//画分割线
        //canvas.drawLine(mPadding ,0, (mRectMenuWidth+mDividerWidth)*mPieceNumber + mPadding, 0, mMenuTextPaint);
        
		mRectPaint.setStrokeWidth((float) 2.0); 
		RectF rect = new RectF(centerX - mRectMenuWidth/2, centerY  - mMenuTextHeight * 2, centerX + mRectMenuWidth/2, centerY);
		canvas.drawRoundRect(rect, 15, 15,mRectPaint);
		
		if(0 == index && !blHasRoot)
		{
			return;
		}
		items.get(index).setRect(rect);
	}
	
	/** 根据触摸坐标获取扇区的索引 */
	public int getTouchArea(Point p){
		
		for(int i = 0 ; i < mPieceNumber ; i++)
		{
			RectF rect = items.get(i).getRect();
			if(null != rect && rect.contains(p.x, p.y))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) 
		{
			mSelectIndex = getTouchArea(new Point(event.getX() , event.getY()));
			this.invalidate();			
		}
		else if(event.getAction() == MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL)
		{
			int upIndex = getTouchArea(new Point(event.getX() , event.getY()));
			if(mListener != null)
			{
				mListener.onPieceClick(upIndex);
			}
			mSelectIndex = -2;
			this.invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			mSelectIndex = -2;
			this.invalidate();
		}
		return true;
	}
	
	public void setItems(List<RectMenuItem> items1){
		this.items = items1; 
		mPieceNumber = items.size();
	}
	
	/** 设置点击事件*/
	public void setOnPieceClickListener(OnPieceClickListener l){
		mListener = l;
	}

}
