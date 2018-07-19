package com.phoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiejingbao on 2017/12/15.
 */

public class Utils {
    /**
     * activity跳转带finish
     * @param context
     * @param targetClass
     */
    public static void jumpWithFinish(Context context, Class targetClass) {
        Intent intent = new Intent(context, targetClass);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    public static void   execShellCmd(String cmd) throws IOException {

            Process process = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();

    }

    /**
     * 判断手机是否ROOT
     */
    public static boolean isRoot() {

        boolean root = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }

        } catch (Exception e) {
        }

        return root;
    }

    /**
     * execCommand("pm","uninstall", "packageName");
     * @param command
     * @return
     */
    //pm命令可以通过adb在shell中执行，同样，我们可以通过代码来执行
    public static String execCommand(String... command) {
        if(!isRoot())
            return "未root";
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        String result = "";
        try {
            process = new ProcessBuilder().command(command).start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            result = new String(baos.toByteArray());
            if (inIs != null)
                inIs.close();
            if (errIs != null)
                errIs.close();
            process.destroy();
        } catch (IOException e) {

            result = e.getMessage();
        }
        LogManager.e("result:"+result);
        return result;
    }


    public static List<String> getPakageName(Context context){
        ArrayList<String> appList = new ArrayList<String>();

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packages.size();i++) {

            PackageInfo packageInfo = packages.get(i);
            appList.add(packageInfo.packageName);

        }
        return appList;
    }

    public static void uninstall(Context context){
       List<String> list = getPakageName(context);
        if(null==list||list.size()<=0){
            return;
        }
        for (String name:list
             ) {
            execCommand("pm","uninstall", name);
            LogManager.e("卸载："+name);
        }
    }

}
