package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.LoginResponseModel;

public interface LoginListener {

    void onStarted();
    void onLoginSuccess(LiveData<LoginResponseModel> loginResponse);
    void onLoginFailure(String message);

}
