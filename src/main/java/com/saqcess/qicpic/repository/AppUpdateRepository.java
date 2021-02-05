package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.appupdate.AppVersionModel;


import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppUpdateRepository {
    MutableLiveData<AppVersionModel> commonResponseLiveData;
    AppVersionModel commonResponse;
    private String tag="AppUpdateRepository";

    public LiveData<AppVersionModel> appUpdate(Context context, Long code){
        SessionManager myPreferenceManager=new SessionManager(context);
        commonResponseLiveData = new MutableLiveData<>();
        AppServices apiService = ServiceGenerator.createService(AppServices.class,myPreferenceManager.getUserDetails().get(myPreferenceManager.KEY_ACCESS_TOKEN));
        HashMap<String,String>param=new HashMap<>();
        Log.d(tag,"---version code--"+code);
        param.put("version_code",String.valueOf(code));
        param.put("device_type","A");
        apiService.updateApp(param).enqueue(new Callback<AppVersionModel>() {
            @Override
            public void onResponse(Call<AppVersionModel> call, Response<AppVersionModel> response) {
                if (response.isSuccessful()) {
                    commonResponse = response.body();
                    commonResponseLiveData.setValue(commonResponse);
                    Log.d(tag,"- success-"+new Gson().toJson(response.body()));
                } else {
                    commonResponse = response.body();
                    commonResponseLiveData.setValue(commonResponse);
                    Log.d(tag,"- unsuccess-"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<AppVersionModel> call, Throwable t) {
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return commonResponseLiveData;
    }

}
