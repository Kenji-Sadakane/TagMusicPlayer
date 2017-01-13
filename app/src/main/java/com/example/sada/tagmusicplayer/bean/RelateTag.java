package com.example.sada.tagmusicplayer.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RelateTag {

    private String tag;
    private int count;
    private STATE state;
    public enum STATE {DEFAULT, SELECTED, UNSELECTED};
}
