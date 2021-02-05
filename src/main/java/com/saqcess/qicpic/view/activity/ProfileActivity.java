package com.saqcess.qicpic.view.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityProfileBinding;
import com.saqcess.qicpic.model.PostData;
import com.saqcess.qicpic.model.PostGalleryPath;
import com.saqcess.qicpic.model.UpdateDataResponseModel;
import com.saqcess.qicpic.view.adapter.ProfileStroyAdpter;
import com.saqcess.qicpic.view.fragment.GalleryFragment;
import com.saqcess.qicpic.view.fragment.StoryFragment;
import com.google.android.material.tabs.TabLayout;
import com.saqcess.qicpic.view.listeners.EditProfileListener;
import com.saqcess.qicpic.view.listeners.UserProfileListener;
import com.saqcess.qicpic.viewmodel.EditProfileViewModel;
import com.saqcess.qicpic.viewmodel.UserProfileResponseModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileActivity extends BaseActivity implements ProfileStroyAdpter.ProfileStroyAdpterListener, UserProfileListener , EditProfileListener{

    ActivityProfileBinding activityProfileBinding;
    UserProfileResponseModel userProfileResponseModel;
    EditProfileViewModel editProfileViewModel;
    SessionManager session;
    RecyclerView rv_profile_story;
    private MyClickHandlers handlers;
    ProfileStroyAdpter profileStroyAdpter;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri;
    private File imageFile;
    private RequestBody requestFile;
    private static final int PERMISSION_REQUEST_CODE = 200;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.story_icon,
            R.drawable.photo_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityProfileBinding = DataBindingUtil.setContentView(ProfileActivity.this, R.layout.activity_profile);
        userProfileResponseModel = ViewModelProviders.of(ProfileActivity.this).get(UserProfileResponseModel.class);
        editProfileViewModel = ViewModelProviders.of(ProfileActivity.this).get(EditProfileViewModel.class);

        activityProfileBinding.setUserprofiel(userProfileResponseModel);
        activityProfileBinding.setEditProfile(editProfileViewModel);
        activityProfileBinding.executePendingBindings();
        activityProfileBinding.setLifecycleOwner(this);
        session = new SessionManager(getApplicationContext());
        handlers = new MyClickHandlers(this);
        renderProfile();
        initRecyclerView();

    }

    private void initRecyclerView() {
        rv_profile_story = activityProfileBinding.rvProfileStory;
        rv_profile_story.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        viewPager = activityProfileBinding.viewpager;
        setupViewPager(viewPager);

        tabLayout = activityProfileBinding.tabs;
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StoryFragment(), "STORY");
        adapter.addFragment(new GalleryFragment(), "GALLERY");
        viewPager.setAdapter(adapter);
    }

    private void renderProfile() {

        showProgressDialog("Please wait...");
        userProfileResponseModel.fetchUserProfileData(ProfileActivity.this, userProfileResponseModel.userProfileListener=this);
    }



    @Override
    public void onPostClicked(PostGalleryPath postGalleryPath) {
        Toast.makeText(getApplicationContext(), "Post clicked! " + postGalleryPath.getImageUrl(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStarted() {
        showProgressDialog("Please wait...");
    }

    @Override
    public void onUserProfileSuccess(final LiveData<UserProfileResponseModel> userProfileResponse) {
        userProfileResponse.observe(ProfileActivity.this, new Observer<UserProfileResponseModel>() {
            @Override
            public void onChanged(UserProfileResponseModel userProfileResponseModel) {

                //save access token
                hideProgressDialog();
                try {
                    if (userProfileResponseModel.getCode() == 200 && userProfileResponseModel.getStatus().equalsIgnoreCase("OK")) {
                      //  showSnackbar(activityProfileBinding.llUserProfile, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                        Log.d("ProfileActivity", "Response : Code" + userProfileResponseModel.getCode() + "\n Status : " + userProfileResponseModel.getStatus() + "\n Message : " + userProfileResponseModel.getMessage());
                        Log.d("ProfileActivity", "User Data" + userProfileResponseModel.getUser().getFullname());

                        session.saveUserProfileData(
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

                        // display user
                        activityProfileBinding.setUser(userProfileResponseModel.getUser());

                        // assign click handlers
                        activityProfileBinding.setHandlers(handlers);


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

                        }

                    } else {
                        showSnackbar(activityProfileBinding.llUserProfile, userProfileResponseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });
    }


    private void setRecyclerView(List<PostGalleryPath> postGalleryPathsArraylist) {
        profileStroyAdpter = new ProfileStroyAdpter(getPosts(postGalleryPathsArraylist), this);
        rv_profile_story.setAdapter(profileStroyAdpter);
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
        showSnackbar(activityProfileBinding.llUserProfile, message, Snackbar.LENGTH_SHORT);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    @Override
    public void onUpdateProfileDataSuccess(LiveData<UpdateDataResponseModel> updateDataResponse) {
        updateDataResponse.observe(ProfileActivity.this, new Observer<UpdateDataResponseModel>() {
            @Override
            public void onChanged(UpdateDataResponseModel updateDataResponseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (updateDataResponse.getValue().getCode() == 200 && updateDataResponse.getValue().getStatus().equalsIgnoreCase("OK")) {
                        showSnackbar(activityProfileBinding.llUserProfile, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                       // sessionManager.setEditProfileData(editProfileViewModel.bio, editProfileViewModel.website, editProfileViewModel.gender);
                        Log.d("ProfileActivity", "Response : Code" + updateDataResponse.getValue().getCode() + "\n Status : " + updateDataResponse.getValue().getStatus() + "\n Message : " + updateDataResponse.getValue().getMessage());
                        renderProfile();
                    } else {
                        showSnackbar(activityProfileBinding.llUserProfile, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

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

        public void onEditProfileClick(View view){
            goToEditProfileActivity();
        }

        public void onLogoutClicked(View view){
            goToLogout();
        }

        public void onSettingsClicked(View view){
            goToSettings();
        }

        public void onProfileFabClicked(View view) {

            if (!checkPermission()) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            } else {
                if (checkPermission()) {
                    requestPermissionAndContinue();
                } else {

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                }
            }

        }

        public boolean onProfileImageLongPressed(View view) {
           // Toast.makeText(getApplicationContext(), "Profile image long pressed!", Toast.LENGTH_LONG).show();
            return false;
        }


        public void onFollowersClicked(View view) {
            if(!activityProfileBinding.tvTotalFollowersCount.getText().toString().equalsIgnoreCase("0")){
                gotoFollowersNFollowingsActivity("followers");
            }

        }

        public void onFollowingClicked(View view) {
            if (!activityProfileBinding.tvTotalFollowingCount.getText().toString().equalsIgnoreCase("0")) {
                gotoFollowersNFollowingsActivity("followings");
            }

        }

        public void onPostsClicked(View view) {
          //  showSnackbar(activityProfileBinding.llUserProfile, "Coming soon!", Snackbar.LENGTH_SHORT);
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this,AlertDialog.THEME_HOLO_LIGHT);
        alertDialog.setTitle("Confirm Logout...");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                session.logoutUser();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void gotoFollowersNFollowingsActivity(String type) {
        Intent intent=new Intent(ProfileActivity.this,FollowersNFollowingsActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
        finish();
    }

    private void goToEditProfileActivity() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null) {
                imageUri = selectedImage;
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    imageFile = new File(picturePath);
                    activityProfileBinding.profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                   // showSnackbar(activityProfileBinding.llUserProfile,imageFile.getName().toString(),Snackbar.LENGTH_SHORT);
                    //tvChooseFile.setText(imageFile.getName());
                    requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imageUri)),
                                    imageFile
                            );
                    cursor.close();

                    uploadProfilePic();
                }
            }
            return;
        }

    }

    private void uploadProfilePic() {
        showProgressDialog("Please wait...");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
        .addFormDataPart("user_id", session.getUserDetails().get(SessionManager.KEY_USERID) );
        if (imageFile != null) {
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContentResolver().getType(imageUri)),
                            imageFile
                    );
            builder.addFormDataPart("profile_picture", imageFile.getName(), requestFile);
            RequestBody requestBody = builder.build();
            editProfileViewModel.onUpdateProfilePic(ProfileActivity.this,requestBody,editProfileViewModel.editProfileListener = this);
        }
    }

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(ProfileActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ProfileActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(ProfileActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, READ_EXTERNAL_STORAGE)) {
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(ProfileActivity.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            //openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    //openActivity();
                } else {
                    //getActivity().finish();
                }

            } else {
                //getActivity().finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}