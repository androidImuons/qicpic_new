package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.viewmodel.UserProfileViewResponseModel;

public interface UserProfileViewListener {

    void onStarted();
    void onUserProfileSuccess(LiveData<UserProfileViewResponseModel> userProfileResponse);

    void onFailure(String message);

}
