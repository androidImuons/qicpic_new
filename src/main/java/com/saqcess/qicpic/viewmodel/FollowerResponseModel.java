package com.saqcess.qicpic.viewmodel;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.Follower;
import com.saqcess.qicpic.repository.ProfileRepository;
import com.saqcess.qicpic.view.listeners.FollowerListener;

import java.util.List;
import java.util.Map;

public class FollowerResponseModel extends ViewModel {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Follower> data = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Follower> getData() {
        return data;
    }

    public void setData(List<Follower> data) {
        this.data = data;
    }

    private LiveData<FollowerResponseModel> followerResponse;
    public FollowerListener followerListener;

    private LiveData<CommonResponse> commonResponse;

    public void fetchFollowers(Context context, FollowerListener followerListener,Map<String, String> followerMap) {

        if (followerResponse == null) {
            followerResponse = new MutableLiveData<FollowerResponseModel>();
            //we will load it asynchronously from server in this method
            followerResponse = new ProfileRepository().getFollowers(followerMap,context);
            followerListener.onGetFollowersSuccess(followerResponse);
        }else{
            followerResponse = new ProfileRepository().getFollowers(followerMap,context);
            followerListener.onGetFollowersSuccess(followerResponse);        }
    }

    public void fetchFollowings(Context context, FollowerListener followerListener,Map<String, String> followerMap) {

        if (followerResponse == null) {
            followerResponse = new MutableLiveData<FollowerResponseModel>();
            //we will load it asynchronously from server in this method
            followerResponse = new ProfileRepository().getFollowings(followerMap,context);
            followerListener.onGetFollowingsSuccess(followerResponse);
        }else{
            followerResponse = new ProfileRepository().getFollowings(followerMap,context);
            followerListener.onGetFollowingsSuccess(followerResponse);        }
    }


    public void sendfollowUnFollow(Context context, Map<String, String> followDataMap, FollowerListener followerListener){
        if (commonResponse == null) {
            commonResponse = new MutableLiveData<CommonResponse>();
            //we will load it asynchronously from server in this method
            commonResponse = new ProfileRepository().followUnFollow(followDataMap,context);
            followerListener.onGetFollowUnFollowUserResponse(commonResponse);
        }else{
            commonResponse = new ProfileRepository().followUnFollow(followDataMap,context);
            followerListener.onGetFollowUnFollowUserResponse(commonResponse);        }
    }

    private LiveData<UserProfileResponseModel> userProfileResponse;
    public void getFollowerProfile(FragmentActivity context, Map<String, String> followUnfollowMap, FollowerListener followerListener) {

        if (userProfileResponse == null) {
            userProfileResponse = new MutableLiveData<UserProfileResponseModel>();
            //we will load it asynchronously from server in this method
            userProfileResponse = new ProfileRepository().fetchUserProfileData(followUnfollowMap,context);
            followerListener.onFollowerProfileSuccess(userProfileResponse);
        }else{
            userProfileResponse = new ProfileRepository().fetchUserProfileData(followUnfollowMap,context);
            followerListener.onFollowerProfileSuccess(userProfileResponse);
        }
    }

    public void getFollowingProfile(FragmentActivity context, Map<String, String> followUnfollowMap, FollowerListener followerListener) {

        if (userProfileResponse == null) {
            userProfileResponse = new MutableLiveData<UserProfileResponseModel>();
            //we will load it asynchronously from server in this method
            userProfileResponse = new ProfileRepository().fetchUserProfileData(followUnfollowMap,context);
            followerListener.onFollowerProfileSuccess(userProfileResponse);
        }else{
            userProfileResponse = new ProfileRepository().fetchUserProfileData(followUnfollowMap,context);
            followerListener.onFollowerProfileSuccess(userProfileResponse);
        }

    }
}
