package com.saqcess.qicpic.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityViewProfileBinding;
import com.saqcess.qicpic.view.fragment.ViewGalleryFragment;
import com.saqcess.qicpic.view.fragment.ViewStoryFragment;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends BaseActivity {

    ActivityViewProfileBinding dataBinding;
    UserProfileResponseModel userProfileResponseModel;
    public ViewProfileActivity.MyClickHandlers handler;
    int type;
    SessionManager session;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public int[] tabIcons = {
            R.drawable.story_icon,
            R.drawable.photo_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataBinding = DataBindingUtil.setContentView(ViewProfileActivity.this, R.layout.activity_view_profile);
        userProfileResponseModel = ViewModelProviders.of(ViewProfileActivity.this).get(UserProfileResponseModel.class);
        dataBinding.setUserprofiel(userProfileResponseModel);
        dataBinding.executePendingBindings();
        dataBinding.setLifecycleOwner(this);
        handler = new MyClickHandlers(this);
        session = new SessionManager(getApplicationContext());
        userProfileResponseModel = (UserProfileResponseModel) getIntent().getSerializableExtra("data");
        type=getIntent().getExtras().getInt("type");
        if (type==1){
            dataBinding.txtMessage.setVisibility(View.VISIBLE);
        }else{
            dataBinding.txtMessage.setVisibility(View.GONE);
        }
        userProfileResponseModel= (UserProfileResponseModel) getIntent().getSerializableExtra("data");
        dataBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        renderProfile(userProfileResponseModel);
        initRecyclerView();

        dataBinding.txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityChatMessage.class);
                intent.putExtra("id", session.getViewOtherUserDetails().get(session.KEY_VIEW_USERID));
                intent.putExtra("bio",session.getViewOtherUserDetails().get(session.KEY_VIEW_BIO));
                intent.putExtra("image",session.getViewOtherUserDetails().get(session.KEY_VIEW_PROFILE_PICTURE));
                intent.putExtra("name",session.getViewOtherUserDetails().get(session.KEY_VIEW_FULL_NAME));
               intent.putExtra("type",type);
                startActivity(intent);
            }
        });


        Log.i("Response::", "---ViewProfileActivity --" + new Gson().toJson(userProfileResponseModel));

    }

    private void renderProfile(UserProfileResponseModel userProfileResponseModel) {

        try {
            if (userProfileResponseModel.getCode() == 200 && userProfileResponseModel.getStatus().equalsIgnoreCase("OK")) {
                //  showSnackbar(activityProfileBinding.llUserProfile, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                Log.d("ProfileActivity", "--Response : Code" + userProfileResponseModel.getCode() + "\n Status : " + userProfileResponseModel.getStatus() + "\n Message : " + userProfileResponseModel.getMessage());
                Log.d("ProfileActivity", "--User Data" + userProfileResponseModel.getUser().getFullname());

                // display user
                dataBinding.setUser(userProfileResponseModel.getUser());
                // assign click handlers
                dataBinding.setHandlers(handler);

                session.saveOtherUserProfileData(
                        userProfileResponseModel.getUser().getId(),
                        userProfileResponseModel.getUser().getUserId(),
                        userProfileResponseModel.getUser().getFullname(),
                        userProfileResponseModel.getUser().getEmail(),
                        userProfileResponseModel.getUser().getMobile(),
                        userProfileResponseModel.getUser().getBio(),
                        userProfileResponseModel.getUser().getProfilePicture(),
                        userProfileResponseModel.getUser().getWebsite(),
                        userProfileResponseModel.getUser().getGender(),
                        userProfileResponseModel.getUser().getJoiningDate(),
                        userProfileResponseModel.getUser().getPostsCount(),
                        userProfileResponseModel.getUser().getFollowersCount(),
                        userProfileResponseModel.getUser().getFollowingCount());

            } else {
                showSnackbar(dataBinding.llUserProfile, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
            }
        } catch (Exception e) {
        } finally {
            hideProgressDialog();
        }
    }

    private void initRecyclerView() {


        viewPager = dataBinding.viewpager;
        setupViewPager(viewPager);

        tabLayout = dataBinding.tabs;
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ViewStoryFragment(), "STORY");
        adapter.addFragment(new ViewGalleryFragment(), "GALLERY");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //  return mFragmentTitleList.get(position);
            // return null to display only the icon
            return null;
        }
    }

    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }


        public void onFollowersClicked(View view) {
            if (!dataBinding.tvTotalFollowersCount.getText().toString().equalsIgnoreCase("0")) {
                gotoFollowersNFollowingsActivity("followers");
            }

        }

        public void onFollowingClicked(View view) {
            if (!dataBinding.tvTotalFollowingCount.getText().toString().equalsIgnoreCase("0")) {
                gotoFollowersNFollowingsActivity("followings");
            }

        }

        public void onPostsClicked(View view) {
            //  showSnackbar(activityProfileBinding.llUserProfile, "Coming soon!", Snackbar.LENGTH_SHORT);
        }
    }

    private void gotoFollowersNFollowingsActivity(String type) {
        Intent intent = new Intent(ViewProfileActivity.this, FollowersNFollowingsActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();
    }

}