package com.saqcess.qicpic.view.fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.os.SystemClock;
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
import android.widget.Toast;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.RunTimePermission;
import com.saqcess.qicpic.databinding.FragmentVideoBinding;
import com.saqcess.qicpic.viewmodel.VideoFragmentViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    VideoFragmentViewModel videoFragmentViewModel;
    FragmentVideoBinding videoBinding;
    private View view;
    private String tag = "VideoFragment";
    private CameraPreview mPreview;
    private SaveVideoTask saveVideoTask;

    public VideoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RunTimePermission runTimePermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        videoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);
        videoFragmentViewModel = ViewModelProviders.of(getActivity()).get(VideoFragmentViewModel.class);
        videoBinding.setVideo(videoFragmentViewModel);
        view = videoBinding.getRoot();
        initUI();
        return view;
    }

    private Camera camera;

    private void initUI() {

        if (checkCameraHardware(getContext())) {

        }

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        runTimePermission = new RunTimePermission(getActivity());
        runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, new RunTimePermission.RunTimePermissionListener() {
            @Override
            public void permissionGranted() {
                initControls(view);

                identifyOrientationEvents();
                folder = new File(Environment.getExternalStorageDirectory() + "/whatsappCamera");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                //capture image on callback
                //  captureImageCallback();
                //
                if (camera != null) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        videoBinding.ivFlashOnOff.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void permissionDenied() {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (runTimePermission != null) {
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void initControls(View view) {

        mediaRecorder = new MediaRecorder();

        camera = getCameraInstance();
        mPreview = new CameraPreview(getContext(), camera);
        videoBinding.camerLayer.addView(mPreview);

        activeCameraCapture();


        videoBinding.ivFlashOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        videoBinding.ivCameraSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseCamera();
                chooseCamera();
            }
        });

    }

    int flashType = 1;

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

    public void refreshCamera() {

        if (mPreview == null) {
            return;
        }
        try {
            camera.stopPreview();
            Camera.Parameters param = camera.getParameters();

            if (flag == 0) {
                if (flashType == 1) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    videoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_auto);
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
                    videoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_on);
                } else if (flashType == 3) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    videoBinding.ivFlashOnOff.setImageResource(R.drawable.ic_flash_off);
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
            mPreview.refreshCamera(camera);
            //  camera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean cameraFront;

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                camera = Camera.open(cameraId);
                setCameraDisplayOrientation(0);
                mPreview.refreshCamera(camera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                camera = Camera.open(cameraId);
                setCameraDisplayOrientation(0);
                mPreview.refreshCamera(camera);
            }
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

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                videoBinding.ivCameraSide.setBackgroundResource(R.drawable.ic__camer_rear);
                break;

            }

        }
        return cameraId;
    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                videoBinding.ivCameraSide.setBackgroundResource(R.drawable.ic__camer_back);
                break;
            }
        }
        return cameraId;

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

    private static Camera getCameraInstance() {
        Camera camera1;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 9) {
                return Camera.open();
            }
            camera1 = Camera.open();
        } catch (Exception exception) {
            return null;
        }
        return camera1;
    }

    private long timeInMilliseconds = 0L, startTime = SystemClock.uptimeMillis(), updatedTime = 0L, timeSwapBuff = 0L;

    private void activeCameraCapture() {
        if (videoBinding.btnTakePhoto != null) {
            videoBinding.btnTakePhoto.setAlpha(0.0f);
            videoBinding.btnTakePhoto.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    videoBinding.hintTextView.setVisibility(View.INVISIBLE);
                    try {
                        if (prepareMediaRecorder()) {
                            myOrientationEventListener.disable();
                            mediaRecorder.start();
                            startTime = SystemClock.uptimeMillis();
                            customHandler.postDelayed(updateTimerThread, 0);
                        } else {
                            Log.d(tag, "----prepare media false--");
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    videoBinding.textCounter.setVisibility(View.VISIBLE);
                    videoBinding.ivFlashOnOff.setVisibility(View.VISIBLE);
                    scaleUpAnimation();
                    videoBinding.btnTakePhoto.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                                return true;
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {

                                scaleDownAnimation();
                                videoBinding.hintTextView.setVisibility(View.VISIBLE);

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

        }


    }

    private void cancelSaveVideoTaskIfNeed() {
        if (saveVideoTask != null && saveVideoTask.getStatus() == AsyncTask.Status.RUNNING) {
            saveVideoTask.cancel(true);
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;

            secs = secs % 60;
            videoBinding.textCounter.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);

        }

    };

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    OrientationEventListener myOrientationEventListener;
    int iOrientation = 0;
    int mOrientation = 90;
    private Handler customHandler = new Handler();
    private MediaRecorder mediaRecorder;

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

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = new MediaRecorder();
        }
    }

    private File tempFile = null;
    private File folder = null;
    private String mediaFileName = null;

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

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

    int MAX_VIDEO_SIZE_UPLOAD = 10; //MB
    int flag = 0;

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
                        Log.d(tag, " run redirect--" + videopath.toString());
                        Log.d(tag, "-run file--" + thumbPath.toString());
//                        Intent mIntent = new Intent(getActivity(), PhotoVideoRedirectActivity.class);
//                        mIntent.putExtra("PATH", videopath.toString());
//                        mIntent.putExtra("THUMB", thumbPath.toString());
//                        mIntent.putExtra("WHO", "Video");
//                        startActivity(mIntent);


                    }
                }
            }
        });
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

            videoBinding.textCounter.setVisibility(View.GONE);
            videoBinding.ivFlashOnOff.setVisibility(View.VISIBLE);

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
                    Log.d(tag, "-temp file--" + tempFile.getAbsolutePath());
                    Log.d(tag, "-thum file--" + thumbFilename.getAbsolutePath());
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
        mediaRecorder.setPreviewDisplay(mPreview.mHolder.getSurface());

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
        mediaFileName = "ozogram" + System.currentTimeMillis();
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

                    videoBinding.btnTakePhoto.dispatchTouchEvent(motionEvent);

                    Toast.makeText(getActivity(), "You reached to Maximum(10MB) video size.", Toast.LENGTH_SHORT).show();
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

    private void scaleDownAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(videoBinding.btnTakePhoto, "scaleX", 0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(videoBinding.btnTakePhoto, "scaleY", 0f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                View p = (View) videoBinding.btnTakePhoto.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    private void scaleUpAnimation() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(videoBinding.btnTakePhoto, "scaleX", 0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(videoBinding.btnTakePhoto, "scaleY", 0f);
        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);

        scaleDownX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                View p = (View) videoBinding.btnTakePhoto.getParent();
                p.invalidate();
            }
        });
        scaleDown.start();
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mCamera.setDisplayOrientation(90);
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(tag, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            refreshCamera(mCamera);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
            mCamera.release();
        }

        public void refreshCamera(Camera camera) {
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            // set preview size and make any resize, rotate or
            // reformatting changes here
            // start preview with new settings
            setCamera(camera);
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

        public void setCamera(Camera camera) {
            //method to set a camera instance
            mCamera = camera;
        }
    }

}