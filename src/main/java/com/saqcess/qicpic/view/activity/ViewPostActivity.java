package com.saqcess.qicpic.view.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityOzogramHomeBinding;
import com.saqcess.qicpic.databinding.HomeFragmentBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetPostRecordModel;
import com.saqcess.qicpic.model.GetPostResponseModel;
import com.saqcess.qicpic.model.GetUnfollowUserResponse;
import com.saqcess.qicpic.model.UnfollowUserRecordDataModel;
import com.saqcess.qicpic.view.adapter.PostRecycleViewAdapter;
import com.saqcess.qicpic.view.adapter.PostViewAdapter;
import com.saqcess.qicpic.view.adapter.PostViewUnFollowUserListAdapter;
import com.saqcess.qicpic.view.adapter.StoryUserRecycleViewAdapter;
import com.saqcess.qicpic.view.dialog.EditCommentDialog;
import com.saqcess.qicpic.view.dialog.PostMoreOptionDialog;
import com.saqcess.qicpic.view.dialog.SendPostDialog;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.GetPostDataListener;
import com.saqcess.qicpic.view.listeners.GetUserInterface;
import com.saqcess.qicpic.view.listeners.UserProfileViewListener;
import com.saqcess.qicpic.viewmodel.HomeViewModel;
import com.saqcess.qicpic.viewmodel.UserProfileViewResponseModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewPostActivity extends BaseActivity implements GetPostDataListener, PostViewUnFollowUserListAdapter.PostViewUnFollowUserListerner,
        CommonResponseInterface, PostRecycleViewAdapter.PostViewInterface, EditCommentDialog.SendCallBack, GetUserInterface, UserProfileViewListener, PostViewAdapter.PostViewInterface {
    // Session Manager Class
    SessionManager session;
    HomeFragmentBinding activityOzogramHomeBinding;
    public HomeViewModel homeViewModel;
    //  BottomTabViewModel bottomTabViewModel;
    private List<GetPostRecordModel> post = new ArrayList<>();
    private StoryUserRecycleViewAdapter storyUserRecycleViewAdapter;
    private PostViewAdapter postRecycelAdapter;
    private String tag = "OzogramHomeActivity";
    private List<UnfollowUserRecordDataModel> unFollowUserList = new ArrayList<>();
    private PostViewUnFollowUserListAdapter unFollowUserAdapter;
    private int action_position;
    private String user_id;
    private int position;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        activityOzogramHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_ozogram_home);
        activityOzogramHomeBinding = DataBindingUtil.setContentView(this, R.layout.home_fragment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        homeViewModel = ViewModelProviders.of(ViewPostActivity.this).get(HomeViewModel.class);
        activityOzogramHomeBinding.setHome(homeViewModel);
        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("user_id");
        position = bundle.getInt("position");
      /*  homeViewModel.postDataListener = ViewPostActivity.this;
        homeViewModel.commonResponseInterface = ViewPostActivity.this;
        homeViewModel.getUserInterface = ViewPostActivity.this;
        homeViewModel.userProfileListener = ViewPostActivity.this;*/
        // Session class instance
        session = new SessionManager(getApplicationContext());
        activityOzogramHomeBinding.executePendingBindings();
        activityOzogramHomeBinding.setLifecycleOwner(ViewPostActivity.this);
        activityOzogramHomeBinding.toolbar.llHomeToolbar.setVisibility(View.GONE);
        activityOzogramHomeBinding.toolbar.llWithBackButton.setVisibility(View.VISIBLE);
       // activityOzogramHomeBinding.llIncludeBottom.setVisibility(View.GONE);
        activityOzogramHomeBinding.llFollowLayerj.setVisibility(View.GONE);
        setupSwipLayout();
        init();
//


    }


    private void init() {

        storyUserRecycleViewAdapter = new StoryUserRecycleViewAdapter(getApplicationContext(), post);
        activityOzogramHomeBinding.recycleStoryUser.setHasFixedSize(true);
        activityOzogramHomeBinding.recycleStoryUser.setNestedScrollingEnabled(true);
        activityOzogramHomeBinding.recycleStoryUser.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        activityOzogramHomeBinding.recycleStoryUser.setAdapter(storyUserRecycleViewAdapter);

        //staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        postRecycelAdapter = new PostViewAdapter(getApplicationContext(), post, ViewPostActivity.this);
        activityOzogramHomeBinding.recyclePostList.setHasFixedSize(true);
        activityOzogramHomeBinding.recyclePostList.setNestedScrollingEnabled(true);
        LinearLayoutManager llManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        activityOzogramHomeBinding.recyclePostList.setLayoutManager(llManager);
        activityOzogramHomeBinding.recyclePostList.setAdapter(postRecycelAdapter);
        activityOzogramHomeBinding.toolbar.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private void setupSwipLayout() {

        activityOzogramHomeBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activityOzogramHomeBinding.swipeLayout.setRefreshing(true);
                gotoFrtchUserProfile(user_id);
               // homeViewModel.fetchUserProfileData(getApplicationContext(), user_id);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityOzogramHomeBinding.swipeLayout.setRefreshing(true);
        gotoFrtchUserProfile(user_id);
        //homeViewModel.fetchUserProfileData(getApplicationContext(), user_id);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onStarted() {
        Log.d("Home", "On start");
    }


    @Override
    public void onUserProfileSuccess(LiveData<UserProfileViewResponseModel> userProfileResponse) {
        userProfileResponse.observe(ViewPostActivity.this, new Observer<UserProfileViewResponseModel>() {
            @Override
            public void onChanged(UserProfileViewResponseModel responseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setPost(responseModel.getUser().getPostDat());
                    } else {
                        activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(activityOzogramHomeBinding.nestedList, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }


    @Override
    public void onUserSuccess(LiveData<GetUnfollowUserResponse> getUnfollowUserResponseLiveData) {
        getUnfollowUserResponseLiveData.observe(ViewPostActivity.this, new Observer<GetUnfollowUserResponse>() {
            @Override
            public void onChanged(GetUnfollowUserResponse responseModel) {
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setUser(responseModel.getData());
                    } else {
                        activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(activityOzogramHomeBinding.nestedList, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    private void setUser(List<UnfollowUserRecordDataModel> data) {
        unFollowUserList = data;
        unFollowUserAdapter = new PostViewUnFollowUserListAdapter(getApplicationContext(), unFollowUserList,this);
        activityOzogramHomeBinding.viewPagerForUser.setNestedScrollingEnabled(true);
        activityOzogramHomeBinding.viewPagerForUser.setAdapter(unFollowUserAdapter);
    }


    @Override
    public void onSuccess(LiveData<GetPostResponseModel> postResponse) {
        postResponse.observe(ViewPostActivity.this, new Observer<GetPostResponseModel>() {
            @Override
            public void onChanged(GetPostResponseModel responseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setPost(responseModel.getData());
                    } else {
                        activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(activityOzogramHomeBinding.nestedList, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    public void setPost(List<GetPostRecordModel> post) {
        this.post = post;
        if (post.size() > 0) {
            activityOzogramHomeBinding.recyclePostList.setVisibility(View.VISIBLE);
            activityOzogramHomeBinding.llFollowLayerj.setVisibility(View.GONE);
            activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
            storyUserRecycleViewAdapter.updateList(post);
            postRecycelAdapter.updateList(post);
            Log.d(tag,"----pos--"+position);
              activityOzogramHomeBinding.recyclePostList.scrollToPosition(position);
           // activityOzogramHomeBinding.nestedList.scrollTo(0, position);
//            staggeredGridLayoutManager.scrollToPosition(position);
//            activityOzogramHomeBinding.recyclePostList.setLayoutManager(staggeredGridLayoutManager);
            // postRecycelAdapter.insert(action_position, post.get(action_position));
        } else {
            activityOzogramHomeBinding.recyclePostList.setVisibility(View.GONE);
            activityOzogramHomeBinding.llFollowLayerj.setVisibility(View.VISIBLE);
            homeViewModel.getUser(getApplicationContext(), null,homeViewModel.commonResponseInterface =this);
        }
    }


    public List<GetPostRecordModel> getPost() {
        return post;
    }

    @Override
    public void onCommoStarted() {
        showProgressDialog("Please Wait");
    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> commonResponseLiveData) {
        commonResponseLiveData.observe(ViewPostActivity.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse responseModel) {
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d(tag, "like Response : Code" + responseModel.getCode());
                        gotoFrtchUserProfile(user_id);
                    } else {
                        activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                        Log.d(tag, "like Response fail: Code" + responseModel.getCode());
                        showSnackbar(activityOzogramHomeBinding.nestedList, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    activityOzogramHomeBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    private void gotoFrtchUserProfile(String user_id) {
        homeViewModel.fetchUserProfileData(getApplicationContext(), user_id,homeViewModel.userProfileListener = this);

    }

    @Override
    public void onCommonFailure(String message) {
        Log.d(tag, "----like post fail--");
        hideProgressDialog();
    }

    @Override
    public void clickLike(int pos, String action) {

        action_position = pos;
        Log.d(tag, "----click like pos--" + pos);
        homeViewModel.postLike(getApplicationContext(), String.valueOf(post.get(pos).getId()), action,homeViewModel.commonResponseInterface=this);

    }

    @Override
    public void clickComment(int pos) {
        action_position = pos;
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(post.get(pos).getId()));
        EditCommentDialog instance = EditCommentDialog.getInstance(bundle);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
        instance.sendCallBack = (EditCommentDialog.SendCallBack) ViewPostActivity.this;

    }

    @Override
    public void sendPost(int pos) {
        action_position = pos;
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(post.get(pos).getId()));
        SendPostDialog instance = SendPostDialog.getInstance(bundle);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
    }

    @Override
    public void savePOST(int pos) {
        homeViewModel.savePost(getApplicationContext(), post.get(pos).getId().toString(),homeViewModel.commonResponseInterface=this);
    }

    @Override
    public void moreaction(GetPostRecordModel getPostRecordModel) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(getPostRecordModel.getId()));
        bundle.putString("u_id",getPostRecordModel.getUser().getUserId());
        PostMoreOptionDialog instance = PostMoreOptionDialog.getInstance(bundle);
        // instance.unfollowUser=(PostMoreOptionDialog.UnfollowUser)getActivity();
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
    }



    @Override
    public void followUnfollow(Context context, String userId, String action) {
        homeViewModel.followUnfollow(context,user_id,action,homeViewModel.commonResponseInterface =this);
    }

    @Override
    public void sendMessage(String msg, String id) {
        homeViewModel.postComment(getApplicationContext(), id, msg,homeViewModel.commonResponseInterface=this);
    }

    @Override
    public void sendError(String err) {
        showSnackbar(activityOzogramHomeBinding.nestedList, err, Snackbar.LENGTH_SHORT);
    }


    public void moreaction() {
        Bundle bundle = new Bundle();
//        bundle.putString("id", String.valueOf(post.get(pos).getId()));
        PostMoreOptionDialog instance = PostMoreOptionDialog.getInstance(bundle);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

