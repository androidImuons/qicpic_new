package com.saqcess.qicpic.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.LoginResponseModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private MutableLiveData<LoginResponseModel> loginResponse;
    LoginResponseModel loginResponseModel;

    public LiveData<LoginResponseModel> checkUserLogin(Map<String, String> loginMap) {

        loginResponse = new MutableLiveData<>();

        AppServices apiService = ServiceGenerator.createService(AppServices.class);
        apiService.userLogin(loginMap).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful()) {
                    loginResponseModel = response.body();
                    Log.d(getClass().getName(),"-if--"+response.body());
                    loginResponse.setValue(loginResponseModel);
                } else {
                    loginResponseModel = response.body();
                    Log.d(getClass().getName(),"-else--"+response.body());
                    loginResponse.setValue(loginResponseModel);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.d(getClass().getName(),"-fail--"+t.getMessage());
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return loginResponse;
    }

}
