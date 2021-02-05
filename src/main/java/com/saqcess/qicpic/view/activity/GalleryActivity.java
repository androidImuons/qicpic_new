package com.saqcess.qicpic.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.ConnectivityReceiver;
import com.saqcess.qicpic.app.utils.Contrants;
import com.saqcess.qicpic.app.utils.Gallery;
import com.saqcess.qicpic.databinding.ActivityGalleryBinding;
import com.saqcess.qicpic.model.ImageModel;
import com.saqcess.qicpic.view.adapter.GirdViewAdapter;
import com.saqcess.qicpic.view.adapter.SpinnerBaseAdapter;
import com.saqcess.qicpic.view.dialog.UploadImagesDialogBoxs;
import com.saqcess.qicpic.view.fragment.PhotoFragment;
import com.saqcess.qicpic.view.fragment.PostGalleryFragment;
import com.saqcess.qicpic.view.fragment.VideoFragment;
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

import static com.saqcess.qicpic.app.utils.Contrants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;

//https://deepshikhapuri.wordpress.com/2017/03/29/get-all-videos-from-gallery-in-android/
public class GalleryActivity extends BaseActivity {
    ActivityGalleryBinding galleryBinding;
    GalleryViewModel galleryViewModel;
    private String tag = "GalleryActivity";
    private boolean boolean_folder;
    private GirdViewAdapter obj_adapter;
    private boolean is_crop;
    private boolean is_check_open;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PostGalleryFragment postGalleryFrament;
    private ArrayList<ImageModel> al_images;
    private int selected_type;
    private int selected_folder;
    private ViewPagerAdapter adapter;
    private PhotoFragment photofragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryBinding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        galleryViewModel = ViewModelProviders.of(GalleryActivity.this).get(GalleryViewModel.class);
        galleryBinding.setGallery(galleryViewModel);
        galleryBinding.executePendingBindings();
        galleryBinding.setLifecycleOwner(GalleryActivity.this);

        if (ConnectivityReceiver.isConnected()) {
            setupViewPager();
            checkPermission();
            setListner();
        } else {
            showSnackbar(galleryBinding.llMainLayout, "Sorry! Not connected to internet", Snackbar.LENGTH_SHORT);
        }

    }

    private void setListner() {
        galleryBinding.inculdeGalleryToolBar.spinnerShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_type = al_images.get(i).getType();

                postGalleryFrament.setList(al_images.get(i).getAl_imagepath(), al_images.get(i).getType());

                selected_folder = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        galleryBinding.inculdeGalleryToolBar.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        galleryBinding.inculdeGalleryToolBar.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_type == 0) {
                    if (postGalleryFrament.selectedImageList.isEmpty()) {
                        Log.d(tag, "----is emtpy--");
                        showSnackbar(galleryBinding.galleryViewPager, "Select Images or Video...!", Snackbar.LENGTH_SHORT);
                    } else {

                        if (postGalleryFrament.selectedImageList.size() < 1 || postGalleryFrament.selectedImageList.size() == 1) {
                            try {
                                Log.d(tag, "----filter--");
                                fillterImage();
                            } catch (Exception e) {
                                Log.d(tag, "----exception--" + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(tag, "----multiple images--");
                            uploadImagesDialogBox(postGalleryFrament.selectedImageList);
                        }

                    }
                } else if (selected_type == 1) {
                    uploadImagesDialogBox(postGalleryFrament.selectedImageList);
                }

            }
        });

    }

    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    public void fillterImage() throws Exception {
        if (postGalleryFrament.is_crop) {
            Bitmap resultbitmap = postGalleryFrament.fragmentPostGalleryBinding.cropImageView.getCroppedImage();
          //  postGalleryFrament.selected_file_url = bitmapToUriConverter(resultbitmap).toString();
        }


        Intent intent = new ImageEditorIntentBuilder(this, al_images.get(selected_folder).getAl_imagepath().get(postGalleryFrament.selected_position), al_images.get(selected_folder).getAl_imagepath().get(postGalleryFrament.selected_position))
                .withAddText()
                .withFilterFeature()
                .withRotateFeature()
                .withCropFeature()
                .withBrightnessFeature()
                .withSaturationFeature()
                .withBeautyFeature()
                .forcePortrait(true)
                .setSupportActionBarVisibility(false)
                .build();
        // .withStickerFeature()
        //  .withPaintFeature()
        EditImageActivity.start(this, intent, ACTION_REQUEST_EDITIMAGE);
    }

    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(getApplicationContext().getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getApplicationContext().openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Contrants.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        } else {
            getImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    getImages();
                    adapter.addFragment(new PhotoFragment(), "PHOTO");
                    adapter.addFragment(new VideoFragment(), "VIDEO");
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GalleryActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    public void getImages() {
        Gallery gallery = new Gallery();
        gallery.getImages(getApplicationContext());

        al_images = Gallery.al_images;

        SpinnerBaseAdapter spinnerBaseAdapter = new SpinnerBaseAdapter(getApplicationContext(), al_images);
        galleryBinding.inculdeGalleryToolBar.spinnerShow.setAdapter(spinnerBaseAdapter);
        // galleryBinding.gridView.setHasFixedSize(true)
        if (al_images.get(0).getAl_imagepath() != null) {
            selected_type = al_images.get(0).getType();
            postGalleryFrament.setList(al_images.get(0).getAl_imagepath(), al_images.get(0).getType());
        }

    }

    ArrayList<String> selected_image = new ArrayList<>();
    int selected_position;
    String selected_file_url;


    private void setupViewPager() {


//        galleryBinding.gallery.getTabAt(1);
//        galleryBinding.gallery.getTabAt(2);
        postGalleryFrament = new PostGalleryFragment();
        photofragment = new PhotoFragment();
        photofragment.passActivity(GalleryActivity.this);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(postGalleryFrament, "GALLERY");
        int permissionCamer = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCamer == PackageManager.PERMISSION_GRANTED) {
            adapter.addFragment(photofragment, "PHOTO/VIDEO");
            //adapter.addFragment(new VideoFragment(), "VIDEO");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Contrants.REQUEST_CAMERA_PERMISSION);

        }


        galleryBinding.galleryViewPager.setAdapter(adapter);
        galleryBinding.gallery.setupWithViewPager(galleryBinding.galleryViewPager);


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
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_REQUEST_EDITIMAGE) { // same code you used while starting
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
            if (isImageEdit) {
                // Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
                Log.d(tag, "----save edit image--" + getString(R.string.save_path, newFilePath));
                HashMap<Integer, String> list = new HashMap<>();
                list.put(0, newFilePath);
                uploadImagesDialogBox(list);
            } else {
                newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
                HashMap<Integer, String> list = new HashMap<>();
                list.put(0, newFilePath);
                uploadImagesDialogBox(list);
                Log.d(tag, "----save edit image  false--" + getString(R.string.save_path, newFilePath));
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                postGalleryFrament.selected_file_url = resultUri.toString();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void uploadImagesDialogBox(HashMap<Integer, String> newFilePath) {
        ArrayList<String> list_of_images_video = new ArrayList<>();
        for (String value : newFilePath.values()) {
            list_of_images_video.add(value);
        }
        Bundle bundle = new Bundle();
        UploadImagesDialogBoxs instance = UploadImagesDialogBoxs.getInstance(bundle, GalleryActivity.this);
        bundle.putStringArrayList("list", list_of_images_video);
        instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GalleryActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}