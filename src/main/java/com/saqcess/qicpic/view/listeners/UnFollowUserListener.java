package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.viewmodel.UnfollowUsersResponseModel;

public interface UnFollowUserListener {
    public void onGetUnFollowUserSuccess(LiveData<UnfollowUsersResponseModel> unfollowUsersResponse);

    void onGetFollowUnFollowUserResponse(LiveData<CommonResponse> commonResponse);
}
