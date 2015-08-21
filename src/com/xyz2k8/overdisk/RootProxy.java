package com.xyz2k8.overdisk;

import com.xyz2k8.utils.RootUtils;

import android.content.Context;

public class RootProxy {
	
	Context mCtx;
	String  mResult;
	String  mBlock;
	
	public RootProxy(Context ctx)
	{
	     mCtx = ctx;
	}
	
//	private boolean getMount(String mountName) 
//    {	
//		if(null == mountName)
//		{
//			return false;
//		}
//		
//		String re = "";
//        re += RootUtils.execRootCmd("mount");  
//        if (re.length() > 10) 
//        {  
//        	mBlock = re.substring(0, re.indexOf(" /" + mountName)); 
//        	if(null != mBlock)
//        	{
//        		mBlock = mBlock.substring(mBlock.lastIndexOf("\n") + 1);
//        		if(null != mBlock)
//        		{
//        			return true;
//        		}
//        	}
//        	 
//        }
//        
//        return false;
//    }
	
//	private boolean moutRW(String mountName) 
//    {  
//		if(null == mountName)
//		{
//			return false;
//		}
//		
//		if(null == mBlock || mBlock.length() <= 0)
//		{
//			return false;
//		}
//		
//        RootUtils.execRootCmd("mount -o remount,rw "+ mBlock +" /" + mountName);
//        return true;
//    }
	
//	private String copyFile(String src, String dst) 
//    {
//		if(null == src || null == dst)
//		{
//			return "src or dst is null";
//		}
//
//        String re = RootUtils.execRootCmd("cp -f " + src + " " + dst);  
//        return re;  
//    } 
	
	public boolean getRootAuth()
	{	
		if (!RootUtils.upgradeRootPermission(mCtx.getPackageCodePath())) 
		{  
	        return false;
	    } 
	
		RootUtils.execRootCmd("chmod 777 -R /");
		//RootUtils.execRootCmd("chmod 777 -R /data");
		
		return true;
	}
	
	public boolean doReMountProcess()
	{
		if (!RootUtils.upgradeRootPermission(mCtx.getPackageCodePath())) 
		{  
	        return false;
	    } 
		
		RootUtils.execRootCmd("chmod 777 /data");
		RootUtils.execRootCmd("chmod 777 -R /data/*");
	
//		if(!getMount("data"))
//		{
//			return true;
//		}
//		
//	    moutRW("data");	
		return true;
	}

}
