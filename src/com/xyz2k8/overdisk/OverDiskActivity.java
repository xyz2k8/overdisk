package com.xyz2k8.overdisk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.xyz2k8.overdisk.ActionSheet.OnActionSheetSelected;
import com.xyz2k8.overdisk.R;
import com.xyz2k8.overdisk.RingView.InfoItem;
import com.xyz2k8.overdisk.SelectOpration.OnOprationSelected;
import com.xyz2k8.utils.MyUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverDiskActivity extends Activity implements OnActionSheetSelected, OnOprationSelected, OnCancelListener{
	
	public static final int UPDATE_RING = 1;
	public static final int UPDATE_INFO = 2;
	public static final int DEL_PROGRESS_INFO = 3;
	public static final int BUILD_PROGRESS_INFO = 4;
	public static final int HIDE_PROGRESS_INFO = 5;
	
	BuildTreeInBackground  buildTreeInBackground = null;
	DeleteFileInBackground deleteFileInBackground = null;
	HidingFileInBackground hidingFileInBackground = null;
	
	Node  mRootNode = null;
	RingView     mRingView    = null;
	LinearLayout mInfoView    = null;
	
	public static final int MAX_NODE_NUM = 150000;
	long  mNodeNum = 0;
	
	Queue<Node> mQueue = null;
	
	//private boolean mCanceled = false;
	private boolean mIsRoot   = false;
	
	public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
	            case UPDATE_RING:            	
	            	updateView();
	            	break;
	            case UPDATE_INFO:
	            	updateInfoView((InfoItem[])msg.obj);
	            	break;
	            case BUILD_PROGRESS_INFO:
	            	updateProgressInfo((String)(msg.obj));
	            	break;
	            case DEL_PROGRESS_INFO:
	            	updateProgressInfo((String)(msg.obj));
	            	break;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_over_disk);
		
		ImageButton backBtn = (ImageButton)findViewById(R.id.title_bar_nav_btn);
	    backBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				backToPrevious();
			}
	    });
	    
		mRingView = (RingView)findViewById(R.id.ringView);		
		mInfoView = (LinearLayout)findViewById(R.id.infoView);
		
		layoutInfoView();
		
		Bundle bundle = new Bundle();
	    bundle = this.getIntent().getExtras();
	    
	    mIsRoot = bundle.getBoolean("isRoot");
	      
	    if(null != buildTreeInBackground)
	    {
	    	buildTreeInBackground.cancel(true);
	    }
	    buildTreeInBackground = new BuildTreeInBackground();
	    buildTreeInBackground.execute(bundle.getString("path"));
	    //buildTreeInBackground.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, bundle.getString("path"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.over_disk, menu);
		return true;
	}
	
	private class BuildTreeInBackground extends AsyncTask<String, Void, Long> {
		protected void onPreExecute(){
		
		};
		
		@SuppressLint("ShowToast")
		protected Long doInBackground(String... vals) {
			if(mIsRoot)
			{
				Message message = Message.obtain();
				message.what = BUILD_PROGRESS_INFO;
				message.obj = (Object)("正在请求Root权限，稍后启动文件扫描...");
				mHandler.sendMessage(message);
				
				RootProxy rootProxy = new RootProxy(OverDiskActivity.this);
				if(!rootProxy.doReMountProcess())
				{
					Toast.makeText(OverDiskActivity.this, "请求Root权限失败，无法扫描系统目录。", Toast.LENGTH_SHORT);
					finish();
					return 0l;
				}
			}
			mNodeNum = 0;
			buildNodeTree(vals[0]);
			Message message = Message.obtain();
			message.what = UPDATE_RING;
			
			mHandler.sendMessage(message);	
			return 0l;
		}
		
		protected void onPostExecute(Long result) {
		}
	}
	
	private class DeleteFileInBackground extends AsyncTask<String, Void, Long> {
		//private ProgressDialog dialog;
		
		protected void onPreExecute(){
			//dialog = ProgressDialog.show(OverDiskActivity.this, "", "Deleting Files...", true, false);
		}
		
		protected Long doInBackground(String... vals) {
			if(null != mRingView)
			{
				mNodeNum = 0;
			    mRingView.deleteTouchedNode();
			}
			
			Message message = Message.obtain();
			message.what = UPDATE_RING;
			message.obj = null;
			//通过Handler发布传送消息，handler
			mHandler.sendMessage(message);	
			return 0l;
		}
		
		protected void onPostExecute(Long result) {
			//dialog.cancel();
		}	
	}
	
	private class HidingFileInBackground extends AsyncTask<String, Void, Long> {
		//private ProgressDialog dialog;
		
		protected void onPreExecute(){
			//dialog = ProgressDialog.show(OverDiskActivity.this, "", "Hiding Files...", true, false);
		}
		
		protected Long doInBackground(String... vals) {
			if(null != mRingView)
			{
				mNodeNum = 0;
			    mRingView.hideTouchedNode();
			}
			
			Message message = Message.obtain();
			message.what = UPDATE_RING;
			message.obj = null;
			//通过Handler发布传送消息，handler
			mHandler.sendMessage(message);	
			return 0l;
		}
		
		protected void onPostExecute(Long result) {
			//dialog.cancel();
		}	
	}
	
	private void layoutInfoView()
	{
		WindowManager wm = this.getWindowManager();
		 
	    int height = wm.getDefaultDisplay().getHeight();
	    
	    int b_height = (int)(0.275*height);
	     
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mInfoView.getLayoutParams();  
		linearParams.height = b_height;//2*(height - width)/3;
		
		mInfoView.setPadding(0,linearParams.height/10,0,linearParams.height/10);
		mInfoView.setLayoutParams(linearParams);  
		mInfoView.setBackgroundColor(0xff0597d2);		
	}
	
	public void updateInfoView(InfoItem[] infoItems)
	{
		if(null == mInfoView || null == infoItems)
		{
			return;
		}
		
		TextView file_name_lab = (TextView) mInfoView.findViewById(R.id.info_filename_lab);
		TextView file_name     = (TextView) mInfoView.findViewById(R.id.info_filename);
		
		TextView file_size_lab = (TextView) mInfoView.findViewById(R.id.info_filesize_lab);
		TextView file_size     = (TextView) mInfoView.findViewById(R.id.info_filesize);
		
		TextView file_num_lab  = (TextView) mInfoView.findViewById(R.id.info_filenum_lab);
		TextView file_num      = (TextView) mInfoView.findViewById(R.id.info_filenum);
		
		TextView file_full_lab = (TextView) mInfoView.findViewById(R.id.info_filefullpath_lab);
		TextView file_full     = (TextView) mInfoView.findViewById(R.id.info_filefullpath);
		
		
		if(null == infoItems || infoItems.length < 4)
		{
			return;
		}
		file_name_lab.setText(infoItems[0].mItemName);
		file_name.setText(infoItems[0].mItemContent);
		
		file_size_lab.setText(infoItems[1].mItemName);
		file_size.setText(infoItems[1].mItemContent);
		
		file_num_lab.setText(infoItems[2].mItemName);
		file_num.setText(infoItems[2].mItemContent);
		
		file_full_lab.setText(infoItems[3].mItemName);
		infoItems[3].mItemContent = infoItems[3].mItemContent.replace("//", "/");
		if(infoItems[3].mItemContent.length() > 30)
		{
			infoItems[3].mItemContent = "...~" + infoItems[3].mItemContent.substring(infoItems[3].mItemContent.length() - 26, infoItems[3].mItemContent.length());			
		}
		file_full.setText(infoItems[3].mItemContent);
	}
	
	public void updateProgressInfo(String path)
	{
		if(null == mInfoView || null == path)
		{
			return;
		}
		
		TextView file_full_lab = (TextView) mInfoView.findViewById(R.id.info_filename_lab);
		TextView file_full     = (TextView) mInfoView.findViewById(R.id.info_filename);
		
		TextView info_filesize_lab = (TextView) mInfoView.findViewById(R.id.info_filesize_lab);
		TextView info_filesize     = (TextView) mInfoView.findViewById(R.id.info_filesize);
		info_filesize_lab.setText("");
		info_filesize.setText("");
		
		TextView info_filenum_lab = (TextView) mInfoView.findViewById(R.id.info_filenum_lab);
		TextView info_filenum     = (TextView) mInfoView.findViewById(R.id.info_filenum);
		info_filenum_lab.setText("");
		info_filenum.setText("");
		
		TextView info_filefullpath_lab = (TextView) mInfoView.findViewById(R.id.info_filefullpath_lab);
		TextView info_filefullpath     = (TextView) mInfoView.findViewById(R.id.info_filefullpath);
		info_filefullpath_lab.setText("");
		info_filefullpath.setText("");
		
		file_full_lab.setText("正在处理  - ");
		
		path = path.replace("//", "/");
		
		if(path.length() > 200)
		{
			path = "...~" + path.substring(path.length() - 196, path.length());			
		}
		file_full.setText(path);
		
		mInfoView.postInvalidate();
	}
	
	private void updateView()
	{
		if(null == mRootNode || null == mRingView)
		{
			return;
		}
		
		mRingView.updateView(mRootNode,this);
	}
	
	private void addSizeToParent(Node child, long size)
	{
		if(null == child || null == child.mParent || 0 == size)
		{
			return;
		}
		
		Node parent = child.mParent;		
		parent.mSize += size;
		addSizeToParent(parent,size);
	}
	
	private void calcSize(Node root)
	{
		if(null == root || null == mQueue)
		{
			return;
		}
		
		for(Node node : mQueue )
		{
			addSizeToParent(node, node.mSize);
		}
	}
	
	private void buildNodeTree(String fullPath)
	{	
		if(null == fullPath)
		{
			return;
		}
		
		if(null == mQueue)
		{
			mQueue = new LinkedList<Node> ();
		}
		else
		{
			mQueue.clear();
		}
		
		if(null != mRootNode)
		{
			mRootNode = null;
		}
		
		mNodeNum = 0;
		
		try
		{
			mRootNode = new Node(fullPath);
			mNodeNum++;
			buildNode(mRootNode);
			calcSize(mRootNode);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private boolean isShouldIgnored(String shortName)
	{
		if(null == shortName)
		{
			return true;
		}
		
		if(shortName.equals("storage"))
		{
			return true;
		}
		
		if(shortName.equals("sdcard"))
		{
			return true;
		}
		
		if(shortName.equals("mnt"))
		{
			return true;
		}
		
		if(shortName.equals("proc"))
		{
			return true;
		}
		
		if(shortName.equals("dev"))
		{
			return true;
		}
		
		if(shortName.equals("sys"))
		{
			return true;
		}
		
		if(shortName.equals("etc"))
		{
			return true;
		}
		
		if(shortName.equals("sbin"))
		{
			return true;
		}
		
		if(shortName.equals("lost+found"))
		{
			return true;
		}
		
		if(shortName.equals("firmware"))
		{
			return true;
		}
		
		if(shortName.equals("config"))
		{
			return true;
		}
		
		if(shortName.equals("cache"))
		{
			return true;
		}
		
		return false;
	}
	
	private static boolean isSymlink(File file) throws IOException {
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
	}
	
	private void buildNode(Node parent)
	{
		if(null == parent )
		{
			return;
		}
		
		if(mNodeNum%50 == 0)
		{
			Message message = Message.obtain();
			message.what = BUILD_PROGRESS_INFO;
			message.obj = (Object)(parent.mFullPath);
			mHandler.sendMessage(message);  
		}
		
		File file = new File(parent.mFullPath);
		try 
		{
			if(isSymlink(file))
			{
				return;
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		if(file.isFile())
		{
			parent.mIsDir = false;
			parent.mSize = file.length();
			mQueue.add(parent);
			return;
		}
		
		if(file.exists() && file.canRead())
		{
			String[] list = file.list();
			if(null == list || list.length == 0)
			{
				return;
			}
			
			parent.mFilesNum = list.length;
			ArrayList<Node> children = new ArrayList<Node>();
			parent.mChildren = children;
			
			/* add files/folder to arraylist depending on hidden status */
			for (int i = 0; i < list.length; i++) 
			{
				if(mNodeNum >= MAX_NODE_NUM)
				{
					return;
				}
				
				if(isShouldIgnored(list[i]))
				{
					continue;
				}
				
				Node node = new Node(parent.mFullPath + "/" + list[i]);
				node.mParent = parent;
				children.add(node);
				mNodeNum ++;
				buildNode(node);
			}	
		}		
	}
	
	@Override
	public void onActionSelected(int whichAction, String title) {
		// TODO Auto-generated method stub
		//choice.toString();
		if(ActionSheet.ACTION_YES == whichAction && null != mRingView)			
		{
			if(title.equals("确定删除吗？"))
			{
				if(null != deleteFileInBackground)
			    {
					deleteFileInBackground.cancel(true);
			    }
				deleteFileInBackground = new DeleteFileInBackground();
				deleteFileInBackground.execute("Deleting files");
			}
			else if(title.equals("确定忽略吗？"))
			{
				if(null != hidingFileInBackground)
			    {
					hidingFileInBackground.cancel(true);
			    }
				hidingFileInBackground = new HidingFileInBackground();
				hidingFileInBackground.execute("Hiding files");
			}
			else
			{
				//mRingView.openTouchedNodeInFileBrowser();
			}
		}
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		// TODO Auto-generated method stub
		arg0.toString();
		//finish();
	}

	@Override
	public void onOprationSelected(int whichOprationSelected) {
		// TODO Auto-generated method stub
		if(SelectOpration.OP_DELETE == whichOprationSelected && null != mRingView)			
		{
			String[] info = new String[8];
			
			Node node = mRingView.getCurrentNode();
			if(null == node)
			{
				return;
			}
			
			info[0] = "文件名称 - ";
			info[1] = node.mShortName;
			
			info[2] = "文件大小 - ";
			info[3] = MyUtils.formatSize(node.mSize);
			
			info[4] = "文件数目 - ";
			info[5] = node.mFilesNum + "";
			
			info[6] = "文件路径 - ";
			node.mFullPath = node.mFullPath.replace("//", "/");
			info[7] = node.mFullPath;				
			
			ActionSheet.showActionSheet(this, this, this, "确定删除吗？", info);			
		}
		else if(SelectOpration.OP_HIDE == whichOprationSelected && null != mRingView)			
		{
			String[] info = new String[8];
			
			Node node = mRingView.getCurrentNode();
			if(null == node)
			{
				return;
			}
			
			info[0] = "文件名称 - ";
			info[1] = node.mShortName;
			
			info[2] = "文件大小 - ";
			info[3] = MyUtils.formatSize(node.mSize);
			
			info[4] = "文件数目 - ";
			info[5] = node.mFilesNum + "";
			
			info[6] = "文件路径 - ";
			node.mFullPath = node.mFullPath.replace("//", "/");
			info[7] = node.mFullPath;				
			
			ActionSheet.showActionSheet(this, this, this, "确定忽略吗？", info);	
			//mRingView.hideTouchedNode();
		}
		else if(SelectOpration.OP_OPEN == whichOprationSelected && null != mRingView)			
		{
			mRingView.openTouchedNodeInFileBrowser();
		}
	}
	
    public void backToPrevious() {
		if(null != buildTreeInBackground && AsyncTask.Status.FINISHED != buildTreeInBackground.getStatus())
		{
			buildTreeInBackground.cancel(true);
			buildTreeInBackground = null;
		}
		
		if(null != deleteFileInBackground && AsyncTask.Status.FINISHED != deleteFileInBackground.getStatus())
		{
			deleteFileInBackground.cancel(true);
			deleteFileInBackground = null;
		}
		
		if(null != hidingFileInBackground && AsyncTask.Status.FINISHED != hidingFileInBackground.getStatus())
		{
			hidingFileInBackground.cancel(true);
			hidingFileInBackground = null;
		}
		
	    finish();
    }
    
    @Override
    public void onBackPressed() {
    	exit();
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
