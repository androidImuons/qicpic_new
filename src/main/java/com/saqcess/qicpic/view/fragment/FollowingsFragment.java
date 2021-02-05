package com.saqcess.qicpic.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.FragmentFollowingsBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.Follower;
import com.saqcess.qicpic.view.activity.ViewProfileActivity;
import com.saqcess.qicpic.view.adapter.FollowingAdapter;
import com.saqcess.qicpic.view.listeners.FollowerListener;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowingsFragment extends BaseFragment implements FollowerListener,FollowingAdapter.FollowingAdapterListener {

    FragmentFollowingsBinding dataBinding;
    FollowerResponseModel followerResponseModel;
    FollowingAdapter followingAdapter;

    public static FollowingsFragment newInstance() {
        return new FollowingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        followerResponseModel= ViewModelProviders.of(getActivity()).get(FollowerResponseModel.class);
        dataBinding=  DataBindingUtil.inflate(inflater, R.layout.fragment_followings, container, false);
        dataBinding.setFollowingsModel(followerResponseModel);
        View view = dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        renderFollowings();
        return view;
    }

    private void renderFollowings() {
        SessionManager session = new SessionManager(getActivity());
        dataBinding.rvFollowingList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        Map<String, String> followingMap = new HashMap<>();
        followingMap.put("search", "");
       // followingMap.put("search", session.getUserDetails().get(SessionManager.KEY_USERID));
        showProgressDialog("Please wait...");
        followerResponseModel.fetchFollowings(getActivity(),followerResponseModel.followerListener=this,followingMap);
    }

    @Override
    public void onGetFollowersSuccess(LiveData<FollowerResponseModel> followerResponse) {

    }

    @Override
    public void onGetFollowingsSuccess(LiveData<FollowerResponseModel> followerResponse) {

        followerResponse.observe(getViewLifecycleOwner(), new Observer<FollowerResponseModel>() {
            @Override
            public void onChanged(FollowerResponseModel followerResponseModel) {
                hideProgressDialog();
                try {
                    if (followerResponseModel.getCode() == 200 && followerResponseModel.getStatus().equalsIgnoreCase("OK")) {
                        //  showSnackbar(storyFragmentBinding.flStoryFragment, userProfileResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("followerResponseModel", "Response : Code" + followerResponseModel.getCode() + "\n Status : " + followerResponseModel.getStatus() + "\n Message : " + followerResponseModel.getMessage());

                        if(followerResponseModel.getData()!=null){
                            List<Follower> followingList=new ArrayList<>();
                            followingList=followerResponseModel.getData();
                            setRecyclerView(followingList);
                        }else{
                            showSnackbar(dataBinding.flFollowingFragment, "Your not following anyone.", Snackbar.LENGTH_SHORT);
                        }

                    } else {
                        showSnackbar(dataBinding.flFollowingFragment, followerResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });


    }

    @Override
    public void onGetFollowUnFollowUserResponse(LiveData<CommonResponse> commonResponse) {
        commonResponse.observe(getViewLifecycleOwner(), new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {
                //save access token
                hideProgressDialog();
                try {
                    if (commonResponse.getCode() == 200 && commonResponse.getStatus().equalsIgnoreCase("OK")) {
                        showSnackbar(dataBinding.flFollowingFragment, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("FollowersFragment", "Response : Code" + commonResponse.getCode() + "\n Status : " + commonResponse.getStatus() + "\n Message : " + commonResponse.getMessage());
                        renderFollowings();
                    } else {
                        showSnackbar(dataBinding.flFollowingFragment, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    @Override
    public void onFollowerProfileSuccess(LiveData<UserProfileResponseModel> userProfileResponse) {
        userProfileResponse.observe(getViewLifecycleOwner(), new Observer<UserProfileResponseModel>() {
            @Override
            public void onChanged(UserProfileResponseModel userProfileResponseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (userProfileResponseModel.getCode() == 200 && userProfileResponseModel.getStatus().equalsIgnoreCase("OK")) {
                        //  showSnackbar(activityProfileBinding.llUserProfile, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("ProfileActivity", "-- Following Response : Code " + userProfileResponseModel.getCode() + "\n Status : " + userProfileResponseModel.getStatus() + "\n Message : " + userProfileResponseModel.getMessage());
                        Log.d("ProfileActivity", "--Following Data : " + userProfileResponseModel.getUser().getFullname());

                        Intent intent=new Intent(getActivity(), ViewProfileActivity.class);
                        intent.putExtra("data",userProfileResponseModel);
                        intent.putExtra("type",1);
                        getActivity().startActivity(intent);


                    } else {
                        showSnackbar(dataBinding.flFollowingFragment, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    private void setRecyclerView(List<Follower> followingList) {
        followingAdapter=new FollowingAdapter(this,followingList);
        dataBinding.rvFollowingList.setAdapter(followingAdapter);
    }

    @Override
    public void onFollowingClick(Follower follower) {
        showProgressDialog("Please wait...");

        Map<String, String> followUnfollowMap = new HashMap<>();
        followUnfollowMap.put("user_id", follower.getUserId());
        followUnfollowMap.put("action", "unfollow");            //follow , unfollow
        followerResponseModel.sendfollowUnFollow(getActivity(), followUnfollowMap,followerResponseModel.followerListener=this);

       // showSnackbar(dataBinding.flFollowingFragment,"Clicked on UnFollow"+ follower.getFullname(), Snackbar.LENGTH_SHORT);
    }

    @Override
    public void onViewFollowingClick(Follower follower) {
        showProgressDialog("Please wait...");

        Map<String, String> followUnfollowMap = new HashMap<>();
        followUnfollowMap.put("user_id", follower.getUserId());
        Log.d("ProfileActivity", "--Following user_id : " + follower.getUserId());

        followerResponseModel.getFollowingProfile(getActivity(), followUnfollowMap,followerResponseModel.followerListener=this);

    }

}