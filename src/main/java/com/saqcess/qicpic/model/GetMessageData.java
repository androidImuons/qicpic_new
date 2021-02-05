package com.saqcess.qicpic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMessageData {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("from_id")
    @Expose
    private Integer fromId;
    @SerializedName("to_id")
    @Expose
    private Integer toId;
    @SerializedName("from_user_id")
    @Expose
    private String fromUserId;
    @SerializedName("to_user_id")
    @Expose
    private String toUserId;
    @SerializedName("from_fullname")
    @Expose
    private String fromFullname;
    @SerializedName("to_fullname")
    @Expose
    private String toFullname;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("entry_time")
    @Expose
    private String entryTime;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("read_status")
    @Expose
    private String readStatus;
    @SerializedName("post_id")
    @Expose
    private Integer postId;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("files")
    @Expose
    private List<String> files = null;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("post")
    @Expose
    private GetPostRecordModel post;
    private final static long serialVersionUID = 9180541956731043114L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromFullname() {
        return fromFullname;
    }

    public void setFromFullname(String fromFullname) {
        this.fromFullname = fromFullname;
    }

    public String getToFullname() {
        return toFullname;
    }

    public void setToFullname(String toFullname) {
        this.toFullname = toFullname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public GetPostRecordModel getPost() {
        return post;
    }

    public void setPost(GetPostRecordModel post) {
        this.post = post;
    }

}
