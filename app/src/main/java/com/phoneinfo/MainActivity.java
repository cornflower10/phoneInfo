package com.phoneinfo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    @BindView(R.id.ed_index)
    AppCompatEditText edIndex;
    @BindView(R.id.ed_install)
    AppCompatEditText edInstall;
    @BindView(R.id.ed_main)
    AppCompatEditText edMain;
    @BindView(R.id.open)
    Button open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//
//        execShellCmd("getevent -p");
//        execShellCmd("sendevent /dev/input/event0 1 158 1");
//        execShellCmd("sendevent /dev/input/event0 1 158 0");
//        execShellCmd("input keyevent 3");//home
//        execShellCmd("input text  'helloworld!' ");
//        execShellCmd("input tap 168 252");
//        execShellCmd("input swipe 100 250 200 280");

        StringBuffer sb = new StringBuffer();
        String num = null;
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager)
                    getSystemService(Context.TELEPHONY_SERVICE);
            num = mTelephonyMgr.getLine1Number();

        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.append("手机号码=" + (TextUtils.isEmpty(num) ? "空" : num) + "\n");
//        /**
//         * 获取屏幕信息
//         */
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//
//        sb.append("屏幕分辨率=" + dm.widthPixels + "*" + dm.heightPixels + "\n");
//
//        getDeviceId(this, sb);
//
//        Field[] fields = Build.class.getDeclaredFields();
//        for (Field field : fields) {
//            try {
//                field.setAccessible(true);
//                sb.append(field.getName() + "=" + field.get(null).toString() + "\n");
//            } catch (Exception e) {
//                Log.e(TAG, "an error occured when collect crash info", e);
//            }
//        }

        TextView textView = (TextView) findViewById(R.id.tv);
        textView.setText(sb.toString());
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
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

    private void jumpHelp() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private static StringBuffer getDeviceId(Context context, StringBuffer sb) {

        String deviceId = null;
        try {
            deviceId = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            sb.append("设备编号=" + deviceId + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (deviceId == null && Build.VERSION.SDK_INT > 9) {
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            sb.append("设备编号=" + deviceId + "\n");
            if (deviceId == null) {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null
                        && networkInfo.isAvailable()
                        && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wm = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    deviceId = wm.getConnectionInfo().getMacAddress();
                    sb.append("MAC地址=" + deviceId + "\n");
                } else {
                    sb.append("UUID=" + deviceId + "\n");
                    deviceId = UUID.randomUUID().toString();
                }
            }
        }

        if (deviceId != null && deviceId.length() < 28) {
            int len = 28 - deviceId.length();
            for (int i = 0; i < len; i++) {
                deviceId = "0" + deviceId;
            }
        }

        return sb;
    }

//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(Message msg) {
//       open.setText(msg.getMsg());
//
//    }

    @OnClick({R.id.bt_in_save, R.id.bt_install_save, R.id.bt_main_save, R.id.bt_ex, R.id.open})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_in_save:
                break;
            case R.id.bt_install_save:
                break;
            case R.id.bt_main_save:
                break;
            case R.id.bt_ex:
                break;
            case R.id.open:
                jumpHelp();
                break;
        }
    }

    @OnClick(R.id.uninstall)
    public void onViewClicked() {
        Utils.uninstall(this);
    }
}
