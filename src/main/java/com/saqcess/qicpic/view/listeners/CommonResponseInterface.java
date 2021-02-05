package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.CommonResponse;

public interface CommonResponseInterface {
    void onCommoStarted();
    void onCommonSuccess(LiveData<CommonResponse> userProfileResponse);
    void onCommonFailure(String message);
}
