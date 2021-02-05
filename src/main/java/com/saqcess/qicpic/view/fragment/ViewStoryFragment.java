package com.saqcess.qicpic.view.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.GridSpacingItemDecoration;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.FragmentViewStoryBinding;
import com.saqcess.qicpic.model.PostData;
import com.saqcess.qicpic.model.PostGalleryPath;
import com.saqcess.qicpic.view.activity.ViewPostActivity;
import com.saqcess.qicpic.view.adapter.PostsAdapter;
import com.saqcess.qicpic.view.listeners.UserProfileListener;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewStoryFragment extends BaseFragment implements PostsAdapter.PostsAdapterListener , UserProfileListener {

    FragmentViewStoryBinding dataBinding;
    UserProfileResponseModel userProfileResponseModel;
    private PostsAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_story, container, false);
        userProfileResponseModel = ViewModelProviders.of(getActivity()).get(UserProfileResponseModel.class);
        dataBinding.setUserprofiel(userProfileResponseModel);
        View view =dataBinding.getRoot();
        dataBinding.setLifecycleOwner(this);
        renderStroryFragment(view);
        return view;
    }

    private void renderStroryFragment(View view) {
        dataBinding.rvProfileStoryGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        dataBinding.rvProfileStoryGallery.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
        dataBinding.rvProfileStoryGallery.setItemAnimator(new DefaultItemAnimator());
        renderProfile();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void renderProfile() {
        SessionManager session = new SessionManager(getActivity());
        Map<String, String> userProfileMap = new HashMap<>();
        userProfileMap.put("user_id", session.getViewOtherUserDetails().get(SessionManager.KEY_VIEW_USERID));

        showProgressDialog("Please wait...");
        userProfileResponseModel.fetchOtherUserProfileData(getActivity(), userProfileResponseModel.userProfileListener=this,userProfileMap);
    }

    @Override
    public void onPostClicked(PostGalleryPath postGalleryPath, int position) {
        SessionManager session = new SessionManager(getActivity());
        Map<String, String> userProfileMap = new HashMap<>();
        //showSnackbar(dataBinding.flStoryFragment, "Coming soon!", Snackbar.LENGTH_SHORT);
        Intent intent = new Intent(getContext(), ViewPostActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("user_id", session.getViewOtherUserDetails().get(SessionManager.KEY_VIEW_USERID));
        startActivity(intent);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onUserProfileSuccess(LiveData<UserProfileResponseModel> userProfileResponse) {
        userProfileResponse.observe(getViewLifecycleOwner(), new Observer<UserProfileResponseModel>() {
            @Override
            public void onChanged(UserProfileResponseModel userProfileResponseModel) {

                //save access token
                hideProgressDialog();
                try {
                    if (userProfileResponseModel.getCode() == 200 && userProfileResponseModel.getStatus().equalsIgnoreCase("OK")) {
                        //  showSnackbar(storyFragmentBinding.flStoryFragment, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("ProfileActivity", "Response : Code" + userProfileResponseModel.getCode() + "\n Status : " + userProfileResponseModel.getStatus() + "\n Message : " + userProfileResponseModel.getMessage());

                        List<PostData> postDataArrayList=new ArrayList<>();
                        postDataArrayList=userProfileResponseModel.getUser().getPostDat();
                        if(postDataArrayList.size() != 0 ){
                            List<PostGalleryPath> postGalleryPathsArraylist=new ArrayList<>();
                            for(int i=0;i<postDataArrayList.size();i++){
                                if(postDataArrayList.get(i).getPostGalleryPath().size() != 0){

                                    for (int j=0;j<postDataArrayList.get(i).getPostGalleryPath().size();j++){
                                        postGalleryPathsArraylist.add(postDataArrayList.get(i).getPostGalleryPath().get(j));
                                    }

                                }
                            }

                            setRecyclerView(postGalleryPathsArraylist);

                        }else{
                            showSnackbar(dataBinding.flStoryFragment, "Create Post.Record not found", Snackbar.LENGTH_SHORT);
                        }

                    } else {
                        showSnackbar(dataBinding.flStoryFragment, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    private void setRecyclerView(List<PostGalleryPath> postGalleryPathsArraylist) {
        mAdapter = new PostsAdapter(getPosts(postGalleryPathsArraylist),this);
        dataBinding.rvProfileStoryGallery.setAdapter(mAdapter);
    }

    private ArrayList<PostGalleryPath> getPosts(List<PostGalleryPath> postGalleryPathsArraylist) {
        ArrayList<PostGalleryPath> postGalleryPaths = new ArrayList<>();
        for(int i=0;i<postGalleryPathsArraylist.size();i++){
            PostGalleryPath postGalleryPath = new PostGalleryPath();
            postGalleryPath.setImageUrl(postGalleryPathsArraylist.get(i).getImageUrl());
            postGalleryPaths.add(postGalleryPath);
        }

        return postGalleryPaths;
    }

    @Override
    public void onFailure(String message) {

    }
}