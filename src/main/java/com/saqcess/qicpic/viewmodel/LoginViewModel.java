package com.saqcess.qicpic.viewmodel;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saqcess.qicpic.model.LoginResponseModel;
import com.saqcess.qicpic.repository.LoginRepository;
import com.saqcess.qicpic.view.listeners.LoginListener;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {

    public String username = "";
    public String password = "";


    public LoginListener loginListener;
    private LiveData<LoginResponseModel> loginResponseModel;

    public void onLoginButtonClick(View view) {
        if(isInputDataValid()){
            loginListener.onStarted();
            Map<String, String> loginMap = new HashMap<>();
            loginMap.put("user_id", username);
            loginMap.put("password", password);

            //if the list is null
            if (loginResponseModel == null) {
                loginResponseModel = new MutableLiveData<LoginResponseModel>();
                loginResponseModel = new LoginRepository().checkUserLogin(loginMap);
                loginListener.onLoginSuccess(loginResponseModel);
            }else {
                loginResponseModel = new LoginRepository().checkUserLogin(loginMap);
                loginListener.onLoginSuccess(loginResponseModel);
            }
        }/*else {
            loginListener.onLoginFailure(errorMessage);
        }*/


    }

    public boolean isInputDataValid() {
        if(username.isEmpty() && password.isEmpty()){
           loginListener.onLoginFailure("Enter valid username & password");
           return false;
        }else if(username.isEmpty()){
           loginListener.onLoginFailure("Enter valid username");
           return false;
       }else if(password.isEmpty()){
           loginListener.onLoginFailure("Enter valid password");
           return false;
       }else if(password.length() < 5){
           loginListener.onLoginFailure("Enter password having more than 5 characters");
           return false;
       }else{
           return true;
       }

    }
}
