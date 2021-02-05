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
import com.saqcess.qicpic.model.UpdateDataResponseModel;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.UnfollowUsersResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileViewResponseModel;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    private String tag="ProfileRepository";

    private MutableLiveData<UserProfileResponseModel> userProfileResponse;
    UserProfileResponseModel userProfileResponseModel;

    private MutableLiveData<UpdateDataResponseModel> updateDataResponse;
    UpdateDataResponseModel updateDataResponseModel;

    private MutableLiveData<UnfollowUsersResponseModel> unfollowUsersResponse;
    UnfollowUsersResponseModel unfollowUsersResponseModel;

    private MutableLiveData<CommonResponse> commonResponse;
    CommonResponse commonResponseModel;

    private MutableLiveData<FollowerResponseModel> followerResponse;
    FollowerResponseModel followerResponseModel;

    private MutableLiveData<FollowerResponseModel> followingResponse;
    FollowerResponseModel followingResponseModel;


    public LiveData<UserProfileResponseModel> fetchUserProfileData(Map<String, String> userProfileMap,Context context) {

        userProfileResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.userProfile(userProfileMap).enqueue(new Callback<UserProfileResponseModel>() {
            @Override
            public void onResponse(Call<UserProfileResponseModel> call, Response<UserProfileResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.d("get"," user profile--on fail-"+response.body());
                    userProfileResponseModel = response.body();
                    userProfileResponse.setValue(userProfileResponseModel);
                    Log.d(tag,"--Profile200----"+new Gson().toJson(response.body()));

                } else {
                    userProfileResponseModel = response.body();
                    userProfileResponse.setValue(userProfileResponseModel);
                    Log.d(tag,"--Profile 200---"+new Gson().toJson(response.body()));

                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseModel> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return userProfileResponse;
    }

    public LiveData<UpdateDataResponseModel> updateUserProfile(RequestBody requestBody,Context context) {

        updateDataResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.updateProfile(requestBody).enqueue(new Callback<UpdateDataResponseModel>() {
            @Override
            public void onResponse(Call<UpdateDataResponseModel> call, Response<UpdateDataResponseModel> response) {
                if (response.isSuccessful()) {
                    updateDataResponseModel = response.body();
                    updateDataResponse.setValue(updateDataResponseModel);
                } else {
                    updateDataResponseModel = response.body();
                    updateDataResponse.setValue(updateDataResponseModel);
                }
            }

            @Override
            public void onFailure(Call<UpdateDataResponseModel> call, Throwable t) {
                Log.d("get post", "--on fail-"+t.getMessage());
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return updateDataResponse;
    }

    public LiveData<UpdateDataResponseModel> updateUserProfilePic(RequestBody requestBody, Context context) {

        updateDataResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.updateProfilePic(requestBody).enqueue(new Callback<UpdateDataResponseModel>() {
            @Override
            public void onResponse(Call<UpdateDataResponseModel> call, Response<UpdateDataResponseModel> response) {
                if (response.isSuccessful()) {
                    updateDataResponseModel = response.body();
                    updateDataResponse.setValue(updateDataResponseModel);
                } else {
                    updateDataResponseModel = response.body();
                    updateDataResponse.setValue(updateDataResponseModel);
                }
            }

            @Override
            public void onFailure(Call<UpdateDataResponseModel> call, Throwable t) {
                Log.d("get post", "--on fail-"+t.getMessage());
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return updateDataResponse;
    }

    public LiveData<UnfollowUsersResponseModel> fetchUnFollowUsers(Context context) {

        unfollowUsersResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.getUnFollowers().enqueue(new Callback<UnfollowUsersResponseModel>() {
            @Override
            public void onResponse(Call<UnfollowUsersResponseModel> call, Response<UnfollowUsersResponseModel> response) {
                if (response.isSuccessful()) {
                    unfollowUsersResponseModel = response.body();
                    unfollowUsersResponse.setValue(unfollowUsersResponseModel);
                } else {
                    unfollowUsersResponseModel = response.body();
                    unfollowUsersResponse.setValue(unfollowUsersResponseModel);
                }
            }

            @Override
            public void onFailure(Call<UnfollowUsersResponseModel> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return unfollowUsersResponse;
    }



    public LiveData<CommonResponse> followUnFollow(Map<String, String> param, Context context){

        commonResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.followUnfollow(param).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    commonResponseModel = response.body();
                    commonResponse.setValue(commonResponseModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));
                } else {
                    commonResponseModel = response.body();
                    commonResponse.setValue(commonResponseModel);
                    Log.d(tag,"---fail-"+new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Log.d(tag,"----fail"+t.getMessage());
            }
        });
        return commonResponse;
    }

    public LiveData<FollowerResponseModel> getFollowers(Map<String, String> followerMap, Context context){

        followerResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.getFollowers(followerMap).enqueue(new Callback<FollowerResponseModel>() {
            @Override
            public void onResponse(Call<FollowerResponseModel> call, Response<FollowerResponseModel> response) {
                if (response.isSuccessful()) {
                    followerResponseModel = response.body();
                    followerResponse.setValue(followerResponseModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));
                } else {
                    followerResponseModel = response.body();
                    followerResponse.setValue(followerResponseModel);
                    Log.d(tag,"---fail-"+new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<FollowerResponseModel> call, Throwable t) {
                Log.d(tag,"----"+t.getMessage());
            }
        });
        return followerResponse;
    }

    public LiveData<FollowerResponseModel> getFollowings(Map<String, String> followerMap, Context context){

        followingResponse = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.getFollowings(followerMap).enqueue(new Callback<FollowerResponseModel>() {
            @Override
            public void onResponse(Call<FollowerResponseModel> call, Response<FollowerResponseModel> response) {
                if (response.isSuccessful()) {
                    followingResponseModel = response.body();
                    followingResponse.setValue(followingResponseModel);
                    Log.d(tag,"- 200---"+new Gson().toJson(response.body()));
                } else {
                    followingResponseModel = response.body();
                    followingResponse.setValue(followingResponseModel);
                    Log.d(tag,"---fail-"+new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<FollowerResponseModel> call, Throwable t) {
                Log.d(tag,"----"+t.getMessage());
            }
        });
        return followingResponse;
    }

    private MutableLiveData<UserProfileViewResponseModel> userProfileViewResponseModelMutableLiveData;
    UserProfileViewResponseModel userProfileViewResponseModel;

    public LiveData<UserProfileViewResponseModel> viewProfile(Map<String, String> userProfileMap,Context context) {

        userProfileViewResponseModelMutableLiveData = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class,session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN));
        apiService.GetProfile(userProfileMap).enqueue(new Callback<UserProfileViewResponseModel>() {
            @Override
            public void onResponse(Call<UserProfileViewResponseModel> call, Response<UserProfileViewResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.d("get"," user profile--on fail-"+response.body());
                    userProfileViewResponseModel = response.body();
                    userProfileViewResponseModelMutableLiveData.setValue(userProfileViewResponseModel);
                } else {
                    userProfileViewResponseModel = response.body();
                    userProfileViewResponseModelMutableLiveData.setValue(userProfileViewResponseModel);
                }
            }

            @Override
            public void onFailure(Call<UserProfileViewResponseModel> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
            }
        });
        return userProfileViewResponseModelMutableLiveData;
    }
}
