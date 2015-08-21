package com.xyz2k8.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import com.xyz2k8.overdisk.StorageInfo;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

/**
 * SD卡相关的辅助类
 * 
 * @author zhy
 * 
 */
public class SDCardUtils
{
	private SDCardUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable()
	{
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}
	
	/**
	 * 获取SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 获取SD卡的剩余容量 单位byte
	 * 
	 * @return
	 */
	public static long getSDCardAllSize()
	{
		if (isSDCardEnable())
		{
			StatFs stat = new StatFs(getSDCardPath());
			// 获取空闲的数据块的数量
			long availableBlocks = (long) stat.getFreeBlocks() - 4;
			// 获取单个数据块的大小（byte）
			long freeBlocks = stat.getAvailableBlocks();
			return freeBlocks * availableBlocks;
		}
		return 0;
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 * 
	 * @param filePath
	 * @return 容量字节 SDCard可用空间，内部存储可用空间
	 */
	public static long getFreeBytes(String filePath)
	{
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(getSDCardPath()))
		{
			filePath = getSDCardPath();
		} else
		{// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = Environment.getDataDirectory().getAbsolutePath();
		}
		StatFs stat = new StatFs(filePath);
		long availableBlocks = (long) stat.getAvailableBlocks() - 4;
		return stat.getBlockSize() * availableBlocks;
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath()
	{
		return Environment.getRootDirectory().getAbsolutePath();
	}
	
	public static StorageInfo getSDCardPathAndSizeInfo(Context context)
	{
				
		StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);  
		// 获取sdcard的路径：外置和内置  
		try 
		{
			StorageInfo sdCardInfo = new StorageInfo();
			sdCardInfo.mPath.clear();
			sdCardInfo.mTotalSize = 0;
			sdCardInfo.mUsedSize = 0;
			String[] paths = (String[])(sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null));
			for(int i = 0; i < paths.length; i++)
			{
				String status = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, paths[i]);
		        if(status.equals(android.os.Environment.MEDIA_MOUNTED))
		        {
		        	sdCardInfo.mPath.add(paths[i]);
		        	StatFs stat = new StatFs(paths[i]);
		        	long blockSize = (long)stat.getBlockSize();
		        	int blockCount = stat.getBlockCount();					
					long freeBlocks = stat.getFreeBlocks();
					
					sdCardInfo.mTotalSize += blockSize * blockCount;
					sdCardInfo.mUsedSize += blockSize * (blockCount - freeBlocks);
		        }
			}			
			return sdCardInfo;			
	        	
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public static StorageInfo getRootStorageSize()
	{
		StorageInfo sdCardInfo = new StorageInfo();
		sdCardInfo.mPath.clear();
		sdCardInfo.mTotalSize = 0;
		sdCardInfo.mUsedSize = 0;
		   
        File root = Environment.getRootDirectory();  
        StatFs sf = new StatFs(root.getPath());  
        long blockSize = sf.getBlockSize();  
        long blockCount = sf.getBlockCount();  
        long availCount = sf.getFreeBlocks();
        
        sdCardInfo.mTotalSize = blockSize * blockCount;
        sdCardInfo.mUsedSize = blockSize * availCount;
        return sdCardInfo;
	}
}
