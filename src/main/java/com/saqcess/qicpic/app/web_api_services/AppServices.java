package com.saqcess.qicpic.app.web_api_services;

import com.saqcess.qicpic.appupdate.AppVersionModel;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.FollowingUserResponse;
import com.saqcess.qicpic.model.GetMessageResponse;
import com.saqcess.qicpic.model.GetPostResponseModel;
import com.saqcess.qicpic.model.GetUnfollowUserResponse;
import com.saqcess.qicpic.model.LoginResponseModel;
import com.saqcess.qicpic.model.UpdateDataResponseModel;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.UnfollowUsersResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileViewResponseModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface AppServices {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponseModel> userLogin(@FieldMap Map<String, String> loginMap);

    @FormUrlEncoded
    @POST("user_profile")
    Call<UserProfileResponseModel> userProfile(@FieldMap Map<String, String> userProfileMap);

    @FormUrlEncoded
    @POST("user_profile")
    Call<UserProfileViewResponseModel> GetProfile(@FieldMap Map<String, String> userProfileMap);

    @POST("update_profile")
    Call<UpdateDataResponseModel> updateProfile(@Body RequestBody post);

    @POST("update_profile")
    Call<UpdateDataResponseModel> updateProfilePic(@Body RequestBody post);

    @POST("get_users")
    Call<UnfollowUsersResponseModel> getUnFollowers();

    @FormUrlEncoded
    @POST("get_posts")
    Call<GetPostResponseModel> getPostS(@FieldMap Map<String, String> param);

    /*Parameters :
1) search -> optional (it can be user_id or name)*/
    @FormUrlEncoded
    @POST("get_followers")
    Call<FollowerResponseModel> getFollowers(@FieldMap Map<String, String> followersMap);

    @FormUrlEncoded
    @POST("get_followings")
    Call<FollowerResponseModel> getFollowings(@FieldMap Map<String, String> followingsMap);

    @Multipart
    @POST("upload_post")
    Call<CommonResponse> postUpload(@Header("Authorization") String authHeader, @PartMap() HashMap<String, RequestBody> param,
                                    @Part List<MultipartBody.Part> file);

    @FormUrlEncoded
    @POST("like_post")
    Call<CommonResponse> postLike(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("comment_on_post")
    Call<CommonResponse> postComment(@FieldMap Map<String, String> param);

    //    user_id -> required (Follower user id whom you want to follow)
// action -> required (follow , unfollow)
    @FormUrlEncoded
    @POST("follow_user")
    Call<CommonResponse> followUnfollow(@FieldMap Map<String, String> param);

    //search
    @FormUrlEncoded
    @POST("get_users")
    Call<GetUnfollowUserResponse> getUser(@FieldMap Map<String, String> param);

    //user_id ->required
//post_id ->required
//message ->optional
    @FormUrlEncoded
    @POST("share_post")
    Call<CommonResponse> postShare(@FieldMap Map<String, String> param);

    //search
    @FormUrlEncoded
    @POST("get_followings")
    Call<FollowingUserResponse> getFollowing(@FieldMap Map<String, String> param);


    @FormUrlEncoded
    @POST("like_comment")
    Call<CommonResponse> like_comment(@FieldMap Map<String, String> param);

    //1) post_id -> required
    @FormUrlEncoded
    @POST("save_post")
    Call<CommonResponse> savePost(@FieldMap Map<String, String> param);

    //
//    1) comment_id -> required
//2) comment -> required
    @FormUrlEncoded
    @POST("reply_to_comment")
    Call<CommonResponse> replyToComment(@FieldMap Map<String, String> param);

    //[{"key":"to_user","value":"O3124533","description":"","type":"text","enabled":true}]
    @FormUrlEncoded
    @POST("get_messages")
    Call<GetMessageResponse> get_message(@FieldMap Map<String, String> param);

    // send_message

//    Header Authorization:Bearer<token>
//    Parameters :
//            1)to_user ->
//
//    required(User id)
//2)message ->
//
//    optional(Required if no attachment)
//3)files[] ->
//
//    optional(Required if no message)

    @Multipart
    @POST("send_message")
    Call<CommonResponse> send_message(@Header("Authorization") String authHeader, @PartMap() HashMap<String, RequestBody> param,
                                      @Part List<MultipartBody.Part> file);

    @FormUrlEncoded
    @POST("check_app_version")
    Call<AppVersionModel> updateApp(@FieldMap Map<String, String> groupMap);
}



