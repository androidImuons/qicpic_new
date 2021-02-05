package com.saqcess.qicpic.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityAddProfileBinding;
import com.saqcess.qicpic.model.UpdateDataResponseModel;
import com.saqcess.qicpic.view.listeners.EditProfileListener;
import com.saqcess.qicpic.viewmodel.EditProfileViewModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddProfileActivity extends BaseActivity implements EditProfileListener {

    ActivityAddProfileBinding addProfileBinding;
    public EditProfileViewModel editProfileViewModel;
    private MyClickHandlers handlers;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addProfileBinding = DataBindingUtil.setContentView(AddProfileActivity.this, R.layout.activity_add_profile);
        editProfileViewModel = ViewModelProviders.of(AddProfileActivity.this).get(EditProfileViewModel.class);
        editProfileViewModel.editProfileListener = this;
        addProfileBinding.setEditProfile(editProfileViewModel);
        handlers = new MyClickHandlers(this);
        addProfileBinding.setHandlers(handlers);
        addProfileBinding.executePendingBindings();
        addProfileBinding.setLifecycleOwner(this);
        sessionManager = new SessionManager(getApplicationContext());

        Intent i = getIntent();
        String type = i.getStringExtra("type");
        if(type.equals("addBio")){
            addProfileBinding.llAddBio.setVisibility(View.VISIBLE);
            addProfileBinding.llEditName.setVisibility(View.GONE);
        }else if(type.equals("editName")){
            addProfileBinding.llAddBio.setVisibility(View.GONE);
            addProfileBinding.llEditName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateProfileDataSuccess(LiveData<UpdateDataResponseModel> updateDataResponse) {
        updateDataResponse.observe(AddProfileActivity.this, new Observer<UpdateDataResponseModel>() {
            @Override
            public void onChanged(UpdateDataResponseModel updateDataResponseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (updateDataResponse.getValue().getCode() == 200 && updateDataResponse.getValue().getStatus().equalsIgnoreCase("OK")) {
                        showSnackbar(addProfileBinding.llAddProfile, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                        sessionManager.setProfileBio(editProfileViewModel.bio);
                        Log.d("AddProfileActivity", "Response : Code" + updateDataResponse.getValue().getCode() + "\n Status : " + updateDataResponse.getValue().getStatus() + "\n Message : " + updateDataResponse.getValue().getMessage());

                    } else {
                        showSnackbar(addProfileBinding.llAddProfile, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    public class MyClickHandlers {

        Context context;

        public MyClickHandlers(Context context) {
            this.context = context;
        }

        public void onCancelClick(View view) {
            goToProfileActivity();

        }

        public void onAcceptAddBioClick(View view){
            updateBio();
            showSnackbar(addProfileBinding.llAddProfile,addProfileBinding.inputAddBio.getText().toString(), Snackbar.LENGTH_SHORT);
        }

        public void onAcceptEditNameClick(View view){
            showSnackbar(addProfileBinding.llAddProfile,addProfileBinding.inputEditName.getText().toString(), Snackbar.LENGTH_SHORT);
        }
    }

    private void updateBio() {

        showProgressDialog("Please wait...");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", sessionManager.getUserDetails().get(SessionManager.KEY_USERID))
                .addFormDataPart("bio", editProfileViewModel.bio);
                       RequestBody requestBody = builder.build();
        editProfileViewModel.onUpdateBio(AddProfileActivity.this, requestBody,editProfileViewModel.editProfileListener = this);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    private void goToProfileActivity() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        }else {
            super.onBackPressed(); //replaced
        }
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }
}