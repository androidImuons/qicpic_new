package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.view.activity.BaseActivity;
import com.saqcess.qicpic.view.activity.GalleryActivity;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.ArrayList;
import java.util.List;

public class UpLoadImageVideoPagerAdapter extends PagerAdapter {


    private Context context;
    List<String> postGalleryPathModelList;
    GalleryActivity uploadImagesDialogBoxs;

    public UpLoadImageVideoPagerAdapter(BaseActivity activity, ArrayList<String> arraList, GalleryActivity uploadImagesDialogBoxs, FragmentActivity activity1) {
        context = activity;
        postGalleryPathModelList = arraList;
        this.uploadImagesDialogBoxs = uploadImagesDialogBoxs;

    }

    @Override
    public int getCount() {
        return postGalleryPathModelList.size();
    }


    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View view = null;
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_post_view, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            container.addView(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if(postGalleryPathModelList.size()==1){
            viewHolder.iv_delete.setVisibility(View.GONE);
        }else{
            viewHolder.iv_delete.setVisibility(View.GONE);
        }
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("file delet","------"+position);
                deleteItem(position);
            }
        });

        if (postGalleryPathModelList.get(position).endsWith("jpg") || postGalleryPathModelList.get(position).endsWith("png")) {
            setData(viewHolder, postGalleryPathModelList.get(position), position);
        } else {
            videoLayer(viewHolder, position);
        }


        return view;
    }

    private void deleteItem(int position) {
       //postGalleryPathModelList.remove(position);
    }

    private void setData(ViewHolder viewHolder, String url, int position) {
        if (url.endsWith("jpg") || url.endsWith("png")) {
            viewHolder.iv_post_image.setVisibility(View.VISIBLE);
            viewHolder.vv_video.setVisibility(View.GONE);
            uploadImagesDialogBoxs.loadImage(context, viewHolder.iv_post_image, viewHolder.pb_bar, url, R.drawable.ic_profile);
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    private void videoLayer(ViewHolder viewHolder, int position) {

        RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context).load(postGalleryPathModelList.get(position))
                .skipMemoryCache(false)
                .apply(requestOptions)
                .into(viewHolder.iv_post_image);

        viewHolder.vv_video.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener() {
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
                viewHolder.iv_post_image.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
                viewHolder.iv_post_image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
                viewHolder.iv_post_image.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.iv_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPlayerManager.playNewVideo(null, viewHolder.vv_video, postGalleryPathModelList.get(position));
            }
        });
    }

    public class ViewHolder {
        ImageView iv_post_image;
        VideoPlayerView vv_video;
        ProgressBar pb_bar;
        public ImageView iv_delete;

        public ViewHolder(View itemView) {
            vv_video = itemView.findViewById(R.id.video_player);
            iv_post_image = itemView.findViewById(R.id.iv_post_image);
            pb_bar = itemView.findViewById(R.id.pb_bar);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

  public  VideoPlayerManager videoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData currentItemMetaData) {
        }
    });
}
