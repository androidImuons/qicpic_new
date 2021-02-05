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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostUpload {

    double file_size;
    private String tag="PostUpload";
    CommonResponse commonResponse;
    private MutableLiveData<CommonResponse> commonResponseMutableLiveData;
    public LiveData<CommonResponse> postUpload(HashMap<String, RequestBody> param, ArrayList<String> filepath, Context context) {
        commonResponseMutableLiveData = new MutableLiveData<>();



        MultipartBody.Part body1 = null;
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < filepath.size(); i++) {
            Log.d(tag,"--upload url--"+filepath.get(i));
            parts.add(prepareFilePart(filepath.get(i)));
        }

        if (file_size>10000){
            Log.d(tag,"-----size send--"+file_size);
            commonResponse=new CommonResponse();
            commonResponse.setCode(404);
            commonResponse.setMessage("Maximum 10 MB Size Allowed");
            commonResponseMutableLiveData.setValue(commonResponse);
            return commonResponseMutableLiveData;
        }else{
            Log.d(tag,"-----size send--"+file_size);
        }


        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.getApiService();
        Call<CommonResponse> call = apiService.postUpload("Bearer " +session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN),
                param, parts); //, body


        call.enqueue(new Callback<CommonResponse>() {

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
                Log.d(tag, "--------------" + t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }

    private MultipartBody.Part prepareFilePart(String fileUri){

        File file = new File(fileUri);
        MultipartBody.Part body = null;
        long length = file.length();
        length = length/1024;
        file_size=file_size+length;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
         body = MultipartBody.Part.createFormData("post_gallery_arr[]", file.getName().replace(" ", "_"), requestFile);
        return body;
    }

}
