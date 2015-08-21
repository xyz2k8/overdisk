package com.xyz2k8.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RootUtils {
	
	/* Android应用申请root权限
	     对于已经root过的机器，应用可以直接申请获取root权限，
	     实现对系统文件的操作。基本是使用shell命令进行操作，
	     首先是使用“su”切换到root用户，然后是"chmod 777 " + pkgCodePath修改该应用的读写权限，
	     如果以上两步能够执行通过则Apk就具有了root权限。代码如下：
	*/
	public static boolean upgradeRootPermission(String pkgCodePath) 
	{  
        Process process = null;  
        DataOutputStream os = null;  
        try 
        {  
            String cmd = "chmod 777 " + pkgCodePath;  
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号  
            os = new DataOutputStream(process.getOutputStream());  
            os.writeBytes(cmd + "\n");  
            os.writeBytes("exit\n");  
            os.flush();  
            process.waitFor();
        } 
        catch (Exception e) 
        {  
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
            }  
        }  
        return true;  
    }  
	
	/*
	         获得root权限后就够执行root用户才能执行的shell命令，比如说修改系统文件。
	         因为系统目录默认挂载为只读的，要修改系统文件首先要将该分区挂载分区挂载为可读的；
	         首先执行命令“mount”，目的是找到该分区挂载的位置；然后执行重新挂载的命名mount -o remount,rw "+block+" /system"
	         是对应的挂载路径。挂载为可读写的之后就任你随意蹂躏了。
	 */
	
	public static String execRootCmd(String cmd) 
	{  
        String result = "";  
        DataOutputStream dos = null;  
        DataInputStream dis = null;  
  
        try 
        {  
            Process p = Runtime.getRuntime().exec("su");  
            dos = new DataOutputStream(p.getOutputStream());  
            dis = new DataInputStream(p.getInputStream());  
  
            dos.writeBytes(cmd + "\n");  
            dos.flush();
            dos.writeBytes("ls -l" + "\n");
            dos.flush();
            dos.writeBytes("exit\n");  
            dos.flush();  
            String line = null;  
            while ((line = dis.readLine()) != null) 
            {  
                result += line+"\r\n";  
            }  
            p.waitFor();  
        } 
        catch (Exception e) 
        {  
            e.printStackTrace();  
        } 
        finally 
        {  
            if (dos != null) 
            {  
                try 
                {  
                    dos.close();  
                } 
                catch (IOException e) 
                {  
                    e.printStackTrace();  
                }  
            }  
            if (dis != null) 
            {  
                try 
                {  
                    dis.close();  
                } 
                catch (IOException e) 
                {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return result;  
    }
}
