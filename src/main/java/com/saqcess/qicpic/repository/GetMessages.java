package com.saqcess.qicpic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.app.web_api_services.AppServices;
import com.saqcess.qicpic.app.web_api_services.ServiceGenerator;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetMessageResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMessages {
    private MutableLiveData<GetMessageResponse> mutableLiveData;
    private GetMessageResponse getMessageResponse;

    private MutableLiveData<CommonResponse> commonResponseMutableLiveData;
    private CommonResponse commonResponse;
    private String tag = "GetMessages";

    public MutableLiveData<GetMessageResponse> getMessages(Context context, String user_id) {
        mutableLiveData = new MutableLiveData<>();
        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_ACCESS_TOKEN));
        Map<String, String> param = new HashMap<>();
        param.put("to_user", user_id);
        apiService.get_message(param).enqueue(new Callback<GetMessageResponse>() {
            @Override
            public void onResponse(Call<GetMessageResponse> call, Response<GetMessageResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("response 200", "--2-" + new Gson().toJson(response.body()));
                    getMessageResponse = response.body();
                    mutableLiveData.setValue(getMessageResponse);
                } else {
                    Log.d("response not 200", "--3-" + response.message());
                    getMessageResponse = response.body();
                    mutableLiveData.setValue(getMessageResponse);
                }
            }

            @Override
            public void onFailure(Call<GetMessageResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("response fail", "--on fail-" + t.getMessage());
            }
        });
        return mutableLiveData;
    }

    public MutableLiveData<CommonResponse> sendMessage(Context context, String user_id, String message, ArrayList<String> filepath) {
        commonResponseMutableLiveData = new MutableLiveData<>();
        MultipartBody.Part body1 = null;
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < filepath.size(); i++) {
            Log.d(tag, "--upload url--" + filepath.get(i));
            parts.add(prepareFilePart(filepath.get(i)));
        }

        if (file_size > 10000) {
            Log.d(tag, "-----size send--" + file_size);
            commonResponse = new CommonResponse();
            commonResponse.setCode(404);
            commonResponse.setMessage("Maximum 10 MB Size Allowed");
            commonResponseMutableLiveData.setValue(commonResponse);
            return commonResponseMutableLiveData;
        } else {
            Log.d(tag, "-----size send--" + file_size);
        }


        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_ACCESS_TOKEN));

        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, message);
        RequestBody requestBody1 = RequestBody.create(MultipartBody.FORM, user_id);
        HashMap<String, okhttp3.RequestBody> map = new HashMap<>();

        if (!message.equals("")){
           // RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), message);
            map.put("message", requestBody);
            Log.d(tag,"send mssage-"+message+"--"+user_id);
        }else{
            Log.d(tag,"no  mssage-"+message+"--"+user_id);
        }

        map.put("to_user", requestBody1);
        Call<CommonResponse> call = apiService.send_message("Bearer " + session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN),
                map, parts); //, body

        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("response 200", "--2-" + new Gson().toJson(response.body()));
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                } else {
                    Log.d("response not 200", "--3-" + response.message());
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("response fail", "--on fail-" + t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }
    public MutableLiveData<CommonResponse> sendFile(Context context, String user_id, ArrayList<String> filepath) {
        commonResponseMutableLiveData = new MutableLiveData<>();
        MultipartBody.Part body1 = null;
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (int i = 0; i < filepath.size(); i++) {
            Log.d(tag, "--upload url--" + filepath.get(i));
            parts.add(prepareFilePart(filepath.get(i)));
        }

        if (file_size > 10000) {
            Log.d(tag, "-----size send--" + file_size);
            commonResponse = new CommonResponse();
            commonResponse.setCode(404);
            commonResponse.setMessage("Maximum 10 MB Size Allowed");
            commonResponseMutableLiveData.setValue(commonResponse);
            return commonResponseMutableLiveData;
        } else {
            Log.d(tag, "-----size send--" + file_size);
        }


        SessionManager session = new SessionManager(context);
        AppServices apiService = ServiceGenerator.createService(AppServices.class, session.getUserDetails().get(session.KEY_ACCESS_TOKEN));


        RequestBody requestBody1 = RequestBody.create(MultipartBody.FORM, user_id);
        HashMap<String, okhttp3.RequestBody> map = new HashMap<>();
        Log.d(tag,"send mssage-"+user_id);

        Log.d(tag,"send filee-"+parts.size());

        map.put("to_user", requestBody1);
        Call<CommonResponse> call = apiService.send_message("Bearer " + session.getUserDetails().get(SessionManager.KEY_ACCESS_TOKEN),
                map, parts); //, body

        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("response 200", "--2-" + new Gson().toJson(response.body()));
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                } else {
                    Log.d("response not 200", "--3-" + response.message());
                    commonResponse = response.body();
                    commonResponseMutableLiveData.setValue(commonResponse);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                //Toast.makeText(UserRepository.this.getClass(), "Please check your internet", Toast.LENGTH_SHORT).show();
                Log.d("response fail", "--on fail-" + t.getMessage());
            }
        });
        return commonResponseMutableLiveData;
    }

    double file_size;

    private MultipartBody.Part prepareFilePart(String fileUri) {

        File file = new File(fileUri);
        MultipartBody.Part body = null;
        long length = file.length();
        length = length / 1024;
        file_size = file_size + length;
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        body = MultipartBody.Part.createFormData("files[]", file.getName().replace(" ", "_"), requestFile);
        return body;
    }
}
