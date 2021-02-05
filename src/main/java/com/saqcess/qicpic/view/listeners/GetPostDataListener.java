package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.GetPostResponseModel;

public interface GetPostDataListener {
    void onStarted();
    void onSuccess(LiveData<GetPostResponseModel> postResponse);
    void onFailure(String message);
}
