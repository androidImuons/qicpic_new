package com.saqcess.qicpic.view.fragment;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.GridSpacingItemDecoration;
import com.saqcess.qicpic.databinding.FragmentPostGalleryBinding;
import com.saqcess.qicpic.view.adapter.GirdViewAdapter;
import com.saqcess.qicpic.viewmodel.PostGalleryViewModel;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostGalleryFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    public boolean is_crop;
    private boolean is_check_open;
    public int selected_position;
    public String selected_file_url;
    private GirdViewAdapter obj_adapter;
    private String tag = "PostGalleryFragment";

    public PostGalleryFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostGalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostGalleryFragment newInstance(String param1, String param2) {
        PostGalleryFragment fragment = new PostGalleryFragment();
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

    public FragmentPostGalleryBinding fragmentPostGalleryBinding;
    PostGalleryViewModel postGalleryViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentPostGalleryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_post_gallery, container, false);
        postGalleryViewModel = ViewModelProviders.of(getActivity()).get(PostGalleryViewModel.class);
        fragmentPostGalleryBinding.setLifecycleOwner(getActivity());
        fragmentPostGalleryBinding.setPostone(postGalleryViewModel);
        view = fragmentPostGalleryBinding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        setListner();
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        fragmentPostGalleryBinding.recycleGallery.setLayoutManager(sglm);
        fragmentPostGalleryBinding.recycleGallery.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(4), true));
        obj_adapter = new GirdViewAdapter(getContext(), arrayList, PostGalleryFragment.this);
        fragmentPostGalleryBinding.recycleGallery.setAdapter(obj_adapter);
        fragmentPostGalleryBinding.nestedListGallery.scrollTo(0, 0);

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void setListner() {
        fragmentPostGalleryBinding.ivCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_crop) {
                    is_crop = true;
                    fragmentPostGalleryBinding.ivImage.setVisibility(View.GONE);
                    fragmentPostGalleryBinding.cropImageView.setVisibility(View.VISIBLE);
                    fragmentPostGalleryBinding.cropImageView.setImageUriAsync(Uri.parse(selected_file_url));

                } else {
                    is_crop = false;
                    fragmentPostGalleryBinding.ivImage.setVisibility(View.VISIBLE);
                    fragmentPostGalleryBinding.cropImageView.setVisibility(View.GONE);
                }

            }
        });

        fragmentPostGalleryBinding.ivSelectMultipleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_check_open) {
                    is_check_open = true;
                    selectedImageList.clear();
                    obj_adapter.update(is_check_open);

                } else {
                    is_check_open = false;
                    selectedImageList.clear();
                    obj_adapter.update(is_check_open);
                }
            }
        });
    }

    ArrayList<String> arrayList = new ArrayList<>();

    public void setList(ArrayList<String> arrayList, int type) {
        selectedImageList.clear();
        this.arrayList = arrayList;
        if(videoPlayerManager!=null){
            videoPlayerManager.stopAnyPlayback();
        }
        if (obj_adapter != null) {
            obj_adapter.update(arrayList, type);
        } else {
            Log.d(tag, "--post obj null-");
        }

    }

    public HashMap<Integer, String> selectedImageList = new HashMap<>();

    public void imageClick(int position, String file) {
        final String url = "file://" + file;

        Glide.with(getActivity()).load(url)
                .into(fragmentPostGalleryBinding.ivImage);
        selected_file_url = url;
        if (is_check_open) {
            selectedImageList.put(position, file);
        } else {
            selectedImageList.clear();
            selectedImageList.put(position, file);
        }

        if (selected_position != position) {
            selected_position = position;
        }
        if (file.endsWith("mp4")){
            fragmentPostGalleryBinding.videoPlayer.onVideoStoppedMainThread();

            fragmentPostGalleryBinding.videoPlayer.setVisibility(View.VISIBLE);
            videoLayer(url);
            videoPlayerManager.playNewVideo(null, fragmentPostGalleryBinding.videoPlayer,url );
        }else{
            fragmentPostGalleryBinding.videoPlayer.onVideoStoppedMainThread();
            fragmentPostGalleryBinding.videoPlayer.setVisibility(View.GONE);
        }
        fragmentPostGalleryBinding.nestedListGallery.scrollTo(0, 0);
    }

    public void checkClick(int position, String file, boolean checked) {
        final String url = "file://" + file;
        if (!checked) {
            selectedImageList.remove(position);
        } else {
            selectedImageList.put(position, file);
            Glide.with(getActivity()).load(url)
                    .into(fragmentPostGalleryBinding.ivImage);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentPostGalleryBinding.recycleGallery.setAdapter(null);
        fragmentPostGalleryBinding.recycleGallery.setLayoutManager(null);
        fragmentPostGalleryBinding.recycleGallery.clearOnScrollListeners();
        fragmentPostGalleryBinding.recycleGallery.addItemDecoration(null);

    }

    private void videoLayer(String url){
Log.d(tag,"----video player--");
        RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC);
        Glide.with(getActivity()).load(url)
                .skipMemoryCache(false)
                .apply(requestOptions)
                .into(fragmentPostGalleryBinding.ivImage);

        fragmentPostGalleryBinding.videoPlayer.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
            @Override
            public void onVideoPreparedMainThread() {
                // We hide the cover when video is prepared. Playback is about to start
                fragmentPostGalleryBinding.ivImage.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVideoStoppedMainThread() {
                // We show the cover when video is stopped
                fragmentPostGalleryBinding.ivImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {
                // We show the cover when video is completed
                fragmentPostGalleryBinding.ivImage.setVisibility(View.VISIBLE);
            }
        });

    }

    VideoPlayerManager videoPlayerManager=new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData currentItemMetaData) {

        }
    });

}