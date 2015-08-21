package com.xyz2k8.overdisk;

import java.util.ArrayList;

public class Node {
	
	long mSize = 0;
	String mShortName = null;
	String mFullPath = null;	
	boolean mIsDir = true;
	int mFilesNum = 0;
	Node   mParent = null;
	//Node[] mChildren = null;
	ArrayList<Node> mChildren = null;
	
	public Node(String fullPath)
	{
		mFullPath = fullPath;
		String[] dirs = fullPath.split("/");
		if(dirs.length == 0)
		{
		    mShortName = mFullPath;	
		}
		else
		{
			mShortName = dirs[dirs.length-1];
		}
	}

	private float[] convertToAngle(long [] list, float factor)
	{
		float[] angle = new float[list.length];
		
		long sum = sumList(list);
		
		if(sum == 0)
		{
			return new float[0];
		}
		
		for(int i = 0 ; i < list.length ; i++) {
			angle[i] = factor*list[i]*360.0f/sum;
        }
		
		return angle;
	}
	
	private long sumList(long [] list)
	{
		long sum = 0;
		
		for(int i = 0 ; i < list.length ; i++) {  
			sum += list[i];  
        }
		
		return sum;
	}
	
	public float[] getAngleList(float factor)
	{
		if(null == mChildren)
		{
			return null;
		}
		
		long[] size  = new long[mChildren.size()];
		
		for(int i=0; i < mChildren.size(); i++)
		{
			//size[i] = mChildren[i].mSize;
			size[i] = mChildren.get(i).mSize;
		}
		
		return convertToAngle(size,factor);
	}
}
