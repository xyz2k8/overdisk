package com.xyz2k8.overdisk;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xyz2k8.overdisk.R;
import com.xyz2k8.utils.MyUtils;

public class RingView extends View {
	
	private OverDiskActivity mActivity = null;
	private Paint mPaint;
	
	//private Node mRootDirNodeInfo = null;
	private Node mCurrentDirNodeInfo = null;
	//private Node mParentDirNodeInfo = null;
	
	private Node mTouchedNode = null;
	private int mCenterX = 0;
	private int mCenterY = 0;
	
	private int mCenter = 0;
	private int mGapWidth = 4;
	private int mRingWidth = 0;
	private int mCenterRadius = 0;
	private int mL1Radius = 0;
	private int mL2Radius = 0;
	
	private static final int LAYER_1 = 1;
	private static final int LAYER_2 = 2;
	private static final int LAYER_3 = 3;
	
	private TouchedViewPos mTouchedViewPos = new TouchedViewPos();
	
	public RingView(Context context, AttributeSet attrs)
	{		
		this(context, attrs, 0);
	}

	public RingView(Context context)
	{		
		this(context, null);
	}
	
	public RingView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		if(null == mPaint)
		{
			mPaint = new Paint();
		}
		
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null == mTouchedViewPos || !mTouchedViewPos.mSuccess)
				{
					return;
				}
				
				rebuildNodeTree();
				postInvalidate();
			}
			
        });
		
		setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(null == mTouchedViewPos || !mTouchedViewPos.mSuccess)
				{
					return false;
				}
		
				mTouchedNode = getTouchedNode();
				if(null != mTouchedNode)
				{	
					SelectOpration.showOprationSheet(mActivity, mActivity, mActivity);
				}			
				
				//deleteTouchedNode();
				return true;
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if(mUpdateRing)
		{
			calcParameters();
			drawView(canvas);
		}
	}
	
	
	private void calcParameters()
	{	
		int delta = 0;
		if(getWidth() > getHeight())
		{
			delta = getWidth() - getHeight();
			mCenterX = getWidth() / 2; // - getHeight() / 2 - delta;
			mCenterY = getHeight() / 2;
		}
		else
		{
			delta = getHeight() - getWidth();
			mCenterX = getWidth() / 2;
			mCenterY = getHeight() - getWidth() / 2 - delta;		
		}
		
		mCenter = mCenterX < mCenterY ? mCenterX : mCenterY;
		mGapWidth = 16;
		
		mRingWidth = mCenter/4;
		
		mCenterRadius = mRingWidth;		
		mL1Radius = 2*mRingWidth;
		mL2Radius = 3*mRingWidth;
	}
	
	private void updateInfoView()
	{
		Handler handler = ((OverDiskActivity)getContext()).getHandler();
		if(null == handler)
		{
			return;
		}
		
		Message msg = new Message();
		msg.what = OverDiskActivity.UPDATE_INFO;
		msg.obj = (Object)getInfoItems();
		
		handler.sendMessage(msg);
	}
	
	private void drawCircle(Canvas canvas, String path, long size)
	{
		/* 画中心圆 */
		mPaint.setAntiAlias(true);
		
		mPaint.setStyle(Paint.Style.FILL);
		
		//if(isRootNodeTouched())
		//{
		//	mPaint.setColor(0xFFcacccc);		    	
		//}
		//else
		//{
			mPaint.setColor(getResources().getColor(R.color.center));
		//}
		canvas.drawCircle(mCenterX, mCenterY, mCenterRadius*5/4, mPaint);
		
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
        
        textPaint.setTextSize(24.0f);  
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度  
        textPaint.setColor(getResources().getColor(R.color.white));
        
        textPaint.setTextAlign(Align.CENTER);
        
        FontMetrics fontMetrics = textPaint.getFontMetrics();  
        //font height 
        float fontHeight = fontMetrics.bottom - fontMetrics.top;  
        //font baseline
        float baseY = mCenterY;

        if(path.length() > 10)
        {
        	String[] temp = path.split("\\.");
        	path = path.substring(0,6);
        	path += "~";
        	if(temp.length > 1)
        	{
        		path += "." + temp[temp.length - 1];
        	}
        }
        canvas.drawText(path, mCenterX, baseY, textPaint);  
        canvas.drawText(MyUtils.formatSize(size), mCenterX, baseY + fontHeight , textPaint);
	}
	//0xff0597d2 ,0xff49b956 , 0xffcc324b , 0xff1a4e95 , 0xff55bc75 , 0xffe55f3a
    //blue        green        red          blue2        green2       orange
	private int[] mColor = {0xff0597d2 ,0xff49b956 , 0xffcc324b , /* 0xff1a4e95 ,*/ 0xff55bc75 , 0xffe55f3a}; 
	private int getRandColor()
	{
		int index = 0;
		index  = new Random().nextInt(10000);
		index %= mColor.length;
		//return getResources().getColor(mColor[index]);		
		return mColor[index];
	}
	
	private void drawRing(Canvas canvas, int radius, float[] angle, float startAngle, float sweepAngle)
	{	
		if(null == angle)
		{
			return;
		}
		
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(mRingWidth - mGapWidth);
		
		RectF oval = new RectF(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY + radius);			
		
		for(int i=0; i< angle.length; i++)
		{
			//if(isNodeTouched(mL1Radius,i+1,0))
			//{
			//	mPaint.setColor(0xFFcacccc);
			//}
			//else
			//{
				mPaint.setColor(getRandColor());
			//}
			canvas.drawArc(oval, (float)((int)(startAngle + 0.5f)), (float)((int)(angle[i] - 1.0f)), false, mPaint);
			startAngle += angle[i];
		}
	}
	
	private void drawView(Canvas canvas)
	{
		if(null == mCurrentDirNodeInfo)
		{
			return;
		}
		
		updateInfoView();
		
		drawCircle(canvas, mCurrentDirNodeInfo.mShortName, mCurrentDirNodeInfo.mSize);
		
		float[] angleList = mCurrentDirNodeInfo.getAngleList(1.0f);
		if(null == angleList)
		{
			return;
		}
		
		drawRing(canvas, mL1Radius, angleList, 0, 360);
		
		ArrayList<Node> childNodes =  mCurrentDirNodeInfo.mChildren;
		
		if(null == childNodes)
		{
			return;
		}
		
		float startAngle = 0.0f;
		float factor     = 1.0f;
		for(int i=0; i< angleList.length; i++)
		{
			if(null == childNodes.get(i))
			{
				continue;
			}
			factor = angleList[i]/360;
			drawRing(canvas, mL2Radius, childNodes.get(i).getAngleList(factor), startAngle, angleList[i]);
			startAngle += angleList[i];
		}
	}
	
	public void updateView(Node dirNodeInfo, OverDiskActivity activity)
	{	
		if(null == activity || null == dirNodeInfo)
		{
			return;
		}
		mActivity = activity;
		if(null == mCurrentDirNodeInfo)
		{
		    mCurrentDirNodeInfo = dirNodeInfo;
		}
		mUpdateRing = true;
		postInvalidate();
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) 
    {
		if(MotionEvent.ACTION_DOWN == event.getAction())
		{
			mTouchedViewPos = getTouchedPos(event);
		}
		
        return super.onTouchEvent(event);
    }	
	
	public Node getCurrentNode()
	{
		return mTouchedNode;
	}
	
	private int mNodeNum = 0;
	public void deleteTouchedNode()
	{
		if(null == mTouchedNode)
		{
			return;
		}
		mUpdateRing = false;
		
		if(mCurrentDirNodeInfo == mTouchedNode)
		{
			mCurrentDirNodeInfo = mTouchedNode.mParent;
		}
		
		mNodeNum = 0;
		deleteFile(mTouchedNode.mFullPath);
		mNodeNum = 0;
		deleteChild(mTouchedNode);
		mTouchedNode = null;
		postInvalidate();
	}
	
	private boolean mUpdateRing = true;
	public void hideTouchedNode()
	{
		if(null == mTouchedNode)
		{
			return;
		}
		
		mUpdateRing = false;
		
		if(mCurrentDirNodeInfo == mTouchedNode)
		{
			mCurrentDirNodeInfo = mTouchedNode.mParent;
		}
		
		deleteChild(mTouchedNode);
		mTouchedNode = null;
		postInvalidate();
	}
	
	public void openTouchedNodeInFileBrowser()
	{
		if(null == mTouchedNode)
		{
			return;
		}
		
		if(mTouchedNode.mIsDir)
		{
			//openInBrowser(new File(mTouchedNode.mFullPath));
			openInBrowser(mTouchedNode.mFullPath);
			return;
		}
		
		if(null == mTouchedNode.mParent)
		{
			return;
		}
		
		//openInBrowser(new File(mTouchedNode.mParent.mFullPath));	
		openInBrowser(mTouchedNode.mParent.mFullPath);
	}
	
    private void openInBrowser(String dir) 
    {
    	Intent intent = new Intent();
    	Bundle bundle = new Bundle();
      	bundle.putString("path", dir);
      	
      	intent.setClass(mActivity,FileBrowser.class);
      	intent.putExtras(bundle);      	
      	mActivity.startActivityForResult(intent, 0);
	}
	
	private void deleteChild(Node child)
	{
		if(null == child || null == child.mParent)
		{
			return;
		}
		
		if(mNodeNum%50 == 0)
		{
			Message message = Message.obtain();
			message.what = OverDiskActivity.DEL_PROGRESS_INFO;
			message.obj = (Object)(child.mFullPath);
			if(null != mActivity)
			{
				mActivity.getHandler().sendMessage(message);
			}
		}
		
		mNodeNum++;
		
		Node parent = child.mParent;
		//parent.mSize -= child.mSize;
		//parent.mFilesNum -= 1;
		
		ArrayList<Node> nodesInParent = parent.mChildren;
		if(null != nodesInParent && nodesInParent.size() > 0)
		{
			ArrayList<Node> newnodesInParent = new ArrayList<Node>();
			for(int loop=0; loop<nodesInParent.size(); loop++)
			{
				if(child != nodesInParent.get(loop))
				{
					newnodesInParent.add(nodesInParent.get(loop));					
				}
			}
			
			if(newnodesInParent.size() == 0)
			{
				parent.mChildren = null;
				parent.mSize = 0;
				parent.mFilesNum = 0;
			}
			else
			{
				parent.mChildren = newnodesInParent;
				parent.mFilesNum = newnodesInParent.size();
			}
		}
		
		deletParentSize(parent,child.mSize);
	}
	
	private void deletParentSize(Node parent, long size)
	{
		if(null == parent)
		{
			return;
		}
		
		parent.mSize -= size;
		deletParentSize(parent.mParent, size);
	}
	
	private int deleteFile(String path)
	{
		if(mNodeNum%5 == 0)
		{
			Message message = Message.obtain();
			message.what = OverDiskActivity.DEL_PROGRESS_INFO;
			message.obj = (Object)(path);
			if(null != mActivity)
			{
				mActivity.getHandler().sendMessage(message);
			}
		}
		
		mNodeNum++;
		
		File target = new File(path);
		
		if(target.exists() && target.isFile() && target.canWrite()) 
		{
			target.delete();
			return 0;
		}		
		else if(target.exists() && target.isDirectory() && target.canRead()) 
		{
			String[] file_list = target.list();			
			if(file_list != null && file_list.length == 0) 
			{
				target.delete();
				return 0;				
			} 
			else if(file_list != null && file_list.length > 0) 
			{	
				for(int i = 0; i < file_list.length; i++) 
				{
					File temp_f = new File(target.getAbsolutePath() + "/" + file_list[i]);
					if(temp_f.isDirectory())
					{
						deleteFile(temp_f.getAbsolutePath());
					}
					else if(temp_f.isFile())
					{
						temp_f.delete();
					}
				}
			}
			if(target.exists())
			{
				if(target.delete())
				{
					return 0;
				}
			}
		}
		
		return -1;
	}
	
	private Node getTouchedNode()
	{
		Node touchedNode = null;
		Node node = null;
		if(null == mTouchedViewPos || !mTouchedViewPos.mSuccess)
		{
			return null;
		}
		
		if(mTouchedViewPos.mLayer == LAYER_1)
		{
			node = mCurrentDirNodeInfo;
			return node;
		}
		
		ArrayList<Node> childNodes = mCurrentDirNodeInfo.mChildren;
		if(null == childNodes)
		{
			return null;
		}
		
		if(mTouchedViewPos.mLayer >= LAYER_2)
		{			
			if(childNodes.size() < mTouchedViewPos.mSeqInLayer_2)
			{
				return null;
			}			
						
			touchedNode = childNodes.get(mTouchedViewPos.mSeqInLayer_2 - 1);			
		}
		
		if(mTouchedViewPos.mLayer == LAYER_2)
		{
			return touchedNode;
		}
		
		if(mTouchedViewPos.mLayer >= LAYER_3)
		{
			ArrayList<Node> level2_childNodes = childNodes.get(mTouchedViewPos.mSeqInLayer_2 - 1).mChildren;
			if(null == level2_childNodes)
			{
				return null;
			}
			
			if(level2_childNodes.size() < mTouchedViewPos.mSeqInLayer_3)
			{
				return null;
			}
			
			return level2_childNodes.get(mTouchedViewPos.mSeqInLayer_3 - 1);			
		}
		
		return null;
	}
	
	private void rebuildNodeTree()
	{	
		if(null == mTouchedViewPos || !mTouchedViewPos.mSuccess)
		{
			return;
		}
		
		if(mTouchedViewPos.mLayer == LAYER_1)
		{
			if(mCurrentDirNodeInfo.mParent == null)
			{
				Toast.makeText(getContext(), "亲，爸爸不知道去哪儿了~", Toast.LENGTH_SHORT).show();
				return;
			}
			
			mCurrentDirNodeInfo = mCurrentDirNodeInfo.mParent;			
			return;
		}
		
		ArrayList<Node> childNodes = mCurrentDirNodeInfo.mChildren;
		if(null == childNodes)
		{
			return;
		}
		
		if(mTouchedViewPos.mLayer >= LAYER_2)
		{			
			if(childNodes.size() < mTouchedViewPos.mSeqInLayer_2)
			{
				return;
			}			
						
			mCurrentDirNodeInfo = childNodes.get(mTouchedViewPos.mSeqInLayer_2 - 1);			
		}
		
		if(mTouchedViewPos.mLayer == LAYER_2)
		{
			return;
		}
		
		if(mTouchedViewPos.mLayer >= LAYER_3)
		{
			ArrayList<Node> level2_childNodes = childNodes.get(mTouchedViewPos.mSeqInLayer_2 - 1).mChildren;
			if(null == level2_childNodes)
			{
				return;
			}
			
			if(level2_childNodes.size() < mTouchedViewPos.mSeqInLayer_3)
			{
				return;
			}
			
			mCurrentDirNodeInfo = level2_childNodes.get(mTouchedViewPos.mSeqInLayer_3 - 1);
			return;
		}
		
		return;
	}
			
	private TouchedViewPos getTouchedPos(MotionEvent event)
	{
		TouchedPos touchedPos = new TouchedPos();
		
		touchedPos.mX = (int)event.getX();  
		touchedPos.mY = (int)event.getY();
		
		return convertTouchPos2View(touchedPos);
	}
	
	private void calcSeq(TouchedViewPos touchedViewPos, double degree)
	{
		if(null == touchedViewPos || null == mCurrentDirNodeInfo)
		{
			return;
		}
		
		if(touchedViewPos.mLayer == LAYER_1)
		{	
			touchedViewPos.mSuccess = true; 
			touchedViewPos.mSeqInLayer_2 = 0;
			touchedViewPos.mSeqInLayer_3 = 0;
			return;
		}
		
		float[] angleList = mCurrentDirNodeInfo.getAngleList(1.0f);
		if(null == angleList)
		{
			touchedViewPos.mSuccess = false;
			return;
		}
		
		float total = 0;
		if(touchedViewPos.mLayer >= LAYER_2)
		{	
			int loop = 0;
			for(loop=0; loop<angleList.length; loop++)
			{
				if(degree>total && degree<total + angleList[loop])
				{
					touchedViewPos.mSuccess = true;
					touchedViewPos.mSeqInLayer_2 = loop + 1;
					touchedViewPos.mSeqInLayer_3 = 0;
					break;
				}
				total += angleList[loop];
			}
			
			if(!touchedViewPos.mSuccess)
			{
				touchedViewPos.mSuccess = false;
				touchedViewPos.mSeqInLayer_2 = 0;
				touchedViewPos.mSeqInLayer_3 = 0;
			}
		}
		
		if(LAYER_2 == touchedViewPos.mLayer)
		{	
			return;
		}
		
		touchedViewPos.mSuccess = false;
		
		if(LAYER_3 == touchedViewPos.mLayer)
		{	
			ArrayList<Node> childNodes = mCurrentDirNodeInfo.mChildren;
			if(null == childNodes)
			{
				touchedViewPos.mSuccess = false;
				return;
			}
			
			if(touchedViewPos.mSeqInLayer_2 > angleList.length || touchedViewPos.mSeqInLayer_2 > childNodes.size())
			{
				touchedViewPos.mSuccess = false;
				return;
			}
			
			float factor = angleList[touchedViewPos.mSeqInLayer_2 - 1]/360.0f;
			
			float[] level2_angleList = childNodes.get(touchedViewPos.mSeqInLayer_2 - 1).getAngleList(factor);
			if(null == level2_angleList)
			{
				touchedViewPos.mSuccess = false;
				return;
			}
			
			int loop = 0;
			
			for(loop=0; loop<level2_angleList.length; loop++)
			{
				if(degree>total && degree<total + level2_angleList[loop])
				{
					touchedViewPos.mSuccess = true;
					touchedViewPos.mSeqInLayer_3 = loop + 1;
					break;
				}
				total += level2_angleList[loop];
			}
			
			if(!touchedViewPos.mSuccess)
			{
				touchedViewPos.mSuccess = false;
				touchedViewPos.mSeqInLayer_3 = 0;
			}
		}				
	}
		
	private TouchedViewPos convertTouchPos2View(TouchedPos touchedPos)
	{
		TouchedViewPos touchedViewPos = new TouchedViewPos();
		int distance = 0;
		
		distance = (int)Math.sqrt(Math.pow(touchedPos.mX - mCenterX,2) + Math.pow(touchedPos.mY - mCenterY,2));
		if(distance < mCenterRadius)
		{
			touchedViewPos.mLayer = LAYER_1;
		}
		else if(distance > (mL1Radius - mRingWidth/2) && distance < (mL1Radius + mRingWidth/2))
		{
			touchedViewPos.mLayer = LAYER_2;
		}
		else if(distance > (mL2Radius - mRingWidth/2) && distance < (mL2Radius + mRingWidth/2))
		{
			touchedViewPos.mLayer = LAYER_3;
		}
		
		double degree = Math.atan2((double)(touchedPos.mY - mCenterY), (double)(touchedPos.mX - mCenterX));
		degree = Math.toDegrees (degree);
		degree = (degree+360)%360;
		calcSeq(touchedViewPos, degree);				
		
		return touchedViewPos;
	}
	
	
	private class TouchedPos
	{
		public int mX = 0;
		public int mY = 0;
	}
	
	private class TouchedViewPos
	{
		public boolean mSuccess = false;
		public int mLayer = 0;
		public int mSeqInLayer_2 = 0;
		public int mSeqInLayer_3 = 0;		
	}
	
	public class InfoItem
	{	
		public String mItemName = null;		
		public String mItemContent = null;
	}
	
	private InfoItem[] getInfoItems()
	{
		InfoItem[] infoItems = new InfoItem[4];
		if(null == mCurrentDirNodeInfo)
		{
			return null;
		}
		
		infoItems[0] = new InfoItem();
		infoItems[1] = new InfoItem();
		infoItems[2] = new InfoItem();
		infoItems[3] = new InfoItem();
				
		infoItems[0].mItemName = "文件名称 - ";
		infoItems[0].mItemContent = mCurrentDirNodeInfo.mShortName;
		
		infoItems[1].mItemName = "文件大小 - ";
		infoItems[1].mItemContent = MyUtils.formatSize(mCurrentDirNodeInfo.mSize);
		
		infoItems[2].mItemName = "文件数目 - ";
		infoItems[2].mItemContent = mCurrentDirNodeInfo.mFilesNum + "";
		
		infoItems[3].mItemName = "文件路径 - ";
		
		infoItems[3].mItemContent = mCurrentDirNodeInfo.mFullPath;
		infoItems[3].mItemContent = infoItems[3].mItemContent.replace("//", "/");
		
		return infoItems;
	}	
}

