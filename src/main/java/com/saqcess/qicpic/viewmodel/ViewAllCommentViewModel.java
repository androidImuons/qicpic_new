package com.saqcess.qicpic.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetPostResponseModel;
import com.saqcess.qicpic.repository.GetPosts;
import com.saqcess.qicpic.repository.LikePost;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.GetPostDataListener;

import java.util.HashMap;
import java.util.Map;

public class ViewAllCommentViewModel extends ViewModel {
    public GetPostDataListener postDataListener;
    LiveData<GetPostResponseModel> getPosts;
    public CommonResponseInterface commonResponseInterface;


    public void getPost(Context context ,String Post_id ) {
        HashMap<String,String> param=new HashMap<>();
        param.put("post_id",Post_id);
        postDataListener.onStarted();
        Log.d("HomeViewModel", "--1-");
        if (getPosts == null) {
            Log.d("HomeViewModel", "--2-");
//            getPosts = new MutableLiveData<GetPostResponseModel>();
            getPosts = new GetPosts().GetPost(context,param);
            postDataListener.onSuccess(getPosts);
        } else {
            Log.d("HomeViewModel", "--3-");
            postDataListener.onSuccess(getPosts = new GetPosts().GetPost(context,param));
        }
    }

    private LiveData<CommonResponse> commonResponseLiveData;

    public void likeComment(Context context, String post_id) {
        Map<String, String> likeparam = new HashMap<>();
        likeparam.put("comment_id", post_id);
       // likeparam.put("action", action);

        if (commonResponseLiveData == null) {
            commonResponseLiveData = new MutableLiveData<CommonResponse>();
            commonResponseLiveData = new LikePost().likeComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        } else {
            commonResponseLiveData = new LikePost().likeComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }
    }

    public void postComment(Context context, String post_id, String action) {

        Map<String, String> likeparam = new HashMap<>();
        likeparam.put("post_id", post_id);
        likeparam.put("comment", action);

        //if the list is null
        if (commonResponseLiveData == null) {
            commonResponseLiveData = new MutableLiveData<CommonResponse>();
            commonResponseLiveData = new LikePost().postComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        } else {
            commonResponseLiveData = new LikePost().postComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }
    }

    public void replyToComment(Context context, String post_id, String message) {

        Map<String, String> likeparam = new HashMap<>();
        likeparam.put("comment_id", post_id);
        likeparam.put("comment", message);

        //if the list is null
        if (commonResponseLiveData == null) {
            commonResponseLiveData = new MutableLiveData<CommonResponse>();
            commonResponseLiveData = new LikePost().replyToComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        } else {
            commonResponseLiveData = new LikePost().replyToComment(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }
    }


}
