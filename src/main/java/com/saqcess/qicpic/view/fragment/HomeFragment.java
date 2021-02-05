package com.saqcess.qicpic.view.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.ConnectivityReceiver;
import com.saqcess.qicpic.app.utils.MyApplication;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.HomeFragmentBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetPostRecordModel;
import com.saqcess.qicpic.model.GetPostResponseModel;
import com.saqcess.qicpic.model.GetUnfollowUserResponse;
import com.saqcess.qicpic.model.UnfollowUserRecordDataModel;
import com.saqcess.qicpic.view.adapter.PostRecycleViewAdapter;
import com.saqcess.qicpic.view.adapter.StoryUserRecycleViewAdapter;
import com.saqcess.qicpic.view.adapter.UnFollowUserListAdapter;
import com.saqcess.qicpic.view.dialog.EditCommentDialog;
import com.saqcess.qicpic.view.dialog.PostMoreOptionDialog;
import com.saqcess.qicpic.view.dialog.SendPostDialog;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.GetPostDataListener;
import com.saqcess.qicpic.view.listeners.GetUserInterface;
import com.saqcess.qicpic.viewmodel.FollowerResponseModel;
import com.saqcess.qicpic.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements GetPostDataListener,
        CommonResponseInterface, PostRecycleViewAdapter.PostViewInterface, EditCommentDialog.SendCallBack, GetUserInterface, UnFollowUserListAdapter.UnfollowInterface {

    private HomeViewModel homeViewModel;
    HomeFragmentBinding databinding;
    SessionManager session;
    private List<GetPostRecordModel> post = new ArrayList<>();
    private StoryUserRecycleViewAdapter storyUserRecycleViewAdapter;
    private PostRecycleViewAdapter postRecycelAdapter;
    private String tag = "OzogramHomeActivity";
    private List<UnfollowUserRecordDataModel> unFollowUserList = new ArrayList<>();
    private UnFollowUserListAdapter unFollowUserAdapter;
    private int action_position;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        databinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);
        databinding.setHome(homeViewModel);
        View view = databinding.getRoot();
        databinding.setLifecycleOwner(this);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        }
        // Session class instance
        session = new SessionManager(getActivity());
        if(ConnectivityReceiver.isConnected()){
            setupSwipLayout();
            init();
        }else{
            showSnackbar(databinding.flHome, "Sorry! Not connected to internet", Snackbar.LENGTH_SHORT);
        }

        return view;
    }

    private void init() {

        storyUserRecycleViewAdapter = new StoryUserRecycleViewAdapter(getActivity(), post);
        databinding.recycleStoryUser.setHasFixedSize(true);
        databinding.recycleStoryUser.setNestedScrollingEnabled(true);
        databinding.recycleStoryUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        databinding.recycleStoryUser.setAdapter(storyUserRecycleViewAdapter);


        postRecycelAdapter = new PostRecycleViewAdapter(getActivity(), post,this);
        databinding.recyclePostList.setHasFixedSize(true);
        databinding.recyclePostList.setNestedScrollingEnabled(true);
        databinding.recyclePostList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        databinding.recyclePostList.setAdapter(postRecycelAdapter);


    }


   
    private void setupSwipLayout() {

        databinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databinding.swipeLayout.setRefreshing(true);
                gotoRefresh();
            }
        });
    }

    private void gotoRefresh() {
        homeViewModel.getPost(getActivity(),homeViewModel.postDataListener=this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(ConnectivityReceiver.isConnected()){
            databinding.swipeLayout.setRefreshing(true);
            gotoRefresh();
        }else{
            showSnackbar(databinding.flHome, "Sorry! Not connected to internet", Snackbar.LENGTH_SHORT);
        }

    }

  /*  @Override
    public void onBackPressed() {
        finishAffinity();
    }
*/
    @Override
    public void onStarted() {
        Log.d("Home", "On start");
    }

    @Override
    public void onUserSuccess(LiveData<GetUnfollowUserResponse> getUnfollowUserResponseLiveData) {
        getUnfollowUserResponseLiveData.observe(getActivity(), new Observer<GetUnfollowUserResponse>() {
            @Override
            public void onChanged(GetUnfollowUserResponse responseModel) {
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setUser(responseModel.getData());
                    } else {
                        databinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(databinding.flHome, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    databinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    private void setUser(List<UnfollowUserRecordDataModel> data) {
        unFollowUserList = data;
        unFollowUserAdapter = new UnFollowUserListAdapter(getActivity(), unFollowUserList,this);
        databinding.viewPagerForUser.setNestedScrollingEnabled(true);
        databinding.viewPagerForUser.setAdapter(unFollowUserAdapter);
    }


    @Override
    public void onSuccess(LiveData<GetPostResponseModel> postResponse) {
        postResponse.observe(getActivity(), new Observer<GetPostResponseModel>() {
            @Override
            public void onChanged(GetPostResponseModel responseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setPost(responseModel.getData());
                    } else {
                        databinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(databinding.flHome, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    databinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onFailure(String message) {
        databinding.swipeLayout.setRefreshing(false);
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
            databinding.recyclePostList.setVisibility(View.VISIBLE);
            databinding.llFollowLayerj.setVisibility(View.GONE);
            databinding.swipeLayout.setRefreshing(false);
            storyUserRecycleViewAdapter.updateList(post);
            postRecycelAdapter.updateList(post);
            // postRecycelAdapter.insert(action_position, post.get(action_position));
        } else {
            databinding.recyclePostList.setVisibility(View.GONE);
            databinding.llFollowLayerj.setVisibility(View.VISIBLE);
            homeViewModel.getUser(getActivity(), null,homeViewModel.commonResponseInterface=this);
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
        commonResponseLiveData.observe(getActivity(), new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse responseModel) {
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d(tag, "like Response : Code" + responseModel.getCode());
                       // homeViewModel.getPost(getActivity());
                        gotoRefresh();
                    } else {
                        databinding.swipeLayout.setRefreshing(false);
                        Log.d(tag, "like Response fail: Code" + responseModel.getCode());
                        showSnackbar(databinding.flHome, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    databinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
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
        homeViewModel.postLike(getActivity(), String.valueOf(post.get(pos).getId()), action,homeViewModel.commonResponseInterface=this);

    }

    @Override
    public void clickComment(int pos) {
        action_position = pos;
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(post.get(pos).getId()));
        EditCommentDialog instance = EditCommentDialog.getInstance(bundle);
        instance.show(getActivity().getSupportFragmentManager(), instance.getClass().getSimpleName());
        instance.sendCallBack = this;

    }

    @Override
    public void sendPost(int pos) {
        action_position = pos;
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(post.get(pos).getId()));
        SendPostDialog instance = SendPostDialog.getInstance(bundle );
        instance.show(getActivity().getSupportFragmentManager(), instance.getClass().getSimpleName());
    }

    @Override
    public void savePOST(int pos) {
        homeViewModel.savePost(getActivity(), post.get(pos).getId().toString(),homeViewModel.commonResponseInterface=this);
    }

    @Override
    public void sendMessage(String msg, String id) {
        homeViewModel.postComment(getActivity(), id, msg,homeViewModel.commonResponseInterface=this);
    }

    @Override
    public void sendError(String err) {
        showSnackbar(databinding.flHome, err, Snackbar.LENGTH_SHORT);
    }


    public void moreaction(GetPostRecordModel getPostRecordModel) {
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(getPostRecordModel.getId()));
        bundle.putString("u_id",getPostRecordModel.getUser().getUserId());
        PostMoreOptionDialog instance = PostMoreOptionDialog.getInstance(bundle);
        // instance.unfollowUser=(PostMoreOptionDialog.UnfollowUser)getActivity();
        instance.show(getActivity().getSupportFragmentManager(), instance.getClass().getSimpleName());
    }

    @Override
    public void followUnfollow(Context context, String userId, String action) {
        homeViewModel.followUnfollow(context,userId,action,homeViewModel.commonResponseInterface=this);
    }

}

