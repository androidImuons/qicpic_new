package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.FollowingUserResponse;

public interface FollowingUserListInterface {
    void onSuccessFollowingUser(LiveData<FollowingUserResponse> postResponse);
}

