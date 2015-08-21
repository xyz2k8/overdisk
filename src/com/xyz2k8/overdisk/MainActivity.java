package com.xyz2k8.overdisk;

import java.util.ArrayList;
import java.util.List;

import com.xyz2k8.utils.RootUtils;
import com.xyz2k8.utils.SDCardUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	String[] mRingMenuStrs = null ;
	String[] mRingMenuPths = null ;
	long     mTotalSize    = 0;
	long     mUsedSize     = 0;
	LinearLayout mLLayoutPecentPanelView = null;
	PercentPanel  mPercentPanle = null;
	LinearLayout mLLayoutRectMenuView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mLLayoutPecentPanelView = (LinearLayout)findViewById(R.id.percent_panel_view);
		
		mPercentPanle = (PercentPanel)findViewById(R.id.percentPanelView);
		mPercentPanle.setOnRegionClickListener( new PercentPanel.OnRegionClickListener(){
			@Override
			public void onRegionClick(int index) {
			  if(index != -1 &&  index != -2) {
				  
				  //Toast.makeText(getBaseContext(), "隐藏按钮按下" + index, Toast.LENGTH_SHORT).show();
				  if(0 == index)
				  {
					  //startWeChatToScanQRCode();
				  }
			  }
		    }
		});
		
		mLLayoutRectMenuView = (LinearLayout)findViewById(R.id.ring_menu_view);
		
		ImageButton backBtn = (ImageButton)findViewById(R.id.title_bar_nav_btn);
	    backBtn.setVisibility(View.GONE);
		
		initMenuItem();
		layoutPercentPanelView();
		layoutRectMenuView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.over_disk, menu);
		return true;
	}
	
	private void initMenuItem()
	{
		StorageInfo sdCardInfo = SDCardUtils.getSDCardPathAndSizeInfo(this);
		if(null == sdCardInfo || null == sdCardInfo.mPath || 0 == sdCardInfo.mPath.size())
		{	
			mRingMenuStrs = new String[2];
			mRingMenuPths = new String[2];
			mRingMenuStrs[0] = "ROOT";
			mRingMenuPths[0] = "/";
			mRingMenuStrs[1] = "存储卡";
			mRingMenuPths[1] = SDCardUtils.getSDCardPath();
			
			StatFs stat = new StatFs(SDCardUtils.getSDCardPath());
        	long blockSize = (long)stat.getBlockSize();
        	int blockCount = stat.getBlockCount();					
			long freeBlocks = stat.getFreeBlocks();
			
			mTotalSize = blockSize * blockCount;
			mUsedSize = blockSize * freeBlocks;
		}
		else
		{
			mRingMenuStrs = new String[sdCardInfo.mPath.size() + 1];
			mRingMenuPths = new String[sdCardInfo.mPath.size() + 1];
			mRingMenuStrs[0] = "ROOT";
			mRingMenuPths[0] = "/";
			
			for(int loop=0; loop<sdCardInfo.mPath.size(); loop++)
			{
				//String[] temp = sdCardInfo.mPath.get(loop).split("/");
				mRingMenuStrs[loop+1] = "存储卡-"+(loop+1);
				mRingMenuPths[loop+1] = sdCardInfo.mPath.get(loop);
			}
			mTotalSize = sdCardInfo.mTotalSize;
			mUsedSize = sdCardInfo.mUsedSize;
		}
		
		StorageInfo rootInfo = SDCardUtils.getRootStorageSize();
		mTotalSize += rootInfo.mTotalSize;
		mUsedSize += rootInfo.mUsedSize;
		
		mPercentPanle.setInfo(mTotalSize, mUsedSize);
		drawRingMenu();
	}
	
	private void drawRingMenu()
	{
		RectMenu ringMenuView = (RectMenu)findViewById(R.id.ringMenuView);
		 
				 
		final List<RectMenuItem> items =new ArrayList<RectMenuItem>();
		for(int i = 0; i < mRingMenuStrs.length; i++) { 
			RectMenuItem item = new RectMenuItem(mRingMenuStrs[i],i);
			items.add(item);
		}
		ringMenuView.setHasRoot(RootUtils.upgradeRootPermission(this.getPackageCodePath()));
		ringMenuView.setItems(items);
		ringMenuView.setOnPieceClickListener( new RectMenu.OnPieceClickListener(){
			@Override
			public void onPieceClick(int index) {
			  if(index != -1 &&  index != -2) {
				  //Toast.makeText(getBaseContext(), "inex = " + index, Toast.LENGTH_SHORT).show();
				  startProcess(index);
			  }
		    }
		});
	}
	
	private void startProcess(int index)
	{
		if(index >= mRingMenuPths.length)
		{	
			return;
		}
		
		if(mRingMenuPths[index].equals(""))
		{
			Toast.makeText(this, "文件路径非法。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(0 == index)
		{
			startToScanRoot(mRingMenuPths[index]);
		}
		else
		{
			startToScanSDCard(mRingMenuPths[index],false);
		}
	}
	
	private void startToScanSDCard(String path, boolean isRoot)
	{
		Intent intent = new Intent();
      	Bundle bundle = new Bundle();
      	bundle.putString("path", path);
      	//bundle.putString("path", "/sdcard/runningshow");
      	bundle.putBoolean("isRoot", isRoot);
      	
      	intent.setClass(this,OverDiskActivity.class);
      	intent.putExtras(bundle);
      	startActivityForResult(intent, 0);
	}
	
	private boolean hasRootPermission()
	{
		RootProxy rootProxy = new RootProxy(this);
		return rootProxy.getRootAuth();
	}
	
	@SuppressLint("ShowToast")
	private void startToScanRoot(String path)
	{
		if(hasRootPermission())
		{      	
			startToScanSDCard(path,true);
		}
		else
		{
			Toast.makeText(MainActivity.this, "请求Root权限失败，无法扫描系统目录。", Toast.LENGTH_SHORT);
		}
	}
	
	private void layoutPercentPanelView()
	{
		WindowManager wm = this.getWindowManager();
		
	    int height = wm.getDefaultDisplay().getHeight();
	    //int b_height = (int)(0.382*height);
	    int b_height = (int)(0.618*height);
	     
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mLLayoutPecentPanelView.getLayoutParams(); 
		//mLLayoutPecentPanelView.setBackgroundDrawable(getResources().getDrawable(R.drawable.frame_bg_small));
		linearParams.width  = wm.getDefaultDisplay().getWidth();
		linearParams.height = b_height;
		mLLayoutPecentPanelView.setPadding(100,0,100,0);
		mLLayoutPecentPanelView.setLayoutParams(linearParams);
	}
	
	private void layoutRectMenuView()
	{
		WindowManager wm = this.getWindowManager();
		 
	    int width = wm.getDefaultDisplay().getWidth();
	    int height = wm.getDefaultDisplay().getHeight();
	    int b_height = (int)((1 -0.618)*height);
	     
		LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) mLLayoutRectMenuView.getLayoutParams();  
		linearParams.height = b_height;
		linearParams.height = width/2;
		mLLayoutRectMenuView.setPadding(0,0,0,0);
		mLLayoutRectMenuView.setLayoutParams(linearParams);
	}
	
	@Override
    public void onBackPressed() {
		exit();
    }
	
	boolean isExit;
	
	Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }
        
    };
	
	private void exit()
	{
        if (!isExit) 
        {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
        else 
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }
	
	private boolean isAvilible( Context context, String packageName )
    {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
            {
                return true;
            }
        }
        return false;
    }
	
	@SuppressWarnings("unused")
	private void startWeChatToScanQRCode()
	{
		if(isAvilible(MainActivity.this, "com.tencent.mm"))
		{ 
		   try{
		       Intent i = new Intent(); 
		       ComponentName cn = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.qrcode.ShareToQQUI"); 
		       i.setComponent(cn); 
		       startActivityForResult(i, RESULT_OK);
		   }
		   catch(Exception ex)
		   {
			   Toast.makeText(getBaseContext(), "未找到微信扫一扫组件！", Toast.LENGTH_SHORT).show();
			   Intent intent = new Intent(); 
			   ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.plugin.setting.ui.qrcode.GetQRCodeInfoUI");
			   intent.setComponent(cmp);
			   intent.setAction(Intent.ACTION_VIEW);
			   intent.addCategory(Intent.CATEGORY_BROWSABLE);
			   intent.addCategory(Intent.CATEGORY_DEFAULT);
			   
			   //Uri uri = Uri.fromFile("/sdcard/Pictures/Screenshots/IMG_20150813_091000.JPG");
			   Uri uri = Uri.parse("weixin:");//这个可以
			   
			   intent.setData(uri);//ssetDataAndType(uri, type)
			   
			   startActivityForResult(intent, RESULT_OK);
		   }
	    } 
	    //未安装，跳转至market下载该程序 
	    else 
	    { 
	    	Toast.makeText(getBaseContext(), "您手机上未安装微信，感谢支持！", Toast.LENGTH_SHORT).show(); 
	    }
	}
}
