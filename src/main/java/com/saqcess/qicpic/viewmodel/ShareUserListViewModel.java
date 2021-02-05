package com.saqcess.qicpic.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.FollowingUserResponse;
import com.saqcess.qicpic.repository.FolloingUser;
import com.saqcess.qicpic.repository.SharePost;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.FollowingUserListInterface;

import java.util.HashMap;
import java.util.Map;

public class ShareUserListViewModel extends ViewModel {
    public FollowingUserListInterface followingUserListInterface;
public CommonResponseInterface commonResponseInterface;
    private LiveData<FollowingUserResponse> livedata;

    public void getFollowing(Context context,String search) {
        commonResponseInterface.onCommoStarted();
        Map<String,String> param=new HashMap<>();
        if(search!=null){
            param.put("search",search);
        }
        if (livedata == null) {
            livedata = new MutableLiveData<FollowingUserResponse>();
            livedata = new FolloingUser().getFollowing(param,context);
            followingUserListInterface.onSuccessFollowingUser(livedata);
        } else {
            Log.d("following", "--3-");
            livedata = new FolloingUser().getFollowing(param,context);
            followingUserListInterface.onSuccessFollowingUser(livedata);
        }
    }
LiveData<CommonResponse> commonResponseLiveData;
    public void sharePost(Context context, String user_id, String post_id, String message) {
        commonResponseInterface.onCommoStarted();
        Map<String, String> likeparam = new HashMap<>();
        likeparam.put("user_id", user_id);
        likeparam.put("post_id", post_id);
        likeparam.put("message", message);
        //if the list is null
        if ( commonResponseLiveData== null) {
            commonResponseLiveData = new MutableLiveData<CommonResponse>();
            commonResponseLiveData = new SharePost().sharePost(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        } else {
            commonResponseLiveData = new SharePost().sharePost(likeparam, context);
            commonResponseInterface.onCommonSuccess(commonResponseLiveData);
        }
    }

}
