package com.saqcess.qicpic.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;

public class BaseFragment extends Fragment {

    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        if (getActivity() != null) {
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void loadImage(Object mContext, ImageView imageView,
                          final ProgressBar pb_image, String imageUrl, int placeHolder) {
        this.loadImage(mContext, imageView, pb_image, imageUrl, placeHolder, placeHolder, placeHolder, 200);
    }

    public void loadImage(Object mContext, ImageView imageView,
                          final ProgressBar pb_image, String imageUrl,
                          int placeHolder, int error, int fallBack, int requireWidth) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                pb_image.setVisibility(View.INVISIBLE);
            }
            imageView.setImageResource(fallBack);
            return;
        }
        if (mContext == null) return;

        try {
            RequestManager requestManager = null;
            if (mContext instanceof Activity) {
                requestManager = Glide.with((Activity) mContext);
            } else if (mContext instanceof Fragment) {
                requestManager = Glide.with(((Fragment) mContext).getActivity());
            } else {
                requestManager = Glide.with((Context) mContext);
            }
            if (requestManager == null) return;
            RequestOptions options = new RequestOptions()
                    .placeholder(placeHolder)
                    .fallback(error);
            if (requireWidth > 0) {
                options = options.override(requireWidth);
            }

            options = options.error(error)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            if (pb_image != null && pb_image.getVisibility() != View.VISIBLE) {
                pb_image.setVisibility(View.VISIBLE);
            }
            requestManager.asBitmap().load(imageUrl).
                    apply(options).
                    listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                                pb_image.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                                pb_image.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }
                    }).centerCrop().into(imageView);

        } catch (Exception e) {

        }

    }


    protected void showProgressDialog(String msg) {
        try {
            if (progressDialog == null ) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            if(!progressDialog.isShowing()) {
                progressDialog.setMessage(msg);
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void openActivity(Bundle bundle, Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra("data", bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }
}
