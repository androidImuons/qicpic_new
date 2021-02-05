package com.saqcess.qicpic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostGalleryPathModel {
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("type")
    @Expose
    private String type;
    private final static long serialVersionUID = 91459451031399078L;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
