package com.phoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.OutputStream;

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

    public static void execShellCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
