package com.saqcess.qicpic.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityFollowersNFollowingsBinding;
import com.saqcess.qicpic.view.fragment.FollowersFragment;
import com.saqcess.qicpic.view.fragment.FollowingsFragment;

import java.util.ArrayList;
import java.util.List;

public class FollowersNFollowingsActivity extends AppCompatActivity {

    ActivityFollowersNFollowingsBinding databinding;
    SessionManager sessionManager;
    private ViewPager viewPager;
    String type,followers,followings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databinding= DataBindingUtil.setContentView(FollowersNFollowingsActivity.this,R.layout.activity_followers_n_followings);
        databinding.executePendingBindings();
        databinding.setLifecycleOwner(FollowersNFollowingsActivity.this);
        databinding.toolbar.setTitleTextAppearance(this, R.style.morn_bold);
        setSupportActionBar(databinding.toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(databinding.viewpager);
        databinding.tabs.setSelectedTabIndicatorColor(Color.parseColor("#0B8DF4"));
        databinding.tabs.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        databinding.tabs.setTabTextColors(Color.parseColor("#7E7E78"), Color.parseColor("#0B8DF4"));
        databinding.tabs.setupWithViewPager(viewPager);
        sessionManager=new SessionManager(FollowersNFollowingsActivity.this);
        /*followers=sessionManager.getUserDetails().get(SessionManager.KEY_FOLLOWERS_COUNT);
        followings=sessionManager.getUserDetails().get(SessionManager.KEY_FOLLOWINGS_COUNT);*/
        databinding.tvUserName.setText(sessionManager.getUserDetails().get(SessionManager.KEY_FULL_NAME));
        getSupportActionBar().setTitle(sessionManager.getUserDetails().get(SessionManager.KEY_FULL_NAME));

        databinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfileActivity();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FollowersFragment(), "FOLLOWERS");
        adapter.addFragment(new FollowingsFragment(), "FOLLOWINGS");
        viewPager.setAdapter(adapter);
        switchToTab(type);
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
            return mFragmentTitleList.get(position);
            // return null to display only the icon
            //return null;
        }
    }

    public void switchToTab(String type) {
        if (type.equals("followers")) {
            viewPager.setCurrentItem(0);
        } else if (type.equals("followings")) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToProfileActivity();
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goToProfileActivity();
        return true;
    }
}