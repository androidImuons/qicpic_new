package com.saqcess.qicpic.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.ProfileStoryListItemBinding;
import com.saqcess.qicpic.model.PostGalleryPath;

import java.util.List;

public class ProfileStroyAdpter extends RecyclerView.Adapter<ProfileStroyAdpter.MyViewHolder> {

    private List<PostGalleryPath> postGalleryPathList;
    private LayoutInflater layoutInflater;
    private ProfileStroyAdpterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final ProfileStoryListItemBinding binding;

        public MyViewHolder(final ProfileStoryListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

    public ProfileStroyAdpter(List<PostGalleryPath> postGalleryPathList, ProfileStroyAdpterListener listener) {
        this.postGalleryPathList = postGalleryPathList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ProfileStoryListItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.profile_story_list_item, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.binding.setPostGalleryPath(postGalleryPathList.get(position));
        holder.binding.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPostClicked(postGalleryPathList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postGalleryPathList.size();
    }

    public interface ProfileStroyAdpterListener {
        void onPostClicked(PostGalleryPath postGalleryPath);
    }
}
