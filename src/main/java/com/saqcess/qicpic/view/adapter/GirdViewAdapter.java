package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.view.activity.BaseActivity;
import com.saqcess.qicpic.view.fragment.PostGalleryFragment;

import java.util.ArrayList;

public class GirdViewAdapter extends RecyclerView.Adapter<GirdViewAdapter.GridView> {
    private boolean[] thumbnailsselection;
    private LayoutInflater inflater;
    Bitmap[] listofImage;
    Context context;
    ArrayList<String> arrayList;
    ClickEvent clickEvent;
PostGalleryFragment postGalleryFragment;
    private int type;

    public GirdViewAdapter(Context applicationContext, ArrayList<String> al_images, PostGalleryFragment galleryActivity) {
        context = applicationContext;
        arrayList = al_images;
        //clickEvent = (ClickEvent)galleryActivity;
        this.postGalleryFragment=galleryActivity;
    }

    @NonNull
    @Override
    public GridView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_list_view, parent, false);
        return new GridView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GridView binding, final int position) {
        Log.d("grid adpter", "----");
        //   binding.imageview.setImageBitmap(listofImage[position]);
        final String file = "file://" + arrayList.get(position);
        if (position == 0) {
            if(is_check_open){
                binding.checkbox.setChecked(true);
            }
            postGalleryFragment.imageClick(position, arrayList.get(position));
        }

        Log.d("grid adpter", "----"+file);
        setImage(position, binding, file);
        setVisibility(binding);


        binding.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGalleryFragment.imageClick(position, arrayList.get(position));
            }
        });
        binding.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(postGalleryFragment.selectedImageList.size()<10){
                    postGalleryFragment.checkClick(position, arrayList.get(position),  binding.checkbox.isChecked());

                }else{
                    binding.checkbox.setChecked(false);
                    postGalleryFragment.showSnackbar(postGalleryFragment.fragmentPostGalleryBinding.recycleGallery,"Max 10 file allowed...!", Snackbar.LENGTH_SHORT);
                }


            }
        });
    }

    private void setImage(int position, GridView binding, String file) {

        if(type==0){
//            RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
//            Glide.with(context).load(file)
//                    .placeholder(R.mipmap.ic_logo)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .error(R.mipmap.ic_logo)
//                    .thumbnail(0.1f)
//                    .apply(requestOptions)
//                    .into(binding.imageview);
            ( (BaseActivity)context).loadImage(context,binding.imageview,binding.pb_image,arrayList.get(position),R.drawable.qicpiclogo);

        }else if(type==1){
            RequestOptions requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL);
            Glide.with(context).load(file)
                    .skipMemoryCache(false)
                    .apply(requestOptions)
                    .into(binding.imageview);
        }


        binding.id = position;
    }

    @Override
    public void onViewRecycled(@NonNull GridView holder) {
        super.onViewRecycled(holder);
    }

    private void setVisibility(GridView binding) {
        if (is_check_open) {
            binding.checkbox.setVisibility(View.VISIBLE);

        } else {
            binding.checkbox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(arrayList.size()<=300){
            return arrayList.size();
        }else{
            return 300;
        }

    }


    public void update(ArrayList<String> list,int type) {
        this.type=type;
        arrayList = list;
        notifyDataSetChanged();
    }

    boolean is_check_open;

    public void update(boolean is_check_open) {
        if (is_check_open) {
            this.is_check_open = is_check_open;
            notifyDataSetChanged();

        } else {
            this.is_check_open = is_check_open;
            notifyDataSetChanged();
        }
    }

    protected class GridView extends RecyclerView.ViewHolder {

        public ImageView imageview;
        public CheckBox checkbox;
        public int id;
        ProgressBar pb_image;

        public GridView(@NonNull View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.iv_show_image);
            checkbox = itemView.findViewById(R.id.ch_image_check);
            pb_image=itemView.findViewById(R.id.pb_image);
        }
    }

    public interface ClickEvent {
        public void imageClickEvent(int position, String url);

        public void checkBokClickEvent(int position, String url);
    }
}
