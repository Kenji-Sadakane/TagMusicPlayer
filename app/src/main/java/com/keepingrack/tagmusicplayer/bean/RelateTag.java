package com.keepingrack.tagmusicplayer.bean;

public class RelateTag {

    public enum STATE {DEFAULT, SELECTED, UNSELECTED};

    private String tag;
    private int count;
    private STATE state;

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public STATE getState() { return state; }
    public void setState(STATE state) { this.state = state; }

    public RelateTag(String tag, int count, STATE state) {
        this.tag = tag;
        this.count = count;
        this.state = state;
    }

}
