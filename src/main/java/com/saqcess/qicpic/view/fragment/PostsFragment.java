package com.saqcess.qicpic.view.fragment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.Contrants;
import com.saqcess.qicpic.app.utils.Gallery;
import com.saqcess.qicpic.databinding.PostsFragmentBinding;
import com.saqcess.qicpic.model.ImageModel;
import com.saqcess.qicpic.view.activity.GalleryActivity;
import com.saqcess.qicpic.view.adapter.GirdViewAdapter;
import com.saqcess.qicpic.view.adapter.SpinnerBaseAdapter;
import com.saqcess.qicpic.view.dialog.UploadImagesDialogBoxs;
import com.saqcess.qicpic.viewmodel.GalleryViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

import static android.app.Activity.RESULT_OK;
import static com.saqcess.qicpic.app.utils.Contrants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;

public class PostsFragment extends BaseFragment {

    PostsFragmentBinding binding;

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.posts_fragment, container, false);
        View view = binding.getRoot();
        binding.setLifecycleOwner(this);

        showProgressDialog("Please wait loading...");
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        getActivity().startActivity(intent);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                hideProgressDialog();
            }

        }, 5000);

        return view;
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}