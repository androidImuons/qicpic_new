package com.saqcess.qicpic.view.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import com.saqcess.qicpic.databinding.FragmentFollowersBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.Follower;
import com.saqcess.qicpic.view.activity.ViewProfileActivity;
import com.saqcess.qicpic.view.adapter.FollowersAdapter;
import com.saqcess.qicpic.view.listeners.FollowerListener;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FollowersFragment extends BaseFragment implements FollowerListener, FollowersAdapter.FollowersAdapterListener {

    FragmentFollowersBinding databinding;
    FollowerResponseModel followerResponseModel;
    FollowersAdapter followersAdapter;

    public static FollowersFragment newInstance() {
        return new FollowersFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        followerResponseModel = ViewModelProviders.of(getActivity()).get(FollowerResponseModel.class);
        databinding = DataBindingUtil.inflate(inflater, R.layout.fragment_followers, container, false);
        databinding.setFollowerModel(followerResponseModel);
        View view = databinding.getRoot();
        databinding.setLifecycleOwner(this);
        renderFollowers();
        return view;
    }

    private void renderFollowers() {

        SessionManager session = new SessionManager(getActivity());
        databinding.rvFollowersList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        Map<String, String> followerMap = new HashMap<>();
        followerMap.put("search","");
        //followerMap.put("search",session.getUserDetails().get(SessionManager.KEY_USERID));
        showProgressDialog("Please wait...");
        followerResponseModel.fetchFollowers(getActivity(), followerResponseModel.followerListener = this, followerMap);
    }

    @Override
    public void onGetFollowersSuccess(LiveData<FollowerResponseModel> followerResponse) {

        followerResponse.observe(getViewLifecycleOwner(), new Observer<FollowerResponseModel>() {
            @Override
            public void onChanged(FollowerResponseModel followerResponseModel) {
                hideProgressDialog();
                try {
                    if (followerResponseModel.getCode() == 200 && followerResponseModel.getStatus().equalsIgnoreCase("OK")) {
                        //  showSnackbar(storyFragmentBinding.flStoryFragment, userProfileResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("followerResponseModel", "Response : Code" + followerResponseModel.getCode() + "\n Status : " + followerResponseModel.getStatus() + "\n Message : " + followerResponseModel.getMessage());

                        if (followerResponseModel.getData() != null) {
                            List<Follower> followerList = new ArrayList<>();
                            followerList = followerResponseModel.getData();
                            setRecyclerView(followerList);
                        } else {
                            showSnackbar(databinding.flFollowerFragment, "Nobody is your Follower", Snackbar.LENGTH_SHORT);
                        }

                    } else {
                        showSnackbar(databinding.flFollowerFragment, followerResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onGetFollowingsSuccess(LiveData<FollowerResponseModel> followerResponse) {

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
                        showSnackbar(databinding.flFollowerFragment, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("FollowersFragment", "Response : Code" + commonResponse.getCode() + "\n Status : " + commonResponse.getStatus() + "\n Message : " + commonResponse.getMessage());
                        renderFollowers();
                    } else {
                        showSnackbar(databinding.flFollowerFragment, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
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
                        Log.d("Followers", "-- Follower Response : Code " + userProfileResponseModel.getCode() + "\n Status : " + userProfileResponseModel.getStatus() + "\n Message : " + userProfileResponseModel.getMessage());
                        Log.d("Followers", "--Follower Data : " + userProfileResponseModel.getUser().getFullname());
                        gotoViewProfileActivity(userProfileResponseModel);
                    } else {
                        showSnackbar(databinding.flFollowerFragment, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.d("Followers", e.getMessage());

                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    private void gotoViewProfileActivity(UserProfileResponseModel userProfileResponseModel) {
        Log.d("ProfileActivity", "--gotoViewProfileActivity : " + userProfileResponseModel.getUser().getFullname());

        Intent intent=new Intent(getActivity(), ViewProfileActivity.class);
        intent.putExtra("data",userProfileResponseModel);
        intent.putExtra("type",0);
        getActivity().startActivity(intent);
    }


    private void setRecyclerView(List<Follower> followerList) {
        followersAdapter = new FollowersAdapter(followerList, this);
        databinding.rvFollowersList.setAdapter(followersAdapter);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    @Override
    public void onRemoveFollowerClick(Follower follower) {
        showProgressDialog("Please wait...");

        Map<String, String> followUnfollowMap = new HashMap<>();
        followUnfollowMap.put("user_id", follower.getUserId());
        followUnfollowMap.put("action", "unfollow");            //follow , unfollow
        followerResponseModel.sendfollowUnFollow(getActivity(), followUnfollowMap, followerResponseModel.followerListener = this);

        //  showSnackbar(databinding.flFollowerFragment, "clicked on remove "+follower.getFullname(), Snackbar.LENGTH_SHORT);

    }

    @Override
    public void onFollowerClick(Follower follower) {
        showProgressDialog("Please wait...");
        Map<String, String> followUnfollowMap = new HashMap<>();
        followUnfollowMap.put("user_id", follower.getUserId());
        Log.d("ProfileActivity", "--Follower user_id : " + follower.getUserId());

        followerResponseModel.getFollowerProfile(getActivity(), followUnfollowMap, followerResponseModel.followerListener = this);
    }

}

