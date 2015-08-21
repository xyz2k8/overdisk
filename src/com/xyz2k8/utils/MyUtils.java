package com.xyz2k8.utils;

/**
 * Created by Dacer on 10/8/13.
 */
public class MyUtils {

    private static final int KB = 1024;
	private static final int MG = KB * KB;
	private static final int GB = MG * KB;
	
	public static String formatSize(long size)
	{
		String disPlaySize = "";
        if (size > GB)
        	disPlaySize = String.format("%.2f GB ", (double)size / GB);
		else if (size < GB && size > MG)
			disPlaySize = String.format("%.2f MB ", (double)size / MG);
		else if (size < MG && size > KB)
			disPlaySize = String.format("%.2f KB ", (double)size/ KB);
		else
			disPlaySize = String.format("%.2f bytes ", (double)size);
        
        return disPlaySize;
	}
}
