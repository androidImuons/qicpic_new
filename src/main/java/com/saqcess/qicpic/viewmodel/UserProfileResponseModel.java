package com.saqcess.qicpic.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.model.UserData;
import com.saqcess.qicpic.repository.ProfileRepository;
import com.saqcess.qicpic.view.listeners.UserProfileListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserProfileResponseModel extends ViewModel implements Serializable {
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
    private UserData user;

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

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    private LiveData<UserProfileResponseModel> userProfileResponse;
    public UserProfileListener userProfileListener;

    public void fetchUserProfileData(Context context, UserProfileListener userProfileListener) {
        SessionManager session = new SessionManager(context);
        Map<String, String> userProfileMap = new HashMap<>();
        userProfileMap.put("user_id", session.getUserDetails().get(SessionManager.KEY_USERID));

        if (userProfileResponse == null) {
            userProfileResponse = new MutableLiveData<UserProfileResponseModel>();
            //we will load it asynchronously from server in this method
            userProfileResponse = new ProfileRepository().fetchUserProfileData(userProfileMap,context);
            userProfileListener.onUserProfileSuccess(userProfileResponse);
        }else{
            userProfileResponse = new ProfileRepository().fetchUserProfileData(userProfileMap,context);
            userProfileListener.onUserProfileSuccess(userProfileResponse);
        }
    }


    public void fetchOtherUserProfileData(Context context, UserProfileListener userProfileListener, Map<String, String> userProfileMap) {
        if (userProfileResponse == null) {
            userProfileResponse = new MutableLiveData<UserProfileResponseModel>();
            //we will load it asynchronously from server in this method
            userProfileResponse = new ProfileRepository().fetchUserProfileData(userProfileMap,context);
            userProfileListener.onUserProfileSuccess(userProfileResponse);
        }else{
            userProfileResponse = new ProfileRepository().fetchUserProfileData(userProfileMap,context);
            userProfileListener.onUserProfileSuccess(userProfileResponse);
        }
    }
}
