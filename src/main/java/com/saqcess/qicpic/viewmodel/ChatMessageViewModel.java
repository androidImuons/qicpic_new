package com.saqcess.qicpic.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetMessageResponse;
import com.saqcess.qicpic.repository.GetMessages;
import com.saqcess.qicpic.view.activity.ActivityChatMessage;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.OnChatGetMessage;

import java.util.ArrayList;

public class ChatMessageViewModel extends ViewModel {
    private MutableLiveData<GetMessageResponse> mutableLiveData;
    private GetMessageResponse getMessageResponse;
    public OnChatGetMessage chatGetMessage;
    private MutableLiveData<CommonResponse> responseMutableLiveData;
    CommonResponse commonResponse;
  public   CommonResponseInterface commonResponseInterface;


    public void getMessage(Context context, ActivityChatMessage activityChatMessage,String to_user_id){

        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<GetMessageResponse>();
            //we will load it asynchronously from server in this method

            mutableLiveData = new GetMessages().getMessages(context,to_user_id);
            chatGetMessage.onSuccessGetMessage(mutableLiveData);
        }else{
            mutableLiveData = new GetMessages().getMessages(context,to_user_id);
            chatGetMessage.onSuccessGetMessage(mutableLiveData);        }
    }

    public void sedMessage(Context context, String to_user_id, String message, ArrayList<String> arrayList){

        if (responseMutableLiveData == null) {
            responseMutableLiveData = new MutableLiveData<CommonResponse>();
            //we will load it asynchronously from server in this method

            responseMutableLiveData = new GetMessages().sendMessage(context,to_user_id,message,arrayList);
            commonResponseInterface.onCommonSuccess(responseMutableLiveData);
        }else{
            responseMutableLiveData = new GetMessages().sendMessage(context,to_user_id,message,arrayList);
            commonResponseInterface.onCommonSuccess(responseMutableLiveData);

        }
    }

    public void sedfile(Context context, String to_user_id, ArrayList<String> arrayList){

        if (responseMutableLiveData == null) {
            responseMutableLiveData = new MutableLiveData<CommonResponse>();
            //we will load it asynchronously from server in this method

            responseMutableLiveData = new GetMessages().sendFile(context,to_user_id,arrayList);
            commonResponseInterface.onCommonSuccess(responseMutableLiveData);
        }else{
            responseMutableLiveData = new GetMessages().sendFile(context,to_user_id,arrayList);
            commonResponseInterface.onCommonSuccess(responseMutableLiveData);

        }
    }
}

