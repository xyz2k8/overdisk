package com.xyz2k8.overdisk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;
import java.io.File;
import java.io.IOException;

import android.util.Log;

public class FileManager {
	private long mDirSize = 0;
	private Stack<String> mPathStack;
	private ArrayList<String> mDirContent;
	
	public FileManager() {
		mDirContent = new ArrayList<String>();
		mPathStack = new Stack<String>();
		
		mPathStack.push("/");
		mPathStack.push(mPathStack.peek() + "sdcard");
	}
	
	public String getCurrentDir() {
		return mPathStack.peek();
	}
	
	public ArrayList<String> setHomeDir(String name) {
		//This will eventually be placed as a settings item
		mPathStack.clear();
		name = name.replace("//", "/");
		mPathStack.push(name);
		
		return populate_list();
	}
	
	public ArrayList<String> getPreviousDir() {
		int size = mPathStack.size();
		
		if (size >= 2)
			mPathStack.pop();
		
		else if(size == 0)
			mPathStack.push("/");
		
		return populate_list();
	}
	
	public ArrayList<String> getNextDir(String path, boolean isFullPath) {
		int size = mPathStack.size();
		
		if(!path.equals(mPathStack.peek()) && !isFullPath) {
			if(size == 1)
				mPathStack.push("/" + path);
			else
				mPathStack.push(mPathStack.peek() + "/" + path);
		}
		
		else if(!path.equals(mPathStack.peek()) && isFullPath) {
			mPathStack.push(path);
		}
		
		return populate_list();
	}
	
	public boolean isFile(String name) {
		File file = new File(name);
		return !file.isDirectory();
	}
	
	public boolean isDirectory(String name) {
		return new File(mPathStack.peek() + "/" + name).isDirectory();
	}
	
	public void clearDirSize()
	{
		mDirSize = 0;
	}
		
	public long getDirSize(String path) {
		
		get_dir_size(new File(path));
	
		return mDirSize;
	}
	
    public long getFileSize(String path) {
	
		File file = new File(path);
		
		if(file.isFile() && file.canRead())
		{
			return file.length();
		}
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	private final Comparator size = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			String dir = mPathStack.peek();
			Long first = new File(dir + "/" + arg0).length();
			Long second = new File(dir + "/" + arg1).length();
			
			return second.compareTo(first);
		}
	};
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> populate_list() {
		
		if(!mDirContent.isEmpty())
			mDirContent.clear();
		
		File file = new File(mPathStack.peek());
		
		if(file.exists() && file.canRead()) {
			String[] list = file.list();
			int len = list.length;
			
			/* add files/folder to arraylist depending on hidden status */
			for (int i = 0; i < len; i++) 
			{
				mDirContent.add(list[i]);			
			}
			
			/* sort the arraylist that was made from above for loop */
			int index = 0;
			Object[] size_ar = mDirContent.toArray();
			String dir = mPathStack.peek();
			
			Arrays.sort(size_ar, size);
			
			mDirContent.clear();
			for (Object a : size_ar) {
				if(new File(dir + "/" + (String)a).isDirectory())
					mDirContent.add(index++, (String)a);
				else
					mDirContent.add((String)a);
			}
				
		} 
		else 
		{
			//mDirContent.add("Emtpy");
		}
		
		mDirContent.add(0, "..");
		
		return mDirContent;
	}
	
	/*
	 * 
	 * @param path
	 */
	private void get_dir_size(File path) {	
		
		File[] list = path.listFiles();
		int len;
		
		if(list != null) {
			len = list.length;
			
			for (int i = 0; i < len; i++) {
				try {
					if(list[i].isFile() && list[i].canRead()) {
						mDirSize += list[i].length();
	
					} else if(list[i].isDirectory() && list[i].canRead() && !isSymlink(list[i])) { 
						get_dir_size(list[i]);
					}
				} catch(IOException e) {
					Log.e("IOException", e.getMessage());
				}
			}
		}
	}
	
	// Inspired by org.apache.commons.io.FileUtils.isSymlink()
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
	
	public int deleteTarget(String path) {
		File target = new File(path);
		
		if(target.exists() && target.isFile() && target.canWrite()) {
			target.delete();
			return 0;
		}
		
		else if(target.exists() && target.isDirectory() && target.canRead()) {
			String[] file_list = target.list();
			
			if(file_list != null && file_list.length == 0) {
				target.delete();
				return 0;
				
			} else if(file_list != null && file_list.length > 0) {
				
				for(int i = 0; i < file_list.length; i++) {
					File temp_f = new File(target.getAbsolutePath() + "/" + file_list[i]);

					if(temp_f.isDirectory())
						deleteTarget(temp_f.getAbsolutePath());
					else if(temp_f.isFile())
						temp_f.delete();
				}
			}
			if(target.exists())
				if(target.delete())
					return 0;
		}	
		return -1;
	}
}

