package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.FollowingUserResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FolloingUser {

    private MutableLiveData<FollowingUserResponse> followingUserResponseMutableLiveData;
    FollowingUserResponse followingUserResponse;
    private String tag="FolloingUser";
    public LiveData<FollowingUserResponse> getFollowing(Map<String, String> param, Context context){

        followingUserResponseMutableLiveData = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.getFollowing(param).enqueue(new Callback<FollowingUserResponse>() {
            @Override
            public void onResponse(Call<FollowingUserResponse> call, Response<FollowingUserResponse> response) {
                if (response.isSuccessful()) {
                    if(response.body().getCode()==200){
                        followingUserResponse = response.body();

                        followingUserResponseMutableLiveData.setValue(followingUserResponse);
                        Log.d(tag,"- 200---"+new Gson().toJson(response.body()));
                    }else{
                        followingUserResponse = response.body();
                        Toast.makeText(context,followingUserResponse.getMessage(),Toast.LENGTH_SHORT);
                    }

                } else {
                    followingUserResponse = response.body();
                    Log.d(tag,"---fail-"+new Gson().toJson(response.body()));
                    followingUserResponseMutableLiveData.setValue(followingUserResponse);
                }
            }

            @Override
            public void onFailure(Call<FollowingUserResponse> call, Throwable t) {
                Log.d(tag,"----"+t.getMessage());

            }
        });
        return followingUserResponseMutableLiveData;
    }
}
