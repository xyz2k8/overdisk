package com.xyz2k8.overdisk;

import android.graphics.RectF;

public class RectMenuItem  {	
	private String title;
	private int index;
	private RectF rect = null;
	public RectMenuItem (String title,int index){
		this.title =title;
		this.index = index;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setRect(RectF rect)
	{
		this.rect = rect;
	}
	
	public RectF getRect()
	{
		return this.rect;
	}
}
