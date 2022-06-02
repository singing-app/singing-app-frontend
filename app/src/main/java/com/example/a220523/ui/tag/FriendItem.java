package com.example.a220523.ui.tag;

public class FriendItem {
    String txt_num;
    String text_title_insert;
    String text_singer_insert;
    String text_pitch_insert;
    String message;
    String octave;
    int resourceId;

    public FriendItem(){
        this.txt_num = null;
        this.text_title_insert= null;
        this.text_singer_insert = null;
        this.text_pitch_insert = null;
        this.octave = null;
        this.resourceId = 0;
    }
    public FriendItem(String txt_num, int resourceId, String text_title_insert, String text_singer_insert, String text_pitch_insert, String octave) {
        this.txt_num = txt_num;
        this.text_title_insert= text_title_insert;
        this.text_singer_insert = text_singer_insert;
        this.text_pitch_insert = text_pitch_insert;
        this.octave = octave;
        this.resourceId = resourceId;
    }

    public void init(){
        this.txt_num = null;
        this.text_title_insert= null;
        this.text_singer_insert = null;
        this.text_pitch_insert = null;
        this.octave = null;
        this.resourceId = 0;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getTxt_num() {
        return txt_num;
    }

    public String getText_title_insert() {
        return text_title_insert;
    }

    public String getText_singer_insert() {
        return text_singer_insert;
    }

    public String getText_pitch_insert() {
        return text_pitch_insert;
    }

    public String getOctave(){ return octave; }

    public void setText_title_insert(String s) { this.text_title_insert = s; }

    public void setText_singer_insert(String s) {
        this.text_singer_insert = s;
    }

    public void setText_pitch_insert(String s) { this.text_pitch_insert = s; }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setOctave(String octave){this.octave = octave;}

    public void setTxt_num(String s) { this.txt_num = s;}
}
