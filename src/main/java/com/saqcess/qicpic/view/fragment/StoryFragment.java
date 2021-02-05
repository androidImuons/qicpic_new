package com.saqcess.qicpic.view.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.GridSpacingItemDecoration;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.StoryFragmentBinding;
import com.saqcess.qicpic.model.PostData;
import com.saqcess.qicpic.model.PostGalleryPath;
import com.saqcess.qicpic.view.activity.AddProfileActivity;
import com.saqcess.qicpic.view.activity.FindMorePeopleActivity;
import com.saqcess.qicpic.view.activity.ViewPostActivity;
import com.saqcess.qicpic.view.adapter.PostsAdapter;
import com.saqcess.qicpic.view.listeners.UserProfileListener;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.util.ArrayList;
import java.util.List;

public class StoryFragment extends BaseFragment implements PostsAdapter.PostsAdapterListener, UserProfileListener {

    public StoryFragmentBinding storyFragmentBinding;
    UserProfileResponseModel userProfileResponseModel;
    private StoryViewModel mViewModel;
    RecyclerView rv_profile_story_gallery;
    private MyClickHandlers handlers;
    private PostsAdapter mAdapter;
    SessionManager sessionManager;

    public static StoryFragment newInstance() {
        return new StoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        userProfileResponseModel = ViewModelProviders.of(getActivity()).get(UserProfileResponseModel.class);
        storyFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.story_fragment, container, false);
        storyFragmentBinding.setUserprofiel(userProfileResponseModel);
        View view = storyFragmentBinding.getRoot();
        storyFragmentBinding.setLifecycleOwner(this);
        handlers = new MyClickHandlers(getActivity());
        sessionManager = new SessionManager(getContext());
        renderStroryFragment(view);
        return view;

    }

    private void renderStroryFragment(View view) {
        // storyFragmentBinding.rvProfileStoryGallery.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        storyFragmentBinding.rvProfileStoryGallery.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        storyFragmentBinding.rvProfileStoryGallery.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
        storyFragmentBinding.rvProfileStoryGallery.setItemAnimator(new DefaultItemAnimator());
        renderProfile();
    }

    private void renderProfile() {
        showProgressDialog("Please wait...");
        userProfileResponseModel.fetchUserProfileData(getActivity(), userProfileResponseModel.userProfileListener = this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StoryViewModel.class);
        // TODO: Use the ViewModel
    }

    private void setRecyclerView(List<PostGalleryPath> postGalleryPathsArraylist) {
        mAdapter = new PostsAdapter(getPosts(postGalleryPathsArraylist), this);
        storyFragmentBinding.rvProfileStoryGallery.setAdapter(mAdapter);
        storyFragmentBinding.setHandlers(handlers);
    }

    private ArrayList<PostGalleryPath> getPosts(List<PostGalleryPath> postGalleryPathsArraylist) {
        ArrayList<PostGalleryPath> postGalleryPaths = new ArrayList<>();
        for (int i = 0; i < postGalleryPathsArraylist.size(); i++) {
            PostGalleryPath postGalleryPath = new PostGalleryPath();
            postGalleryPath.setImageUrl(postGalleryPathsArraylist.get(i).getImageUrl());
            postGalleryPaths.add(postGalleryPath);
        }

        return postGalleryPaths;
    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onPostClicked(PostGalleryPath postGalleryPath, int position) {
        // showSnackbar(storyFragmentBinding.flStoryFragment, "Coming soon!", Snackbar.LENGTH_SHORT);
        Intent intent = new Intent(getContext(), ViewPostActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("user_id", sessionManager.getUserDetails().get(sessionManager.KEY_USERID));
        startActivity(intent);
        //Toast.makeText(getActivity(), "Post clicked! " + postGalleryPath.getImageUrl(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarted() {
        showProgressDialog("Please wait...");
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

                        List<PostData> postDataArrayList = new ArrayList<>();
                        postDataArrayList = userProfileResponseModel.getUser().getPostDat();
                        if (postDataArrayList.size() != 0) {
                            List<PostGalleryPath> postGalleryPathsArraylist = new ArrayList<>();
                            for (int i = 0; i < postDataArrayList.size(); i++) {
                                if (postDataArrayList.get(i).getPostGalleryPath().size() != 0) {

                                    for (int j = 0; j < postDataArrayList.get(i).getPostGalleryPath().size(); j++) {
                                        postGalleryPathsArraylist.add(postDataArrayList.get(i).getPostGalleryPath().get(j));
                                    }

                                }
                            }

                            setRecyclerView(postGalleryPathsArraylist);

                        } else {
                            showSnackbar(storyFragmentBinding.flStoryFragment, "Create Post.Record not found", Snackbar.LENGTH_SHORT);
                        }

                    } else {
                        showSnackbar(storyFragmentBinding.flStoryFragment, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    @Override
    public void onFailure(String message) {
        showSnackbar(storyFragmentBinding.flStoryFragment, message, Snackbar.LENGTH_SHORT);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onAddBioClick(View view) {
            goToAddProfileActivity("addBio");
        }

        public void onEditNameClick(View view) {
            showSnackbar(storyFragmentBinding.flStoryFragment, "Coming soon!", Snackbar.LENGTH_SHORT);

//            goToAddProfileActivity("editName");
        }

        public void onFindMoreClick(View view) {
            // Toast.makeText(getActivity(), "Find More Firends clicked! ", Toast.LENGTH_SHORT).show();
            // showSnackbar(storyFragmentBinding.flStoryFragment, "Coming soon!", Snackbar.LENGTH_SHORT);

            gotoFindMorePeopleActivity();

        }

    }

    private void gotoFindMorePeopleActivity() {
        Intent intent = new Intent(getActivity(), FindMorePeopleActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void goToAddProfileActivity(String type) {
        Intent intent = new Intent(getActivity(), AddProfileActivity.class);
        intent.putExtra("type", type);
        getActivity().startActivity(intent);
    }
}