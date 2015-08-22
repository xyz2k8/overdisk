package com.xyz2k8.overdisk;

import com.xyz2k8.utils.MyUtils;

import android.content.Context;  
import android.content.res.TypedArray;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;  
import android.view.MotionEvent;
import android.view.View;  
  
public class PercentPanel extends View{  
      
    private Paint percentPaint;
    
    private Paint circlePaint;
      
    private Paint textPaint;  
    private int textSize = 90;
    
    private Paint textPaint2;  
    private int textSize2 = textSize/4; 
      
    private String usedSize = null;
    private String totalSize = null;
    private int percent;
    private int curr_percent = 0;
    
    private int allLineWidth = 2;  
    private int percentLineWidth = 4;  
    private int lineHeight = 20;  
    private int lineHeight2 = 30;
    private int y_offset = 0;
    private int arrow_offset = 0;
    
    private Rect mRect = null;
    private OnRegionClickListener mListener;
    
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            postInvalidate();            
        }

    };
    
    public void delayToDraw(){
        if (curr_percent != percent) {
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
    }
    
    public interface OnRegionClickListener
    {
        void onRegionClick(int whitchPiece);
    }
    
    /** 设置点击事件*/
	public void setOnRegionClickListener(OnRegionClickListener l){
		mListener = l;
	}
  
    public PercentPanel(Context context) {    
        super(context);    
        init(null, 0);    
    }    
    
    public PercentPanel(Context context, AttributeSet attrs) {    
        super(context, attrs);    
        init(attrs, 0);    
    }    
    
    public PercentPanel(Context context, AttributeSet attrs, int defStyle) {    
        super(context, attrs, defStyle);    
        init(attrs, defStyle);    
    }    
  
    private void init(AttributeSet attrs, int defStyle) {  
        // TODO Auto-generated method stub  
        final TypedArray a = getContext().obtainStyledAttributes(    
                attrs, R.styleable.PercentPanel, defStyle, 0);     
        percent = a.getInt(R.styleable.PercentPanel_percent, 0);
        a.recycle();    
          
        percentPaint = new Paint();  
        percentPaint.setAntiAlias(true);
        percentPaint.setTextSize(25);
        percentPaint.setStrokeWidth(allLineWidth);
        percentPaint.setTextAlign(Align.CENTER);
        
        circlePaint = new Paint();  
        circlePaint.setAntiAlias(true);
        
        textPaint = new Paint();    
        textPaint.setTextSize(textSize);    
        textPaint.setAntiAlias(true); 
        
        textPaint2 = new Paint();    
        textPaint2.setTextSize(textSize2); 
        textPaint2.setTextAlign(Align.CENTER);
        textPaint2.setAntiAlias(true); 
    }  
    
    private void setPaintColor()
    {
    	//0xff0597d2 ,0xff49b956 , 0xffcc324b , 0xff1a4e95 , 0xff55bc75 , 0xffe55f3a
        //blue        green        red          blue2        green2       orange
        if(percent<= 25)
        {
        	circlePaint.setColor(0xff49b956);	
        	percentPaint.setColor(0xff49b956);
        }
        else if(percent>25&&percent<50)
        {
        	circlePaint.setColor(0xff0597d2);
        	percentPaint.setColor(0xff0597d2);
        }
        else if(percent>=50&&percent<75)
        {
        	circlePaint.setColor(0xffe55f3a);
        	percentPaint.setColor(0xffe55f3a);
        }
        else
        {
        	circlePaint.setColor(0xffcc324b);
        	percentPaint.setColor(0xffcc324b);
        }
        
        circlePaint.setColor(0xff0597d2);
    	percentPaint.setColor(0xff0597d2);
    }
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
          
        int width = getMeasuredWidth();    
        int height = getMeasuredHeight();    
        int pointX = width/2;    
        int pointY = height/2 + y_offset;
        int radius = 0;
        if(width > height)
        {
        	radius = 4*height/5;
        }
        else
        {
        	radius = 4*width/5;
        }
        
        setPaintColor();
            
        float degrees = (float) (270.0/100);
        
        //画1%刻度
        canvas.save();    
        canvas.translate(0,pointY);   
        canvas.rotate(-45, pointX, 0);        
        for(int i = 0;i<100;i++)
        {        
            canvas.drawLine(lineHeight, 0, 0, 0, percentPaint);  
            canvas.rotate(degrees, pointX, 0);                    
        }      
        canvas.restore();
          
        //画5% or 10%刻度
        canvas.save();    
        canvas.translate(0,pointY);   
        canvas.rotate(-45, pointX, 0);        
        for(int i = 0;i<=100;i++)
        {    
         	if(i%10 == 0)
        	{
        		canvas.drawLine(0 , 0, lineHeight2, 0, percentPaint);
        		//canvas.drawText(i+"", lineHeight2 + 15, 0, percentPaint);
        	}
        	else if(i%5 == 0)
        	{
        		canvas.drawLine(lineHeight2 - lineHeight, 0, lineHeight2, 0, percentPaint);
        	}
         	canvas.rotate(degrees, pointX, 0);
        }      
        canvas.restore();
        
        for(int i = 0;i<=100;i++)
        {    
         	if(i%10 == 0)
        	{
         		float midDegree = (135 + i*degrees+360)%360;
         		float x  = (float) ((pointY - lineHeight2*3.5) * Math.cos(midDegree * Math.PI/180));
         		float y  = (float) ((pointY - lineHeight2*3.5) * Math.sin(midDegree * Math.PI/180));
         		x = x + pointX;
        		y = y + pointY;
        		
         		canvas.drawText(i+"", x, y, percentPaint);
        	}
        }
        
        //画占用比率指针
        percentPaint.setStrokeWidth(percentLineWidth);  
        canvas.save();
        curr_percent += 2;
        if(curr_percent >= percent)
        {
        	curr_percent = percent;
        }
        canvas.translate(0,pointY);   
        canvas.rotate(-45, pointX, 0); 
        canvas.rotate(degrees * curr_percent, pointX, 0);
        canvas.drawLine(arrow_offset, 0, lineHeight + pointX, 0, percentPaint);
              
        canvas.restore();  
        
        canvas.drawCircle(pointX, pointY, (int)(0.425*radius), circlePaint);
        
        float textWidth = textPaint.measureText(percent + "%");    
        textPaint.setColor(Color.WHITE);
        canvas.drawText(percent+"%",pointX - textWidth/2,pointY,textPaint);
        
        textPaint2.setColor(Color.WHITE);
        String info = usedSize + "/" + totalSize;
        canvas.drawText(info,pointX,pointY + textPaint.getTextSize()/2,textPaint2); 
        
        textPaint2.setColor(Color.WHITE);
        canvas.drawText("OverDisk",pointX,pointY + textPaint.getTextSize()/2 + 3*textPaint2.getTextSize()/2,textPaint2);
        
        if(null == mRect)
        {
        	Rect rect = new Rect();
	        textPaint2.getTextBounds("OverDisk", 0, ("OverDisk").length(), rect);
	        
	        mRect = new Rect((int)(pointX - rect.width()/2), (int)(pointY + textPaint.getTextSize()/2 + 3*textPaint2.getTextSize()/2 - rect.height()/2),
	        		         (int)(pointX + rect.width()/2), (int)(pointY + textPaint.getTextSize()/2 + 3*textPaint2.getTextSize()/2 + rect.height()/2));
        }
          
        percentPaint.setStrokeWidth(allLineWidth);
        delayToDraw();
    }
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        // TODO Auto-generated method stub  
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
        int width = MeasureSpec.getSize(widthMeasureSpec);    
        int height = MeasureSpec.getSize(heightMeasureSpec);    
        int d = (width >= height) ? height : width;
        
        textSize = (int)(0.2*width);
        textSize2 = textSize/4;
        
        percentPaint.setTextSize((int)(0.05*width));
        textPaint.setTextSize(textSize);
        textPaint2.setTextSize(textSize2);
        setMeasuredDimension(d,d);
        
        lineHeight  = (int)(0.038*width);  
        lineHeight2 = (int)(0.058*width);
        y_offset    = (int)(0.096*width);
        arrow_offset= (int)(0.116*width);
    }
  
    public void setPercent(int percent) {  
        // TODO Auto-generated method stub  
        this.percent = percent;  
        postInvalidate();  
    }
    
    public void setInfo(long total, long used)
    {
    	if(0 == total)
    	{
    		this.percent = 0;
    	}
    	
    	this.percent = (int) (used*100/total);
    	//this.percent = 90;
    	
    	usedSize = MyUtils.formatSize(used);
    	totalSize = MyUtils.formatSize(total); 
    	postInvalidate(); 
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) 
		{
						
		}
		else if(event.getAction() == MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL)
		{
			int upIndex = getTouchArea(event.getX() , event.getY());
			if(mListener != null)
			{
				mListener.onRegionClick(upIndex);
			}
			this.invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_CANCEL)
		{
			//mSelectIndex = -2;
			this.invalidate();
		}
		
		return true;
	}
    
    public int getTouchArea(float x, float y){
		
    	if(mRect == null)
    	{
    		return -1;
    	}
    	
    	if(mRect.contains((int)(x),(int)(y)))
    	{
    		return 0;
    	}
    	
    	return -1;
	}
}  
