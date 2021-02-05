package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.CommonResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SharePost {

    private MutableLiveData<CommonResponse> commonResponseMutableLiveData;
    CommonResponse commonResponse;
    private String tag="SharePost";


    public LiveData<CommonResponse> sharePost(Map<String, String> param, Context context){

        commonResponseMutableLiveData = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.postShare(param).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));
                } else {
                    commonResponse = response.body();
                    Log.d(tag,"---fail-"+new Gson().toJson(response.body()));
                    commonResponseMutableLiveData.setValue(commonResponse);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Log.d(tag,"----"+t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }
}
