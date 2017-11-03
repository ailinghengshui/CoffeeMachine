package com.jingye.coffeemac.beans;

/**
 * Created by Hades on 2017/2/21.
 */

public class DebugItem {
    private int id;
    private String title;
    private int resId;
    private String hint;

    public DebugItem(int id, int resId,String title ) {
        this.id = id;
        this.title = title;
        this.resId = resId;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
