package com.phoneinfo;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by xiejingbao on 2017/12/15.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String [] packageNames = {"com.mp.b","com.phoneinfo","com.look.xy","com.hand.read"};
    private static List<String> list = new ArrayList<>();
    public MyAccessibilityService() {
    }

    /**
     * AccessibilityService 这个服务可以关联很多属性，这些属性 一般可以通过代码在这个方法里进行设置，
     * 我这里偷懒 把这些设置属性的流程用xml 写好 放在manifest里，如果你们要使用的时候需要区分版本号
     * 做兼容，在老的版本里是无法通过xml进行引用的 只能在这个方法里手写那些属性 一定要注意.
     * 同时你的业务如果很复杂比如需要初始化广播啊之类的工作 都可以在这个方法里写。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogManager.e("connect--------");
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new Message(""));
        list = Arrays.asList(packageNames);

    }

    /**
     * 当你这个服务正常开启的时候，就可以监听事件了，当然监听什么事件，监听到什么程度 都是由给这个服务的属性来决定的，
     * 我的那些属性写在xml里了。
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        /**
         * 事件是分很多种的，我这里是最简单的那种，只演示核心功能，如果要做成业务上线 这里推荐一个方法可以快速理解这里的type属性。
         * 把这个type的int 值取出来 并转成16进制，然后去AccessibilityEvent 源码里find。顺便看注释 ，这样是迅速理解type类型的方法
         */
        LogManager.e("event.getPackageName"+event.getPackageName());
        LogManager.e("event.getClassName"+event.getClassName());
        LogManager.e("event.source"+event.getSource());
        final int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                click();
                //这个地方没什么好说的 你就理解成 找到当前界面 包含有安装 这个关键词的 所有节点就可以了。返回这些节点的list
                //注意这里的find 其实是contains的意思，比如你界面上有2个节点，一个节点内容是安装1 一个节点内容是安装2，那这2个节点是都会返回过来的
                //除了有根据Text找节点的方法 还有根据Id找节点的方法。考虑到众多手机rom都不一样，这里需要大家多测试一下，有的rom packageInstall
                //定制的比较深入，可能和官方rom里差的很远 这里就要做冗余处理，可以告诉大家一个小技巧 你就把这些rom的 安装器打开 然后
                //通过ddms里 看view结构的按钮 直接进去看就行了，可以直接看到那个界面属于哪个包名，也可以看到你要捕获的那个按钮的id是什么 很方便！
                break;
            default:
                break;
        }
    }

    private boolean isPackage(AccessibilityEvent event){
        if(event==null)
            return false;
        if(null!=event.getSource()){
            CharSequence p = event.getSource().getPackageName();
            if(!TextUtils.isEmpty(p)&&list.contains(p.toString())){
                return true;
            }

        }
        return false;
    }

    private boolean isPackageBygetRootInActiveWindow(){
        if(getRootInActiveWindow()==null)
            return false;
        if(null!=getRootInActiveWindow().getPackageName()){
            if(list.contains(getRootInActiveWindow().getPackageName().toString())){
                return true;
            }

        }
        return false;
    }
    private void click(){

//        Resources resources = this.getResources();
//        final DisplayMetrics dm = resources.getDisplayMetrics();
//        final int width = dm.widthPixels;
//        final int height = dm.heightPixels;
            Observable.timer(10, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    if(isPackageBygetRootInActiveWindow()){
                        LogManager.e("MainActivity延迟执行");
//                        Utils.execShellCmd("input tap " + width / 2 + " " + height / 2);
//                        Toast.makeText(getApplicationContext(),"click",Toast.LENGTH_SHORT).show();
                        performClick();
                    }

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    LogManager.e("su---error");
                    click();
                }
            });
    }

    public boolean  findByText(String str){
        boolean isFind = false;
        List<AccessibilityNodeInfo> list = this.getRootInActiveWindow().findAccessibilityNodeInfosByText(str);

        if (null!=list&&list.size()>0){
            for (AccessibilityNodeInfo info : list) {
                LogManager.e("info"+info.getText()+"");
                if (info.getText().toString().equals(str))
                {
                    isFind = true;
                }
            }
        }else{
            LogManager.e(str+"：未找到");
             isFind =false;
        }
        return isFind;
    }

    public boolean  findByContainsText(String str){
        boolean isFind = false;
        List<AccessibilityNodeInfo> list = this.getRootInActiveWindow().findAccessibilityNodeInfosByText(str);

        if (null!=list&&list.size()>0){
            for (AccessibilityNodeInfo info : list) {
                LogManager.e("info"+info.getText()+"");
                if (info.getText().toString().contains(str))
                {
                    isFind = true;
                }
            }
        }else{
            LogManager.e(str+"：未找到");
            isFind =false;
        }
        return isFind;
    }

  public void   findByTextAndClick(AccessibilityNodeInfo nodeInfo,String str){

      List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
      if (null!=list&&list.size()>0){
          for (AccessibilityNodeInfo info : list) {
              LogManager.e("info"+info.getText()+"");
              if (info.getText().toString().equals(str))
              {
                  //找到你的节点以后 就直接点击他就行了
                  info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
              }
          }
      }else
          LogManager.e(str+"：未找到");
    }
    @Override
    public void onInterrupt() {
        LogManager.e("onInterrupt--------");
        list.clear();
        EventBus.getDefault().post(new Message("关闭成功"));
        EventBus.getDefault().unregister(this);

    }


    private void excu(){
        Observable.interval(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                String packageName  = getRootInActiveWindow().getPackageName()+"";
                String className = getRootInActiveWindow().getClassName()+"";


                LogManager.e("onMessageEvent--packageName:"+packageName);
                LogManager.e("onMessageEvent--className:"+className);
                click();
                if(packageName.equals("com.android.phone")){
                    if(findByContainsText("发送")
                            &&findByContainsText("取消")
                           ){
                        performClickXiaomi("取消");
                    }
                }
                if(findByText("允许")&&findByText("拒绝")){
                    LogManager.e("拒绝");
                    performClickXiaomi("拒绝");
                }

                else if(findByText("打开")&&findByText("完成")){
                    LogManager.e("完成");
                    performClickXiaomi("完成");
                }

                else if(findByText("安装")&&findByText("取消")){
                    performClickXiaomi("下一步");
                    performClickXiaomi("安装");
                    performClickXiaomi("继续安装");
                    performClickXiaomi("完成");
                }else {
                    performClickXiaomi("下一步");
                    performClickXiaomi("继续安装");
                    performClickXiaomi("完成");
                }

                if("com.android.packageinstaller".equals(packageName)
                        ||"com.mini.packageinstaller".equals(packageName)
                        ||"com.miui.packageinstaller".equals(packageName)){

//                    if(className.contains("UninstallerActivity")){
                    if(findByContainsText("卸载")
                            ||findByContainsText("Uninstall")||findByContainsText("uninstall")
                            ){
                        performClickXiaomi("确定");
                        performClickXiaomi("卸载");
                        performClickXiaomi("OK");
                        return;
                    }
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogManager.e("onMessageEvent---error"+throwable.getMessage());
                excu();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final Message msg) {
        excu();
       }

       private void youmi(){
           touc(findNodeInfosByText(this.getRootInActiveWindow(),"youmi"));
       }

       private void touc(AccessibilityNodeInfo nodeInfo){
           if(null==nodeInfo){
               LogManager.e("获取失败");
               return;
           }
//           if (nodeInfo.isClickable()) {
               nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
               LogManager.e("点击了---click");
//           }

       }
    //执行点击
    public  void performClick() {

        LogManager.e("点击执行");

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();

        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo);
          touc(targetNode);
    }

    //执行点击
    public  void performClickXiaomi(String msg) {
            LogManager.e("performClickXiaomi点击执行:"+msg);
            AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
            findByTextAndClick(nodeInfo,msg);


    }


    //通过id查找
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//          LogManager.e("view-----count---"+ nodeInfo.getChildCount());
            for (int i = 0; i <  nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo nodeIn = nodeInfo.getChild(i);
                LogManager.e("PackageName"+nodeIn.getPackageName());
                if(list.contains(nodeIn.getPackageName().toString())){
                    if(list.get(3).equals(nodeIn.getPackageName().toString())){
                        LogManager.i("eeei:"+i);
                        AccessibilityNodeInfo accessibilityNodeInfo = nodeInfo.getChild(i);
                        LogManager.i("eeetext:"+accessibilityNodeInfo.getText());
                        LogManager.i("eeegetPackageName:"+accessibilityNodeInfo.getPackageName());
                        LogManager.i("eeegetClassName:"+accessibilityNodeInfo.getClassName());
                        LogManager.i("eeegetContentDescriptione:"+accessibilityNodeInfo.getContentDescription());
                        if("android.widget.LinearLayout".equals(nodeInfo.getChild(i).getClassName())
                               ){
                            LogManager.i(nodeIn.getPackageName().toString()+":---->点击");
                            return nodeInfo.getChild(i);
                        }
                    }
                    if("android.widget.ImageView".equals(nodeInfo.getChild(i).getClassName())){

                        LogManager.e("获取到imageView:getViewIdResourceName"+nodeIn.getViewIdResourceName());
                        LogManager.e("获取到imageView:getText"+nodeIn.getText());
                        LogManager.e("获取到imageView:getContentDescription"+nodeIn.getContentDescription());
                        LogManager.e("PackageName"+nodeInfo.getChild(i).getPackageName());
                        return nodeInfo.getChild(i);
                    }
                }


            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          LogManager.e("view-----count---"+ nodeInfo.getChildCount());
            for (int i = 0; i <  nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo nodeIn = nodeInfo.getChild(i);
                LogManager.e("PackageName"+nodeIn.getPackageName());
                if("com.look.xy".equals(nodeIn.getPackageName())){
                    if("youmi".equals(nodeIn.getContentDescription())){

                        LogManager.e("youmi:getViewIdResourceName"+nodeIn.getViewIdResourceName());
                        LogManager.e("youmi:getText"+nodeIn.getText());
                        LogManager.e("youmi:getContentDescription"+nodeIn.getContentDescription());
                        LogManager.e("PackageName"+nodeInfo.getChild(i).getPackageName());
                        return nodeInfo.getChild(i);
                    }
                }


            }
        }
        return null;
    }

}
