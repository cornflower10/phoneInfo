package com.phoneinfo;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by xiejingbao on 2017/12/24.
 */

public class Message {
    private String msg;
    private AccessibilityEvent event;

    public AccessibilityEvent getEvent() {
        return event;
    }

    public void setEvent(AccessibilityEvent event) {
        this.event = event;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Message(String msg, AccessibilityEvent event) {
        this.msg = msg;
        this.event = event;
    }

    public Message(String msg) {
        this.msg = msg;
    }
}
