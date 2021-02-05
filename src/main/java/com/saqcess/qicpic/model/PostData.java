package com.saqcess.qicpic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("no_of_images")
    @Expose
    private Integer noOfImages;
    @SerializedName("entry_time")
    @Expose
    private String entryTime;
    @SerializedName("post_gallery_path")
    @Expose
    private List<PostGalleryPath> postGalleryPath = null;
    @SerializedName("user")
    @Expose
    private PostUser user;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;
    @SerializedName("like_users")
    @Expose
    private List<LikeUserModel> likeUsers = null;
    @SerializedName("comments_count")
    @Expose
    private Integer commentsCount;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getNoOfImages() {
        return noOfImages;
    }

    public void setNoOfImages(Integer noOfImages) {
        this.noOfImages = noOfImages;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public List<PostGalleryPath> getPostGalleryPath() {
        return postGalleryPath;
    }

    public void setPostGalleryPath(List<PostGalleryPath> postGalleryPath) {
        this.postGalleryPath = postGalleryPath;
    }

    public PostUser getUser() {
        return user;
    }

    public void setUser(PostUser user) {
        this.user = user;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public List<LikeUserModel> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<LikeUserModel> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}