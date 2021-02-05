package com.saqcess.qicpic.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saqcess.qicpic.app.utils.Contrants;

import java.util.List;

public class GetPostRecordModel {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("uid")
    @Expose
    private Integer uid;
    @SerializedName("caption")
    @Expose
    private String caption;

    public Integer getSaved() {
        return saved;
    }

    public void setSaved(Integer saved) {
        this.saved = saved;
    }

    @SerializedName("saved")
    @Expose
    private Integer saved;
    @SerializedName("no_of_images")
    @Expose
    private Integer noOfImages;
    @SerializedName("entry_time")
    @Expose
    private String entryTime;
    @SerializedName("post_gallery_path")
    @Expose
    private List<PostGalleryPathModel> postGalleryPath = null;
    @SerializedName("user")
    @Expose
    private UserModel user;
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
    private List<CommentModel> comments = null;
    private final static long serialVersionUID = 1199271569860148076L;
    private boolean readMore=true;

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
        return Contrants.getValidString(caption);
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

    public List<PostGalleryPathModel> getPostGalleryPath() {
        return postGalleryPath;
    }

    public void setPostGalleryPath(List<PostGalleryPathModel> postGalleryPath) {
        this.postGalleryPath = postGalleryPath;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
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

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }
    public boolean isReadMore() {
        return readMore;
    }

    public void setReadMore(boolean readMore) {
        this.readMore = readMore;
    }
}
