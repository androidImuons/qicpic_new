package com.saqcess.qicpic.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.ActivityFindMorePeopleBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.view.adapter.UnFollowUserAdapter;
import com.saqcess.qicpic.view.listeners.UnFollowUserListener;
import com.saqcess.qicpic.viewmodel.UnFollowerData;
import com.saqcess.qicpic.viewmodel.UnfollowUsersResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindMorePeopleActivity extends BaseActivity implements UnFollowUserListener,UnFollowUserAdapter.UnFollowUserAdapterListener {

    ActivityFindMorePeopleBinding activityFindMorePeopleBinding;
    UnfollowUsersResponseModel unfollowUsersResponseModel;
    UnFollowUserAdapter unFollowUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFindMorePeopleBinding= DataBindingUtil.setContentView(FindMorePeopleActivity.this,R.layout.activity_find_more_people);
        unfollowUsersResponseModel= ViewModelProviders.of(FindMorePeopleActivity.this).get(UnfollowUsersResponseModel.class);
        activityFindMorePeopleBinding.toolbar.setTitleTextAppearance(this, R.style.morn_bold);
        setSupportActionBar(activityFindMorePeopleBinding.toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Discover People");
        activityFindMorePeopleBinding.setUnfollowUserModel(unfollowUsersResponseModel);
        activityFindMorePeopleBinding.executePendingBindings();
        activityFindMorePeopleBinding.setLifecycleOwner(FindMorePeopleActivity.this);
        init();
    }

    private void init() {
        activityFindMorePeopleBinding.rvNewSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        activityFindMorePeopleBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfileActivity();
            }
        });
        renderFindMorePeople();

    }

    private void renderFindMorePeople() {
        showProgressDialog("Please wait...");
        unfollowUsersResponseModel.fetchUnFollowUsersList(FindMorePeopleActivity.this, unfollowUsersResponseModel.unFollowUserListener=this);
    }

    @Override
    public void onGetUnFollowUserSuccess(LiveData<UnfollowUsersResponseModel> unfollowUsersResponse) {
        unfollowUsersResponse.observe(FindMorePeopleActivity.this, new Observer<UnfollowUsersResponseModel>() {
            @Override
            public void onChanged(UnfollowUsersResponseModel unfollowUsersResponseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (unfollowUsersResponseModel.getCode() == 200 && unfollowUsersResponseModel.getStatus().equalsIgnoreCase("OK")) {
                        //showSnackbar(activityFindMorePeopleBinding.llFindFollowers, unfollowUsersResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("FindMorePeopleActivity", "Response : Code" + unfollowUsersResponseModel.getCode() + "\n Status : " + unfollowUsersResponseModel.getStatus() + "\n Message : " + unfollowUsersResponseModel.getMessage());


                        List<UnFollowerData> unFollowUserArrayList=new ArrayList<>();
                        unFollowUserArrayList=unfollowUsersResponseModel.getData();
                        setRecyclerView(unFollowUserArrayList);


                    } else {
                        showSnackbar(activityFindMorePeopleBinding.llFindFollowers,"People are not available to discover.", Snackbar.LENGTH_SHORT);
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
        commonResponse.observe(FindMorePeopleActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {
                //save access token
                hideProgressDialog();
                try {
                    if (commonResponse.getCode() == 200 && commonResponse.getStatus().equalsIgnoreCase("OK")) {
                        showSnackbar(activityFindMorePeopleBinding.llFindFollowers, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("FindMorePeopleActivity", "Response : Code" + commonResponse.getCode() + "\n Status : " + commonResponse.getStatus() + "\n Message : " + commonResponse.getMessage());
                        renderFindMorePeople();
                    } else {
                        showSnackbar(activityFindMorePeopleBinding.llFindFollowers, commonResponse.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }

    private void setRecyclerView(List<UnFollowerData> unFollowUserArrayList) {
        unFollowUserAdapter=new UnFollowUserAdapter(unFollowUserArrayList,this);
        activityFindMorePeopleBinding.rvNewSuggestions.setAdapter(unFollowUserAdapter);

    }

    @Override
    public void onFollowRequestClicked(UnFollowerData unFollowerData) {
        showProgressDialog("Please wait...");

        Map<String, String> followUnfollowMap = new HashMap<>();
        followUnfollowMap.put("user_id", unFollowerData.getUserId());
        followUnfollowMap.put("action", "follow");            //follow , unfollow
        unfollowUsersResponseModel.sendfollowUnFollow(FindMorePeopleActivity.this, followUnfollowMap,unfollowUsersResponseModel.unFollowUserListener=this);

       // Toast.makeText(getApplicationContext(), "Follow clicked! " + unFollowerData.getFullname() + "user id : "+unFollowerData.getUserId() , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveUserClicked(UnFollowerData unFollowerData) {
        Toast.makeText(getApplicationContext(), "Remove clicked! " + unFollowerData.getFullname(), Toast.LENGTH_SHORT).show();

    }

    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToProfileActivity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goToProfileActivity();
        return true;
    }
}