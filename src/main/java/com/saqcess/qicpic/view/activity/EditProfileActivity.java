package com.saqcess.qicpic.view.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.databinding.ActivityEditProfileBinding;
import com.saqcess.qicpic.model.UpdateDataResponseModel;
import com.saqcess.qicpic.view.listeners.EditProfileListener;
import com.saqcess.qicpic.viewmodel.EditProfileViewModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditProfileActivity extends BaseActivity implements EditProfileListener, View.OnClickListener {

    ActivityEditProfileBinding activityEditProfileBinding;
    public EditProfileViewModel editProfileViewModel;
    SessionManager sessionManager;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri;
    private File imageFile=null;
    private RequestBody requestFile;
    private static final int PERMISSION_REQUEST_CODE = 200;
    String spin_val;
    String[] gender = { "Male", "Female" };//array of strings used to populate the spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editProfileViewModel = ViewModelProviders.of(EditProfileActivity.this).get(EditProfileViewModel.class);
        activityEditProfileBinding = DataBindingUtil.setContentView(EditProfileActivity.this, R.layout.activity_edit_profile);
        editProfileViewModel.editProfileListener = this;
        activityEditProfileBinding.executePendingBindings();
        activityEditProfileBinding.setLifecycleOwner(this);
        activityEditProfileBinding.setEditProfile(editProfileViewModel);
        sessionManager = new SessionManager(getApplicationContext());
        renderEditProfile();
    }

    private void renderEditProfile() {
        editProfileViewModel.setProfilePicture(sessionManager.getUserDetails().get(SessionManager.KEY_PROFILE_PICTURE));
        editProfileViewModel.setFullName(sessionManager.getUserDetails().get(SessionManager.KEY_FULL_NAME));
        editProfileViewModel.setUserName(sessionManager.getUserDetails().get(SessionManager.KEY_USERID));
        editProfileViewModel.setBio(sessionManager.getUserDetails().get(SessionManager.KEY_BIO));
        editProfileViewModel.setEmail(sessionManager.getUserDetails().get(SessionManager.KEY_EMAIL));
        editProfileViewModel.setMobileNumber(sessionManager.getUserDetails().get(SessionManager.KEY_MOBILE));
        editProfileViewModel.setGender(sessionManager.getUserDetails().get(SessionManager.KEY_GENDER));
        editProfileViewModel.setWebsite(sessionManager.getUserDetails().get(SessionManager.KEY_WEBSITE));
        activityEditProfileBinding.setEditProfile(editProfileViewModel);

        activityEditProfileBinding.ivAccept.setOnClickListener(this);
        activityEditProfileBinding.ivCancel.setOnClickListener(this);
        activityEditProfileBinding.tvUpdateProfilePic.setOnClickListener(this);
        activityEditProfileBinding.spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spin_val = gender[position];//saving the value selected
                editProfileViewModel.setGender(spin_val.toLowerCase());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //setting array adaptors to spinners
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(EditProfileActivity.this,R.layout.spinner_item_list, gender);
        activityEditProfileBinding.spinnerId.setAdapter(spin_adapter);
        String gen = sessionManager.getUserDetails().get(SessionManager.KEY_GENDER);
        for(int i=0;i<gender.length ;i++){
            if(gen.equalsIgnoreCase(gender[i])){
                activityEditProfileBinding.spinnerId.setSelection(i);
                editProfileViewModel.setGender(gender[i].toLowerCase());

            }
        }
    }

  /*  private void updateProfiel() {
        showProgressDialog("Please wait...");
        editProfileViewModel.onUpdateProfile(EditProfileActivity.this, editProfileViewModel.editProfileListener = this);
    }
*/

    @Override
    public void onUpdateProfileDataSuccess(LiveData<UpdateDataResponseModel> updateDataResponse) {
       // Log.d("UpdateDataResponseModel", "UpdateDataResponseModel : "+updateDataResponse.getValue().getCode() +"\n Message "+updateDataResponse.getValue().getMessage());

        updateDataResponse.observe(EditProfileActivity.this, new Observer<UpdateDataResponseModel>() {
            @Override
            public void onChanged(UpdateDataResponseModel updateDataResponseModel) {

                //save access token
                hideProgressDialog();
                try {
                    if (updateDataResponse.getValue().getCode() == 200 && updateDataResponse.getValue().getStatus().equalsIgnoreCase("OK")) {
                        showSnackbar(activityEditProfileBinding.llEditProfileData, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                        sessionManager.setEditProfileData(editProfileViewModel.bio, editProfileViewModel.website, editProfileViewModel.gender);
                        Log.d("EditProfileActivity", "Response : Code" + updateDataResponse.getValue().getCode() + "\n Status : " + updateDataResponse.getValue().getStatus() + "\n Message : " + updateDataResponse.getValue().getMessage());

                    } else {
                        showSnackbar(activityEditProfileBinding.llEditProfileData, updateDataResponse.getValue().getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    Log.d("EditProfileActivity",e.getMessage());
                } finally {
                    hideProgressDialog();
                }
            }
        });

    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_accept) {
            updateProfile();

        } else if (view.getId() == R.id.iv_cancel) {
            goToProfileActivity();
        }else if(view.getId() == R.id.tv_updateProfilePic){
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
                    activityEditProfileBinding.profileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                   // showSnackbar(activityEditProfileBinding.llEditProfileData, imageFile.getName().toString(), Snackbar.LENGTH_SHORT);
                    //tvChooseFile.setText(imageFile.getName());
                    requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imageUri)),
                                    imageFile
                            );
                    cursor.close();

                }
            }
            return;
        }

    }

    private void updateProfile() {
        String website=editProfileViewModel.website.replace("\"","");
        showProgressDialog("Please wait...");
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", sessionManager.getUserDetails().get(SessionManager.KEY_USERID))
                .addFormDataPart("bio", editProfileViewModel.bio)
                .addFormDataPart("website",website)
                .addFormDataPart("gender", editProfileViewModel.gender.toLowerCase());

        if (imageFile != null) {
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContentResolver().getType(imageUri)),
                            imageFile
                    );
            builder.addFormDataPart("profile_picture", imageFile.getName(), requestFile);
        }
        Log.d("EditProfileActivity", "Parameters : user_id" + sessionManager.getUserDetails().get(SessionManager.KEY_USERID) +
                "\n bio : " + editProfileViewModel.bio + "\n website : " + editProfileViewModel.website+
                "\n gender : " + editProfileViewModel.gender.toLowerCase());

        RequestBody requestBody = builder.build();
        editProfileViewModel.onUpdateProfile(EditProfileActivity.this, requestBody, editProfileViewModel.editProfileListener = this);

    }

    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(EditProfileActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(EditProfileActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(EditProfileActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, READ_EXTERNAL_STORAGE)) {
                android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(EditProfileActivity.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.permission_necessary));
                alertBuilder.setMessage(R.string.storage_permission_is_encessary_to_wrote_event);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
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
    protected void onDestroy() {
        super.onDestroy();
        activityEditProfileBinding=null;
    }


}