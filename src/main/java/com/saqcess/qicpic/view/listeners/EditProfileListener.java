package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.UpdateDataResponseModel;

public interface EditProfileListener {

    void onUpdateProfileDataSuccess(LiveData<UpdateDataResponseModel> updateDataResponse);

}
