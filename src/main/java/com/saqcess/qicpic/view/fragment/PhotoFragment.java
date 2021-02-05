package com.saqcess.qicpic.view.fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.RunTimePermission;
import com.saqcess.qicpic.databinding.FragmentPhotoBinding;
import com.saqcess.qicpic.view.activity.ChatGalleryActivity;
import com.saqcess.qicpic.view.activity.GalleryActivity;
import com.saqcess.qicpic.view.dialog.UploadImagesDialogBoxs;
import com.saqcess.qicpic.viewmodel.PhotoViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;

import static com.saqcess.qicpic.view.activity.GalleryActivity.ACTION_REQUEST_EDITIMAGE;

public class PhotoFragment extends Fragment implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private String tag = "PhotoFragment";
    //  private CameraPreview mPreview;
    private Camera camera;
    private boolean cameraFront;
    private Camera.PictureCallback pictureCallback;
    private String path = "";


    private SurfaceHolder surfaceHolder;
    private Handler customHandler = new Handler();
    int flag = 0;
    private File tempFile = null;
    private Camera.PictureCallback jpegCallback;
    int MAX_VIDEO_SIZE_UPLOAD = 10; //MB
    private MediaRecorder mediaRecorder;
    private SurfaceView imgSurface;
    private ImageView imgCapture;
    private ImageView imgFlashOnOff;
    private ImageView imgSwipeCamera;
    private RunTimePermission runTimePermission;
    private TextView textCounter;
    private TextView hintTextView;
    private ChatGalleryActivity chatActivity;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance(String param1, String param2) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FragmentPhotoBinding photoBinding;
    PhotoViewModel photoViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        photoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo, container, false);
        photoViewModel = ViewModelProviders.of(getActivity()).get(PhotoViewModel.class);
        photoBinding.setLifecycleOwner(getActivity());
        photoBinding.setPhot(photoViewModel);
        view = photoBinding.getRoot();
        //initUI();
        init(view);
        return view;
    }

    //    private void initUI() {
//        if (checkCameraHardware(getContext())) {
//            camera = getCameraInstance();
//            mPreview = new CameraPreview(getContext(), camera);
//            photoBinding.camerLayer.addView(mPreview);
//            // setParameter();
//            pictureCallback = getPictureCallback();
//        }
//
//        photoBinding.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (camera != null) {
//                    Log.d(tag,"---take photo-camera");
//                    camera.takePicture(null, null, pictureCallback);
//                }
//            }
//        });
//
//        photoBinding.ivCameraSide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                releaseCamera();
//                chooseCamera();
//            }
//        });
//        photoBinding.ivFlashOnOff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                flashToggle();
//            }
//        });
//    }
//    int flashType = 1;
//    private void flashToggle() {
//
//        if (flashType == 1) {
//
//            flashType = 2;
//        } else if (flashType == 2) {
//
//            flashType = 3;
//        } else if (flashType == 3) {
//
//            flashType = 1;
//        }
//        refreshCamera();
//    }
//    int flag = 0;
//    public void refreshCamera() {
//
//        if (mPreview == null) {
//            return;
//        }
//        try {
//            camera.stopPreview();
//            Camera.Parameters param = camera.getParameters();
//
//            if (flag == 0) {
//                if (flashType == 1) {
//                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//                    photoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_auto);
//                } else if (flashType == 2) {
//                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                    Camera.Parameters params = null;
//                    if (camera != null) {
//                        params = camera.getParameters();
//
//                        if (params != null) {
//                            List<String> supportedFlashModes = params.getSupportedFlashModes();
//
//                            if (supportedFlashModes != null) {
//                                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
//                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                                } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
//                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//                                }
//                            }
//                        }
//                    }
//                    photoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_on);
//                } else if (flashType == 3) {
//                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                    photoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_off);
//                }
//            }
//
//
//            refrechCameraPriview(param);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private void refrechCameraPriview(Camera.Parameters param) {
//        try {
//            camera.setParameters(param);
//            setCameraDisplayOrientation(0);
//            mPreview.refreshCamera(camera);
//          //  camera.startPreview();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    public void chooseCamera() {
//        //if the camera preview is the front
//        if (cameraFront) {
//            int cameraId = findBackFacingCamera();
//            if (cameraId >= 0) {
//                //open the backFacingCamera
//                //set a picture callback
//                //refresh the preview
//
//                camera = Camera.open(cameraId);
//               setCameraDisplayOrientation(0);
//                pictureCallback = getPictureCallback();
//                mPreview.refreshCamera(camera);
//            }
//        } else {
//            int cameraId = findFrontFacingCamera();
//            if (cameraId >= 0) {
//                //open the backFacingCamera
//                //set a picture callback
//                //refresh the preview
//
//                camera = Camera.open(cameraId);
//                setCameraDisplayOrientation(0);
//                pictureCallback = getPictureCallback();
//                mPreview.refreshCamera(camera);
//            }
//        }
//    }
//
//    public void setCameraDisplayOrientation(int cameraId) {
//
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//
//        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
//
//        if (Build.MODEL.equalsIgnoreCase("Nexus 6") && flag == 1) {
//            rotation = Surface.ROTATION_180;
//        }
//        int degrees = 0;
//        switch (rotation) {
//
//            case Surface.ROTATION_0:
//
//                degrees = 0;
//                break;
//
//            case Surface.ROTATION_90:
//
//                degrees = 90;
//                break;
//
//            case Surface.ROTATION_180:
//
//                degrees = 180;
//                break;
//
//            case Surface.ROTATION_270:
//
//                degrees = 270;
//                break;
//
//        }
//
//        int result;
//
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360; // compensate the mirror
//
//        } else {
//            result = (info.orientation - degrees + 360) % 360;
//
//        }
//
//        camera.setDisplayOrientation(result);
//
//    }
//
//    private int findBackFacingCamera() {
//        int cameraId = -1;
//        //Search for the back facing camera
//        //get the number of cameras
//        int numberOfCameras = Camera.getNumberOfCameras();
//        //for every camera check
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                cameraId = i;
//                cameraFront = false;
//                photoBinding.ivCameraSide.setBackgroundResource(R.drawable.ic__camer_rear);
//                break;
//
//            }
//
//        }
//        return cameraId;
//    }
//
//    private int findFrontFacingCamera() {
//
//        int cameraId = -1;
//        // Search for the front facing camera
//        int numberOfCameras = Camera.getNumberOfCameras();
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                cameraId = i;
//                cameraFront = true;
//                photoBinding.ivCameraSide.setBackgroundResource(R.drawable.ic__camer_back);
//                break;
//            }
//        }
//        return cameraId;
//
//    }
//
//
//
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }
//
//    private static Camera getCameraInstance() {
//        Camera camera1;
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= 9) {
//                return Camera.open();
//            }
//            camera1 = Camera.open();
//        } catch (Exception exception) {
//            return null;
//        }
//        return camera1;
//    }
//
//    @Override
//    public void onPictureTaken(byte[] bytes, Camera camera) {
//
//    }
//
//
//    public static final int ACTION_REQUEST_EDITIMAGE = 9;
//
    public void fillterImage(String path, File file) throws Exception {
        Log.d(tag, "----filter photo--" + file.getAbsolutePath());


        Intent intent = new ImageEditorIntentBuilder(getContext(), file.getAbsolutePath(), file.getAbsolutePath())
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
        EditImageActivity.start(getActivity(), intent, ACTION_REQUEST_EDITIMAGE);
    }

    //
//
//    public Camera.PictureCallback getPictureCallback() {
//        Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//                Log.d(tag,"----take photo--");
//                if (pictureFile == null) {
//                    Log.d(tag, "Error creating media file, check storage permissions");
//                    return;
//                }
//
//                try {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                    FileOutputStream fos = new FileOutputStream(pictureFile);
//                    fos.write(data);
//                    fos.close();
//                    fillterImage(path,pictureFile);
//                } catch (FileNotFoundException e) {
//                    Log.d(tag, "File not found: " + e.getMessage());
//                } catch (IOException e) {
//                    Log.d(tag, "Error accessing file: " + e.getMessage());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.d(tag, "Error ex file: " + e.getMessage());
//                }
//            }
//        };
//        return pictureCallback;
//    }
//
//    public void onResume() {
//
//        super.onResume();
//        if (camera == null) {
//            camera = Camera.open();
//            camera.setDisplayOrientation(90);
//            pictureCallback = getPictureCallback();
//            mPreview.refreshCamera(camera);
//            Log.d("nu", "null");
//        } else {
//            Log.d("nu", "no null");
//        }
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        releaseCamera();
//    }
//
//    private void releaseCamera() {
//        // stop and release camera
//        if (camera != null) {
//            camera.stopPreview();
//            camera.setPreviewCallback(null);
//            camera.release();
//            camera = null;
//        }
//    }
//
//    private static File getOutputMediaFile(int type) {
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "OzoGram");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
//    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
//        private SurfaceHolder mHolder;
//        private Camera mCamera;
//
//        public CameraPreview(Context context, Camera camera) {
//            super(context);
//            mCamera = camera;
//            mCamera.setDisplayOrientation(90);
//            mHolder = getHolder();
//            mHolder.addCallback(this);
//            // deprecated setting, but required on Android versions prior to 3.0
//            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        }
//
//
//
//        @Override
//        public void surfaceCreated(@NonNull SurfaceHolder holder) {
//            try {
//                mCamera.setPreviewDisplay(holder);
//                mCamera.startPreview();
//            } catch (IOException e) {
//                Log.d(tag, "Error setting camera preview: " + e.getMessage());
//            }
//        }
//
//        @Override
//        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            refreshCamera(mCamera);
//        }
//
//        @Override
//        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
//            mCamera.release();
//        }
//
//        public void refreshCamera(Camera camera) {
//            if (mHolder.getSurface() == null) {
//                // preview surface does not exist
//                return;
//            }
//            // stop preview before making changes
//            try {
//                mCamera.stopPreview();
//            } catch (Exception e) {
//                // ignore: tried to stop a non-existent preview
//            }
//            // set preview size and make any resize, rotate or
//            // reformatting changes here
//            // start preview with new settings
//            setCamera(camera);
//            try {
//                mCamera.setPreviewDisplay(mHolder);
//                mCamera.startPreview();
//            } catch (Exception e) {
//                Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
//            }
//        }
//
//        public void setCamera(Camera camera) {
//            //method to set a camera instance
//            mCamera = camera;
//        }
//    }

    private void init(View view) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        runTimePermission = new RunTimePermission(getActivity());
        runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new RunTimePermission.RunTimePermissionListener() {

            @Override
            public void permissionGranted() {
                // First we need to check availability of play services
                initControls(view);

                identifyOrientationEvents();

                //create a folder to get image
                folder = new File(Environment.getExternalStorageDirectory() + "/whatsappCamera");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                //capture image on callback
                captureImageCallback();
                //
                if (camera != null) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        imgFlashOnOff.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void permissionDenied() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (myOrientationEventListener != null)
                myOrientationEventListener.enable();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private File folder = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void cancelSavePicTaskIfNeed() {
        if (savePicTask != null && savePicTask.getStatus() == AsyncTask.Status.RUNNING) {
            savePicTask.cancel(true);
        }
    }

    private void cancelSaveVideoTaskIfNeed() {
        if (saveVideoTask != null && saveVideoTask.getStatus() == AsyncTask.Status.RUNNING) {
            saveVideoTask.cancel(true);
        }
    }

    private SavePicTask savePicTask;
    GalleryActivity activity;

    public void passActivity(GalleryActivity galleryActivity) {
        activity = galleryActivity;
    }
    public void passChatActivity(ChatGalleryActivity galleryActivity) {
        chatActivity = galleryActivity;
    }

    private class SavePicTask extends AsyncTask<Void, Void, String> {
        private byte[] data;
        private int rotation = 0;

        public SavePicTask(byte[] data, int rotation) {
            this.data = data;
            this.rotation = rotation;
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                return saveToSDCard(data, rotation);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            activeCameraCapture();

            tempFile = new File(result);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        fillterImage(tempFile.toString(), tempFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, 100);


        }
    }

    public String saveToSDCard(byte[] data, int rotation) throws IOException {
        String imagePath = "";
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int reqHeight = metrics.heightPixels;
            int reqWidth = metrics.widthPixels;

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (rotation != 0) {
                Matrix mat = new Matrix();
                mat.postRotate(rotation);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
                if (bitmap != bitmap1) {
                    bitmap.recycle();
                }
                imagePath = getSavePhotoLocal(bitmap1);
                if (bitmap1 != null) {
                    bitmap1.recycle();
                }
            } else {
                imagePath = getSavePhotoLocal(bitmap);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private String getSavePhotoLocal(Bitmap bitmap) {
        String path = "";
        try {
            OutputStream output;
            File file = new File(folder.getAbsolutePath(), "wc" + System.currentTimeMillis() + ".jpg");
            try {
                output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                path = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private void captureImageCallback() {

        surfaceHolder = imgSurface.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

                refreshCamera();

                cancelSavePicTaskIfNeed();
                savePicTask = new SavePicTask(data, getPhotoRotation());
                savePicTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

            }
        };
    }

    private class SaveVideoTask extends AsyncTask<Void, Void, Void> {

        File thumbFilename;

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Processing a video...");
            progressDialog.show();
            super.onPreExecute();
            imgCapture.setOnTouchListener(null);
            textCounter.setVisibility(View.GONE);
            imgSwipeCamera.setVisibility(View.VISIBLE);
            imgFlashOnOff.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                try {
                    myOrientationEventListener.enable();

                    customHandler.removeCallbacksAndMessages(null);

                    mediaRecorder.stop();
                    releaseMediaRecorder();

                    tempFile = new File(folder.getAbsolutePath() + "/" + mediaFileName + ".mp4");
                    thumbFilename = new File(folder.getAbsolutePath(), "t_" + mediaFileName + ".jpeg");
                    generateVideoThmb(tempFile.getPath(), thumbFilename);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            if (tempFile != null && thumbFilename != null)
                onVideoSendDialog(tempFile.getAbsolutePath(), thumbFilename.getAbsolutePath());
        }
    }

    private int mPhotoAngle = 90;

    private void identifyOrientationEvents() {

        myOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int iAngle) {

                final int iLookup[] = {0, 0, 0, 90, 90, 90, 90, 90, 90, 180, 180, 180, 180, 180, 180, 270, 270, 270, 270, 270, 270, 0, 0, 0}; // 15-degree increments
                if (iAngle != ORIENTATION_UNKNOWN) {

                    int iNewOrientation = iLookup[iAngle / 15];
                    if (iOrientation != iNewOrientation) {
                        iOrientation = iNewOrientation;
                        if (iOrientation == 0) {
                            mOrientation = 90;
                        } else if (iOrientation == 270) {
                            mOrientation = 0;
                        } else if (iOrientation == 90) {
                            mOrientation = 180;
                        }

                    }
                    mPhotoAngle = normalize(iAngle);
                }
            }
        };

        if (myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.enable();
        }

    }

    private void initControls(View view) {

        mediaRecorder = new MediaRecorder();

        imgSurface = (SurfaceView) view.findViewById(R.id.imgSurface);
        textCounter = (TextView) view.findViewById(R.id.textCounter);
        imgCapture = (ImageView) view.findViewById(R.id.imgCapture);
        imgFlashOnOff = (ImageView) view.findViewById(R.id.imgFlashOnOff);
        imgSwipeCamera = (ImageView) view.findViewById(R.id.imgChangeCamera);
        textCounter.setVisibility(View.GONE);

        hintTextView = (TextView) view.findViewById(R.id.hintTextView);

        imgSwipeCamera.setOnClickListener(this);
        activeCameraCapture();

        imgFlashOnOff.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlashOnOff:
                flashToggle();
                break;
            case R.id.imgChangeCamera:
                camera.stopPreview();
                camera.release();
                if (flag == 0) {
                    imgFlashOnOff.setVisibility(View.GONE);
                    flag = 1;
                } else {
                    imgFlashOnOff.setVisibility(View.VISIBLE);
                    flag = 0;
                }
                surfaceCreated(surfaceHolder);
                break;
            default:
                break;
        }
    }

    private void flashToggle() {

        if (flashType == 1) {
            flashType = 2;
        } else if (flashType == 2) {
            flashType = 3;
        } else if (flashType == 3) {
            flashType = 1;
        }

        refreshCamera();
    }

    private void captureImage() {
        camera.takePicture(null, null, jpegCallback);
        inActiveCameraCapture();
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = new MediaRecorder();
        }
    }


    public void refreshCamera() {

        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
            Camera.Parameters param = camera.getParameters();

            if (flag == 0) {
                if (flashType == 1) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_auto);
                } else if (flashType == 2) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    Camera.Parameters params = null;
                    if (camera != null) {
                        params = camera.getParameters();

                        if (params != null) {
                            List<String> supportedFlashModes = params.getSupportedFlashModes();

                            if (supportedFlashModes != null) {
                                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                                }
                            }
                        }
                    }
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);
                } else if (flashType == 3) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
                }
            }


            refrechCameraPriview(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refrechCameraPriview(Camera.Parameters param) {
        try {
            camera.setParameters(param);
            setCameraDisplayOrientation(0);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCameraDisplayOrientation(int cameraId) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        if (Build.MODEL.equalsIgnoreCase("Nexus 6") && flag == 1) {
            rotation = Surface.ROTATION_180;
        }
        int degrees = 0;
        switch (rotation) {

            case Surface.ROTATION_0:

                degrees = 0;
                break;

            case Surface.ROTATION_90:

                degrees = 90;
                break;

            case Surface.ROTATION_180:

                degrees = 180;
                break;

            case Surface.ROTATION_270:

                degrees = 270;
                break;

        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror

        } else {
            result = (info.orientation - degrees + 360) % 360;

        }

        camera.setDisplayOrientation(result);

    }

    //------------------SURFACE CREATED FIRST TIME--------------------//

    int flashType = 1;

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        try {
            if (flag == 0) {
                camera = Camera.open(0);
            } else {
                camera = Camera.open(1);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        try {
            Camera.Parameters param;
            param = camera.getParameters();
            List<Camera.Size> sizes = param.getSupportedPreviewSizes();
            //get diff to get perfact preview sizes
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            long diff = (height * 1000 / width);
            long cdistance = Integer.MAX_VALUE;
            int idx = 0;
            for (int i = 0; i < sizes.size(); i++) {
                long value = (long) (sizes.get(i).width * 1000) / sizes.get(i).height;
                if (value > diff && value < cdistance) {
                    idx = i;
                    cdistance = value;
                }
                Log.e("WHHATSAPP", "width=" + sizes.get(i).width + " height=" + sizes.get(i).height);
            }
            Log.e("WHHATSAPP", "INDEX:  " + idx);
            Camera.Size cs = sizes.get(idx);
            param.setPreviewSize(cs.width, cs.height);
            param.setPictureSize(cs.width, cs.height);
            camera.setParameters(param);
            setCameraDisplayOrientation(0);

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            if (flashType == 1) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_auto);

            } else if (flashType == 2) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                Camera.Parameters params = null;
                if (camera != null) {
                    params = camera.getParameters();

                    if (params != null) {
                        List<String> supportedFlashModes = params.getSupportedFlashModes();

                        if (supportedFlashModes != null) {
                            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                            }
                        }
                    }
                }
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_on);

            } else if (flashType == 3) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                imgFlashOnOff.setImageResource(R.drawable.ic_flash_off);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        try {
            camera.stopPreview();
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera();
    }

    //------------------SURFACE OVERRIDE METHIDS END--------------------//

    private long timeInMilliseconds = 0L, startTime = SystemClock.uptimeMillis(), updatedTime = 0L, timeSwapBuff = 0L;
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;

            secs = secs % 60;
            textCounter.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }

    };

    private void scaleUpAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 2f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 2f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) imgCapture.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    private void scaleDownAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imgCapture, "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imgCapture, "scaleY", 1f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                View p = (View) imgCapture.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        try {

            if (customHandler != null)
                customHandler.removeCallbacksAndMessages(null);

            releaseMediaRecorder();       // if you are using MediaRecorder, release it first

            if (myOrientationEventListener != null)
                myOrientationEventListener.enable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SaveVideoTask saveVideoTask = null;

    private void activeCameraCapture() {
        if (imgCapture != null) {
            imgCapture.setAlpha(1.0f);
            imgCapture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    hintTextView.setVisibility(View.INVISIBLE);
                    try {
                        if (prepareMediaRecorder()) {
                            myOrientationEventListener.disable();
                            mediaRecorder.start();
                            startTime = SystemClock.uptimeMillis();
                            customHandler.postDelayed(updateTimerThread, 0);
                        } else {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    textCounter.setVisibility(View.VISIBLE);
                    imgSwipeCamera.setVisibility(View.GONE);
                    imgFlashOnOff.setVisibility(View.GONE);
                    scaleUpAnimation();
                    imgCapture.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                                return true;
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {

                                scaleDownAnimation();
                                hintTextView.setVisibility(View.VISIBLE);

                                cancelSaveVideoTaskIfNeed();
                                saveVideoTask = new SaveVideoTask();
                                saveVideoTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                                return true;
                            }
                            return true;

                        }
                    });
                    return true;
                }

            });
            imgCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSpaceAvailable()) {
                        captureImage();
                    } else {
                        Toast.makeText(getActivity(), "Memory is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void onVideoSendDialog(final String videopath, final String thumbPath) {

        getActivity().runOnUiThread(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                if (videopath != null) {
                    File fileVideo = new File(videopath);
                    long fileSizeInBytes = fileVideo.length();
                    long fileSizeInKB = fileSizeInBytes / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;
                    if (fileSizeInMB > MAX_VIDEO_SIZE_UPLOAD) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(getString(R.string.file_limit_size_upload_format, MAX_VIDEO_SIZE_UPLOAD))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        ArrayList<String> list_of_images_video = new ArrayList<>();
                        list_of_images_video.add(videopath);
                        Bundle bundle = new Bundle();
                        if(activity!=null){
                            UploadImagesDialogBoxs instance = UploadImagesDialogBoxs.getInstance(bundle, activity);
                            bundle.putStringArrayList("list", list_of_images_video);
                            instance.show(getChildFragmentManager(), instance.getClass().getSimpleName());
                        }else{

                        }

                    }
                }
            }
        });
    }

    private void inActiveCameraCapture() {
        if (imgCapture != null) {
            imgCapture.setAlpha(0.5f);
            imgCapture.setOnClickListener(null);
        }
    }

    //--------------------------CHECK FOR MEMORY -----------------------------//

    public int getFreeSpacePercantage() {
        int percantage = (int) (freeMemory() * 100 / totalMemory());
        int modValue = percantage % 5;
        return percantage - modValue;
    }

    public double totalMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getBlockCount() * (double) stat.getBlockSize();
        return sdAvailSize / 1073741824;
    }

    public double freeMemory() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        return sdAvailSize / 1073741824;
    }

    public boolean isSpaceAvailable() {
        if (getFreeSpacePercantage() >= 1) {
            return true;
        } else {
            return false;
        }
    }
    //-------------------END METHODS OF CHECK MEMORY--------------------------//


    private String mediaFileName = null;

    @SuppressLint("SimpleDateFormat")
    protected boolean prepareMediaRecorder() throws IOException {

        mediaRecorder = new MediaRecorder(); // Works well
        camera.stopPreview();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (flag == 1) {
            mediaRecorder.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH));
        } else {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        }
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        mediaRecorder.setOrientationHint(mOrientation);

        if (Build.MODEL.equalsIgnoreCase("Nexus 6") && flag == 1) {

            if (mOrientation == 90) {
                mediaRecorder.setOrientationHint(mOrientation);
            } else if (mOrientation == 180) {
                mediaRecorder.setOrientationHint(0);
            } else {
                mediaRecorder.setOrientationHint(180);
            }

        } else if (mOrientation == 90 && flag == 1) {
            mediaRecorder.setOrientationHint(270);
        } else if (flag == 1) {
            mediaRecorder.setOrientationHint(mOrientation);
        }
        mediaFileName = "wc_vid_" + System.currentTimeMillis();
        mediaRecorder.setOutputFile(folder.getAbsolutePath() + "/" + mediaFileName + ".mp4"); // Environment.getExternalStorageDirectory()

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

            public void onInfo(MediaRecorder mr, int what, int extra) {
                // TODO Auto-generated method stub

                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {

                    long downTime = 0;
                    long eventTime = 0;
                    float x = 0.0f;
                    float y = 0.0f;
                    int metaState = 0;
                    MotionEvent motionEvent = MotionEvent.obtain(
                            downTime,
                            eventTime,
                            MotionEvent.ACTION_UP,
                            0,
                            0,
                            metaState
                    );

                    imgCapture.dispatchTouchEvent(motionEvent);

                    Toast.makeText(getActivity(), "You reached to Maximum(25MB) video size.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mediaRecorder.setMaxFileSize(1000 * 25 * 1000);

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        }
        return true;

    }

    OrientationEventListener myOrientationEventListener;
    int iOrientation = 0;
    int mOrientation = 90;

    public void generateVideoThmb(String srcFilePath, File destFile) {
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(srcFilePath, 120);
            FileOutputStream out = new FileOutputStream(destFile);
            ThumbnailUtils.extractThumbnail(bitmap, 200, 200).compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int normalize(int degrees) {
        if (degrees > 315 || degrees <= 45) {
            return 0;
        }

        if (degrees > 45 && degrees <= 135) {
            return 90;
        }

        if (degrees > 135 && degrees <= 225) {
            return 180;
        }

        if (degrees > 225 && degrees <= 315) {
            return 270;
        }

        throw new RuntimeException("Error....");
    }

    private int getPhotoRotation() {
        int rotation;
        int orientation = mPhotoAngle;

        Camera.CameraInfo info = new Camera.CameraInfo();
        if (flag == 0) {
            Camera.getCameraInfo(0, info);
        } else {
            Camera.getCameraInfo(1, info);
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else {
            rotation = (info.orientation + orientation) % 360;
        }
        return rotation;
    }

}