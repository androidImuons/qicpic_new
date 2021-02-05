package com.saqcess.qicpic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saqcess.qicpic.app.utils.Contrants;

import java.util.ArrayList;
import java.util.List;

public class CommentModel implements Parcelable {
    @SerializedName("user_id")
    @Expose
    private String userId;

    protected CommentModel(Parcel in) {
        userId = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        fullname = in.readString();
        comment = in.readString();
        time = in.readString();
        if (in.readByte() == 0) {
            comment_likes_count = null;
        } else {
            comment_likes_count = in.readInt();
        }
        replyList = in.createTypedArrayList(CommentModel.CREATOR);
        readMore = in.readByte() != 0;
        profile_picture = in.readString();
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("comment")
    @Expose
    private String comment;


    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("comment_likes_count")
    @Expose
    private Integer comment_likes_count;

    public List<LikeUserModel> getComment_like_users() {
        return comment_like_users;
    }





    public void setComment_like_users(List<LikeUserModel> comment_like_users) {
        this.comment_like_users = comment_like_users;
    }

    @SerializedName("comment_like_users")
    @Expose
    private List<LikeUserModel> comment_like_users;

    public List<CommentModel> getReplyList() {
        return replyList;
    }

    public void setReplyList(ArrayList<CommentModel> replyList) {
        this.replyList = replyList;
    }

    @SerializedName("replies")
    @Expose
    private ArrayList<CommentModel> replyList;



    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getComment_likes_count() {
        return comment_likes_count;
    }

    public void setComment_likes_count(Integer comment_likes_count) {
        this.comment_likes_count = comment_likes_count;
    }

    private boolean readMore=true;

    public String getProfile_picture() {
        return Contrants.getValidString(profile_picture);
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

    private final static long serialVersionUID = 6945037347844154059L;

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
        return Contrants.getValidString(comment);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isReadMore() {
        return readMore;
    }

    public void setReadMore(boolean readMore) {
        this.readMore = readMore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(fullname);
        parcel.writeString(comment);
        parcel.writeString(time);
        if (comment_likes_count == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(comment_likes_count);
        }
        parcel.writeTypedList(replyList);
        parcel.writeByte((byte) (readMore ? 1 : 0));
        parcel.writeString(profile_picture);
    }
}
