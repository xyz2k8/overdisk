package com.xyz2k8.overdisk;

import java.io.File;
import java.util.ArrayList;

import com.xyz2k8.overdisk.ActionSheet.OnActionSheetSelected;
import com.xyz2k8.overdisk.SelectOpration.OnOprationSelected;
import com.xyz2k8.utils.MyUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
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
	    mRootPath = mRootPath.replace("//", "/");
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
			mRootPath = mRootPath.replace("//", "/");
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
				//Toast.makeText(this, "当前不是目录", Toast.LENGTH_SHORT).show();
				openFile(touchedItemName);
			}
			mPrePos = position;
		}
	}
	
	private void openFile(String fileName)
	{
		File   file     = new File(mFileMag.getCurrentDir() + "/" + fileName);
		String item_ext = null;
		
		try {
    		item_ext = fileName.substring(fileName.lastIndexOf("."), fileName.length());
    		
    	} catch(IndexOutOfBoundsException e) {	
    		item_ext = ""; 
    	}
		  	
    	/*music file selected--add more audio formats*/
    	if (item_ext.equalsIgnoreCase(".mp3") || 
			item_ext.equalsIgnoreCase(".m4a")||
			item_ext.equalsIgnoreCase(".mp4")) 
    	{
    		
    		Intent i = new Intent();
			i.setAction(android.content.Intent.ACTION_VIEW);
			i.setDataAndType(Uri.fromFile(file), "audio/*");
			startActivity(i);    		
    	}
    	/*photo file selected*/
    	else if(item_ext.equalsIgnoreCase(".jpeg") || 
    			item_ext.equalsIgnoreCase(".jpg")  ||
    			item_ext.equalsIgnoreCase(".png")  ||
    			item_ext.equalsIgnoreCase(".gif")  || 
    			item_ext.equalsIgnoreCase(".tiff")) 
    	{
 			    		
		
    		Intent picIntent = new Intent();
    		picIntent.setAction(android.content.Intent.ACTION_VIEW);
    		picIntent.setDataAndType(Uri.fromFile(file), "image/*");
    		startActivity(picIntent);
	    }/*video file selected--add more video formats*/
    	else if(item_ext.equalsIgnoreCase(".m4v") || 
    			item_ext.equalsIgnoreCase(".3gp") ||
    			item_ext.equalsIgnoreCase(".wmv") || 
    			item_ext.equalsIgnoreCase(".mp4") || 
    			item_ext.equalsIgnoreCase(".ogg") ||
    			item_ext.equalsIgnoreCase(".wav")) 
    	{
			Intent movieIntent = new Intent();
    		movieIntent.setAction(android.content.Intent.ACTION_VIEW);
    		movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
    		startActivity(movieIntent);
    	}/*zip file */
    	else if(item_ext.equalsIgnoreCase(".zip")) {
    		
    	}
    	/* gzip files, this will be implemented later */
    	else if(item_ext.equalsIgnoreCase(".gzip") ||
    			item_ext.equalsIgnoreCase(".gz")) 
    	{

    	}
    	/*pdf file selected*/
    	else if(item_ext.equalsIgnoreCase(".pdf")) 
    	{
    		
    		Intent pdfIntent = new Intent();
    		pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
    		pdfIntent.setDataAndType(Uri.fromFile(file), 
    								 "application/pdf");
    		
    		try 
    		{
    			startActivity(pdfIntent);
    		} 
    		catch (ActivityNotFoundException e) 
    		{
    			Toast.makeText(this, "Sorry, couldn't find a pdf viewer", 
						Toast.LENGTH_SHORT).show();
    		}
		}
    	/*Android application file*/
    	else if(item_ext.equalsIgnoreCase(".apk"))
    	{
    		Intent apkIntent = new Intent();
			apkIntent.setAction(android.content.Intent.ACTION_VIEW);
			apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			startActivity(apkIntent);
		}
    	/* HTML file */
    	else if(item_ext.equalsIgnoreCase(".html")) 
    	{
			Intent htmlIntent = new Intent();
			htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
			htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");
			
			try 
			{
				startActivity(htmlIntent);
			}
			catch(ActivityNotFoundException e) 
			{
				Toast.makeText(this, "Sorry, couldn't find a HTML viewer", 
									Toast.LENGTH_SHORT).show();
			}
    	}
    	/* text file*/
    	else if(item_ext.equalsIgnoreCase(".txt")) {
    		
			Intent txtIntent = new Intent();
			txtIntent.setAction(android.content.Intent.ACTION_VIEW);
			txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");
			
			try 
			{
				startActivity(txtIntent);
			}
			catch(ActivityNotFoundException e) 
			{
				txtIntent.setType("text/*");
				startActivity(txtIntent);
			}
    	}
    	/* generic intent */
    	else 
    	{
    		Intent generic = new Intent();
    		generic.setAction(android.content.Intent.ACTION_VIEW);
    		generic.setDataAndType(Uri.fromFile(file), "text/plain");
    		
    		try 
    		{
    			startActivity(generic);
    		}
    		catch(ActivityNotFoundException e) 
    		{
    			Toast.makeText(this, "Sorry, couldn't find anything " +
    						   "to open " + file.getName(), 
    						   Toast.LENGTH_SHORT).show();
    		}
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
		String currentDir = mFileMag.getCurrentDir();
		mRootPath = mRootPath.replace("//", "/");
		if(currentDir.equals("/") || currentDir.equals(mRootPath))
		{
		    exit();	
		}
		else
		{
			mChildren = mFileMag.getPreviousDir();
			buildItemList();
			mListItemAdapter.notifyDataSetChanged();
			mListView.setSelection(mPrePos);
		}    	
    }
    
	boolean isExitThisActivity;
	
	Handler mBackPressedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExitThisActivity = false;
        }
    };
	
	private void exit()
	{
        if (!isExitThisActivity) 
        {
        	isExitThisActivity = true;
            Toast.makeText(getApplicationContext(), "再按一次返回", Toast.LENGTH_SHORT).show();
            mBackPressedHandler.sendEmptyMessageDelayed(0, 1000);
        }
        else 
        {
            finish();
        }
    }
}
