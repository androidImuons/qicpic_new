package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

public interface UserProfileListener {

    void onStarted();
    void onUserProfileSuccess(LiveData<UserProfileResponseModel> userProfileResponse);

    void onFailure(String message);

}
