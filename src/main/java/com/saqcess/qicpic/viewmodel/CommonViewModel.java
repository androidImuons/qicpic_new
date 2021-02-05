package com.saqcess.qicpic.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.repository.PostUpload;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CommonViewModel extends ViewModel {
    public CommonResponseInterface commonResponseInterface;
    private LiveData<CommonResponse> commonResponseLiveData;


    public void uploadPost(Context context, String post, ArrayList<String> arrayList) {


        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), post);
        HashMap<String, okhttp3.RequestBody> map = new HashMap<>();
        map.put("caption", message);

        commonResponseInterface.onCommoStarted();
        //if the list is null
        if (commonResponseLiveData == null) {
            commonResponseLiveData = new MutableLiveData<CommonResponse>();
            commonResponseLiveData = new PostUpload().postUpload(map,arrayList,context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }else {
            commonResponseLiveData = new PostUpload().postUpload(map,arrayList,context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }
    }
}
