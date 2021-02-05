package com.saqcess.qicpic.model;

import java.util.ArrayList;

public class ImageModel {
    String str_folder;
    ArrayList<String> al_imagepath;

    public ArrayList<Integer> getThum_list() {
        return thum_list;
    }

    public void setThum_list(ArrayList<Integer> thum_list) {
        this.thum_list = thum_list;
    }

    ArrayList<Integer> thum_list;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int type;

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAl_imagepath() {
        return al_imagepath;
    }

    public void setAl_imagepath(ArrayList<String> al_imagepath) {
        this.al_imagepath = al_imagepath;
    }



    String str_path,str_thumb;
    boolean boolean_selected;

    public String getStr_path() {
        return str_path;
    }

    public void setStr_path(String str_path) {
        this.str_path = str_path;
    }

    public String getStr_thumb() {
        return str_thumb;
    }

    public void setStr_thumb(String str_thumb) {
        this.str_thumb = str_thumb;
    }

    public boolean isBoolean_selected() {
        return boolean_selected;
    }

    public void setBoolean_selected(boolean boolean_selected) {
        this.boolean_selected = boolean_selected;
    }

}
