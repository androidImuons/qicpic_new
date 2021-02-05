package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.GetUnfollowUserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetUser {
    private MutableLiveData<GetUnfollowUserResponse> liveData;
    GetUnfollowUserResponse responsemodel;

    public MutableLiveData<GetUnfollowUserResponse> getUser(Map<String,String> param,Context context) {

        liveData = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        Log.d("get post", "--1-");
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_ACCESS_TOKEN));
        apiService.getUser(param).enqueue(new Callback<GetUnfollowUserResponse>() {
            @Override
            public void onResponse(Call<GetUnfollowUserResponse> call, Response<GetUnfollowUserResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("get user", "--2-"+new Gson().toJson(response.body()));
                    responsemodel = response.body();
                    liveData.setValue(responsemodel);
                } else {
                    Log.d("get user", "--3-"+response.message());
                    responsemodel = response.body();
                    liveData.setValue(responsemodel);
                }
            }

            @Override
            public void onFailure(Call<GetUnfollowUserResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("get post", "--on fail-"+t.getMessage());
            }
        });
        return liveData;
    }
}
