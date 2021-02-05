package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

public interface FollowerListener {
    void onGetFollowersSuccess(LiveData<FollowerResponseModel> followerResponse);

    void onGetFollowingsSuccess(LiveData<FollowerResponseModel> followerResponse);

    void onGetFollowUnFollowUserResponse(LiveData<CommonResponse> commonResponse);

    void onFollowerProfileSuccess(LiveData<UserProfileResponseModel> userProfileResponse);
}
