package com.xyz2k8.overdisk;

import java.io.DataOutputStream;

public class RootService {	
	public static boolean isRoot()
	{
	    try
	    {
	    	Process process  = Runtime.getRuntime().exec("su");
	        process.getOutputStream().write("exit\n".getBytes());
	        process.getOutputStream().flush();
	        int i = process.waitFor();
	        if(0 == i)
	        {
	            process = Runtime.getRuntime().exec("su");
	            return true;
	        }
	    }
	    catch (Exception e)
	    {
	        return false;
	    }
	    
	    return false;     
	}  

	protected static boolean haveRoot() 
	{
		int i = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
		if (i != -1) 
		{
		    return true;
		}
		return false;
	}
	
	protected static int execRootCmdSilent(String paramString) 
	{
        try 
        {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int result = localProcess.exitValue();
            return (Integer) result;
        } 
        catch (Exception localException) 
        {
            localException.printStackTrace();
            return -1;
        }
    }
	
	public static synchronized boolean getRootAuth()  
	{  
	    Process process = null;  
	    DataOutputStream os = null;  
	    try  
	    {  
	        process = Runtime.getRuntime().exec("su");  
	        os = new DataOutputStream(process.getOutputStream());  
	        os.writeBytes("exit\n");  
	        os.flush();  
	        int exitValue = process.waitFor();  
	        if (exitValue == 0)  
	        {  
	            return true;  
	        } 
	        else  
	        {  
	            return false;  
	        }  
	    } 
	    catch (Exception e)  
	    {  
	        //Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());  
	        return false;  
	    } 
	    finally  
	    {  
	        try  
	        {  
	            if (os != null)  
	            {  
	                os.close();  
	            }  
	            process.destroy();  
	        } 
	        catch (Exception e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }  
	}
}
