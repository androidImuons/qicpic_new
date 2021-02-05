package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.GetUnfollowUserResponse;

public interface GetUserInterface {

    void onUserSuccess(LiveData<GetUnfollowUserResponse> getUnfollowUserResponseLiveData);
}
