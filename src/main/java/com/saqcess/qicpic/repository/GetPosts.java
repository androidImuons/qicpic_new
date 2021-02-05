package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.GetPostResponseModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetPosts {
    private MutableLiveData<GetPostResponseModel> loginResponse;
    GetPostResponseModel getpostModel;

    public MutableLiveData<GetPostResponseModel> GetPost(Context context,HashMap<String,String> param) {

        loginResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        Log.d("get post", "--1-");
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_ACCESS_TOKEN));
        apiService.getPostS(param).enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.d("get post", "--2-"+new Gson().toJson(response.body()));
                    getpostModel = response.body();
                    loginResponse.setValue(getpostModel);
                } else {
                    Log.d("get post", "--3-"+response.message());
                    getpostModel = response.body();
                    loginResponse.setValue(getpostModel);
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("get post", "--on fail-"+t.getMessage());
            }
        });
        return loginResponse;
    }
}
