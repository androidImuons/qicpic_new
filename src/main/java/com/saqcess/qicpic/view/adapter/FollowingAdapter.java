package com.saqcess.qicpic.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.FollowingItemLayoutBinding;
import com.saqcess.qicpic.model.Follower;
import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.MyViewHolder> {
    FollowingAdapterListener listener;
    List<Follower> followingList;
    LayoutInflater layoutInflater;

    public FollowingAdapter(FollowingAdapterListener listener, List<Follower> followingList) {
        this.listener = listener;
        this.followingList = followingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        FollowingItemLayoutBinding binding= DataBindingUtil.inflate(layoutInflater, R.layout.following_item_layout,parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setFollowing(followingList.get(position));
        holder.binding.llFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onViewFollowingClick(followingList.get(position));
                }   
            }
        });
        holder.binding.tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFollowingClick(followingList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FollowingItemLayoutBinding binding;
        public MyViewHolder(@NonNull FollowingItemLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding=itemBinding;
        }
    }

    public interface FollowingAdapterListener{
         void onFollowingClick(Follower follower);

        void onViewFollowingClick(Follower follower);
    }
}
