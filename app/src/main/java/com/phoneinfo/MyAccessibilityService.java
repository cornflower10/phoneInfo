package com.phoneinfo;

import android.accessibilityservice.AccessibilityService;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by xiejingbao on 2017/12/15.
 */

public class MyAccessibilityService extends AccessibilityService {
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
//        EventBus.getDefault().post(new Message("开启成功"));
    }

    /**
     * 当你这个服务正常开启的时候，就可以监听事件了，当然监听什么事件，监听到什么程度 都是由给这个服务的属性来决定的，
     * 我的那些属性写在xml里了。
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        /**
         * 事件是分很多种的，我这里是最简单的那种，只演示核心功能，如果要做成业务上线 这里推荐一个方法可以快速理解这里的type属性。
         * 把这个type的int 值取出来 并转成16进制，然后去AccessibilityEvent 源码里find。顺便看注释 ，这样是迅速理解type类型的方法
         */
        LogManager.e("event.getPackageName"+event.getPackageName());
        LogManager.e("event.getClassName"+event.getClassName());
        LogManager.e("event.source"+event.getSource());
        final int eventType = event.getEventType();
        LogManager.e(eventType+"---eventType---");

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //这个地方没什么好说的 你就理解成 找到当前界面 包含有安装 这个关键词的 所有节点就可以了。返回这些节点的list
                //注意这里的find 其实是contains的意思，比如你界面上有2个节点，一个节点内容是安装1 一个节点内容是安装2，那这2个节点是都会返回过来的
                //除了有根据Text找节点的方法 还有根据Id找节点的方法。考虑到众多手机rom都不一样，这里需要大家多测试一下，有的rom packageInstall
                //定制的比较深入，可能和官方rom里差的很远 这里就要做冗余处理，可以告诉大家一个小技巧 你就把这些rom的 安装器打开 然后
                //通过ddms里 看view结构的按钮 直接进去看就行了，可以直接看到那个界面属于哪个包名，也可以看到你要捕获的那个按钮的id是什么 很方便！
                if("com.android.packageinstaller".equals(event.getPackageName())){
                    findByTextAndClick(event.getSource(),"下一步");
                    findByTextAndClick(event.getSource(),"安装");
                    findByTextAndClick(event.getSource(),"完成");

                }else if("com.look.xy".equals(event.getSource().getPackageName())){
                    if("com.xy.WelcomeActivity".equals(event.getClassName())){
                        Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                LogManager.e("延迟执行");
                                Utils.execShellCmd("input tap 360 360");
                                Utils.execShellCmd("input tap 367 480");
                            }
                        });
                    }

                    Resources resources = this.getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    final int width = dm.widthPixels;
                    final int height = dm.heightPixels;
                    if("com.xy.MainActivity".equals(event.getClassName())){
                        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                LogManager.e("MainActivity延迟执行");
                                Utils.execShellCmd("input tap "+width/2+" "+height/2);
                            }
                        });
                    }

                }
                break;
            default:
                break;
        }
    }

  public void   findByTextAndClick(AccessibilityNodeInfo nodeInfo,String str){
      List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
      if (null!=list){
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
        EventBus.getDefault().post(new Message("关闭成功"));
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Message msg) {
//        if("youmi".equals(msg.getMsg())){
//            LogManager.e("start---youmi");
//            youmi();
//        }else {
//            performClick("");
//        }

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
    public  void performClick(String resourceId) {

        LogManager.e("点击执行");

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();

        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo);
          touc(targetNode);
    }


    //通过id查找
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//          LogManager.e("view-----count---"+ nodeInfo.getChildCount());
            for (int i = 0; i <  nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo nodeIn = nodeInfo.getChild(i);
                LogManager.e("PackageName"+nodeIn.getPackageName());
                if("com.look.xy".equals(nodeIn.getPackageName())){
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
