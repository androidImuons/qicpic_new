package com.saqcess.qicpic.view.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.FileUtils;

import java.io.File;

import iamutkarshtiwari.github.io.ananas.BaseActivity;
import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;


public class FilterActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PERMISSON_SORAGE = 1;
    public static final int REQUEST_PERMISSON_CAMERA = 2;

    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;

    private ImageView imgView;
    private Bitmap mainBitmap;
    private Dialog loadingDialog;

    private int imageWidth, imageHeight;
    private String path;
    private File photoFile = null;
    //private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        initView();
    }

    private void initView() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        imgView = findViewById(R.id.img);

        View selectAlbum = findViewById(R.id.select_album);
        View editImage = findViewById(R.id.edit_image);
        selectAlbum.setOnClickListener(this);
        editImage.setOnClickListener(this);

        View takenPhoto = findViewById(R.id.take_photo);
        takenPhoto.setOnClickListener(this);

        loadingDialog = BaseActivity.getLoadingDialog(this, R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                takePhotoClick();
                break;
            case R.id.edit_image:
                editImageClick();
                break;
            case R.id.select_album:
                selectFromAblum();
                break;
        }
    }
    protected void takePhotoClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestTakePhotoPermissions();
        } else {
            launchCamera();
        }
    }

    private void requestTakePhotoPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSON_CAMERA);
            return;
        }
        launchCamera();
    }
    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            photoFile = FileUtils.genEditFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        } else {
            photoFile = FileUtils.genEditFile();
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PHOTO_CODE);
        }
    }

    private void editImageClick() {
        File outputFile = FileUtils.genEditFile();
        try {

            Intent intent = new ImageEditorIntentBuilder(this, path, outputFile.getAbsolutePath())
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
        } catch (Exception e) {
            Toast.makeText(this, R.string.iamutkarshtiwari_github_io_ananas_not_selected, Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

    private void selectFromAblum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openAblumWithPermissionsCheck();
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
       /* FilterActivity.this.startActivityForResult(new Intent(
                        FilterActivity.this, SelectPictureActivity.class),
                SELECT_GALLERY_IMAGE_CODE);*/
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_GALLERY_IMAGE_CODE);
    }

    private void openAblumWithPermissionsCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSON_SORAGE);
            return;
        }
        openAlbum();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSON_SORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAlbum();
        } else if (requestCode == REQUEST_PERMISSON_CAMERA
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
           /* switch (requestCode) {
                case SELECT_GALLERY_IMAGE_CODE:
                    handleSelectFromAblum(data);
                    break;
                case TAKE_PHOTO_CODE:
                    handleTakePhoto();
                    break;
                case ACTION_REQUEST_EDITIMAGE:
                    handleEditorImage(data);
                    break;
            }*/
        }
        if (requestCode == SELECT_GALLERY_IMAGE_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            path = picturePath;
            cursor.close();
            imgView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        if (requestCode == ACTION_REQUEST_EDITIMAGE) { // same code you used while starting
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
            if (isImageEdit) {
                Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
                imgView.setImageBitmap(BitmapFactory.decodeFile(newFilePath));
            } else {
                newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
                imgView.setImageURI(Uri.parse(newFilePath));
            }

        }

    }

  /*  private void handleTakePhoto() {
        if (photoFile != null) {
            path = photoFile.getPath();
            loadImage(path);
        }
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);

        if (isImageEdit) {
            Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
        } else {
            newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);

        }

        loadImage(newFilePath);
    }

    private void handleSelectFromAblum(Intent data) {
        path = data.getStringExtra("imgPath");
        loadImage(path);
    }*/

   /* private void loadImage(String imagePath) {
        compositeDisposable.clear();
        Disposable applyRotationDisposable = loadBitmapFromFile(imagePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(
                        this::setMainBitmap,
                        e -> { e.printStackTrace();
                            Toast.makeText(
                                    this, R.string.iamutkarshtiwari_github_io_ananas_load_error, Toast.LENGTH_SHORT).show();}
                );

        compositeDisposable.add(applyRotationDisposable);
    }

    private void setMainBitmap(Bitmap sourceBitmap) {
        if (mainBitmap != null) {
            mainBitmap.recycle();
            mainBitmap = null;
            System.gc();
        }
        mainBitmap = sourceBitmap;
        imgView.setImageBitmap(mainBitmap);
    }

    private Single<Bitmap> loadBitmapFromFile(String filePath) {
        return Single.fromCallable(() ->
                BitmapUtils.getSampledBitmap(
                        filePath,
                        imageWidth / 4,
                        imageHeight / 4
                )
        );
    }*/


}
