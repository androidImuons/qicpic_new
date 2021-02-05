package com.saqcess.qicpic.view.listeners;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.model.GetMessageResponse;

public interface OnChatGetMessage {
    void onSuccessGetMessage(LiveData<GetMessageResponse> postResponse);
}
