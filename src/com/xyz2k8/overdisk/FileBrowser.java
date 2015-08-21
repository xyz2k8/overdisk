package com.xyz2k8.overdisk;

import java.util.ArrayList;

import com.xyz2k8.overdisk.ActionSheet.OnActionSheetSelected;
import com.xyz2k8.overdisk.SelectOpration.OnOprationSelected;
import com.xyz2k8.utils.MyUtils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public final class FileBrowser extends Activity implements OnActionSheetSelected, OnCancelListener, OnOprationSelected{
	
	private FileManager         mFileMag;
	private String              mRootPath = null;
	private ArrayList<String>   mChildren = null;
	private ArrayList<ListItem> mItemList = null;

	private FileListViewAdapter mListItemAdapter;
	private ListView            mListView;
	private ListItem            mTouchedItem = null;
	private int                 mPrePos = 0;
	
	private Handler mHandler = new Handler()
	{
        public void handleMessage(Message msg) 
        {
            switch (msg.what)
            {
	            //case UPDATE_RING:
	            //	updateView();
	            //	break;
	            
	            default:
	            	break;
            } 
            return;
        }
    };
    
    public Handler getHandler()
	{
        return mHandler;
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.file_browser);
        
        ImageButton backBtn = (ImageButton)findViewById(R.id.title_bar_nav_btn);
	    backBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
	    });
        
        Bundle bundle = new Bundle();
	    bundle = this.getIntent().getExtras();
	    mRootPath = bundle.getString("path");
	    mFileMag = new FileManager();
	    mChildren = mFileMag.setHomeDir(mRootPath);
	    
	    buildItemList();
	    
	    mListItemAdapter = new FileListViewAdapter(this, R.layout.files_list_item, mItemList);
        mListView = (ListView)findViewById(R.id.fileListView);
        mListView.setAdapter(mListItemAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
            	ListItem item = (ListItem)mListItemAdapter.getItem(position);
            	changePath(item.itemName ,position);
            }
        });
        
        mListView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				//String currentDir = mFileMag.getCurrentDir();
			    
				ListItem touchedItem = ((ListItem)mListItemAdapter.getItem(position));
				
				if(touchedItem.itemName.equals(".."))
				{
					return true;
				}
				
				showDeleteDialog(touchedItem);
				// show delete file dialog
				return true;
			}
		});
    }
	
	private void buildItemList()
	{
		if(null == mChildren)
		{
			return;
		}
		if(null != mItemList)
		{
		    mItemList.clear();
		}
		else
		{
			mItemList = new ArrayList<ListItem>();
		}
		for(int i=0; i<mChildren.size(); i++)
		{
			ListItem item = new ListItem();
			item.itemName = mChildren.get(i);			
			if(mFileMag.isDirectory(item.itemName))
			{
				item.itemType = "D";
				//item.itemSize = MyUtils.formatSize(mFileMag.getDirSize(item.itemName));
			}
			else
			{
				item.itemType = "F";
				item.itemSize = MyUtils.formatSize(mFileMag.getFileSize(mFileMag.getCurrentDir() + "/" + item.itemName));
			}
			mItemList.add(item);
		}
	}
	
	private void showDeleteDialog(ListItem touchedItem)
	{

		mTouchedItem = touchedItem;
		
		SelectOpration.showDeleteSheet(this,this,this);
	}
	
	private void changePath(String touchedItemName, int position)
	{
		if(null == touchedItemName)
		{
			return;
		}
		
		String currentDir = mFileMag.getCurrentDir();
		if(touchedItemName.equals(".."))
		{
			if(currentDir.equals("/") || currentDir.equals(mRootPath))
			{
				Toast.makeText(this, "当前已经是根目录", Toast.LENGTH_SHORT).show();
				return;
			}
			else
			{
				mChildren = mFileMag.getPreviousDir();
				buildItemList();
				mListItemAdapter.notifyDataSetChanged();
				mListView.setSelection(mPrePos);
			}
		}
		else
		{	
			if(mFileMag.isDirectory(touchedItemName))
			{
				mChildren = mFileMag.getNextDir(currentDir + "/" + touchedItemName, true);
				buildItemList();
				mListItemAdapter.notifyDataSetChanged();			
			}
			else
			{
				Toast.makeText(this, "当前不是目录", Toast.LENGTH_SHORT).show();
			}
			mPrePos = position;
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onActionSelected(int whichAction, String title) {
		if(ActionSheet.ACTION_YES == whichAction)			
		{
			if(title.equals("确定删除吗？") && null != mTouchedItem)
			{
				mFileMag.deleteTarget(mFileMag.getCurrentDir() + "/" + mTouchedItem.itemName);
				deleteTouchedItemNameFromList();
			}
		}
	}
	
	private void deleteTouchedItemNameFromList()
	{
		if(null == mTouchedItem || null == mItemList)
		{
			return;
		}
		
		mItemList.remove(mTouchedItem);
		mListItemAdapter.notifyDataSetChanged();
	}

	@Override
	public void onOprationSelected(int whichOprationSelected) {
		
		if(SelectOpration.OP_DELETE == whichOprationSelected)			
		{
			String[] info = new String[2];
			
			info[0] = "文件名称 - ";
			info[1] = mTouchedItem.itemName;

			ActionSheet.showActionSheet(this, this, this, "确定删除吗？", info);		
		}
		else
		{
			
		}
	}
	
	@Override
    public void onBackPressed() {
		finish();
    }
}
