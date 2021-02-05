package com.saqcess.qicpic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Comment implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("comment_likes_count")
    @Expose
    private Integer commentLikesCount;
    @SerializedName("comment_like_users")
    @Expose
    private List<Object> commentLikeUsers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getCommentLikesCount() {
        return commentLikesCount;
    }

    public void setCommentLikesCount(Integer commentLikesCount) {
        this.commentLikesCount = commentLikesCount;
    }

    public List<Object> getCommentLikeUsers() {
        return commentLikeUsers;
    }

    public void setCommentLikeUsers(List<Object> commentLikeUsers) {
        this.commentLikeUsers = commentLikeUsers;
    }

}
