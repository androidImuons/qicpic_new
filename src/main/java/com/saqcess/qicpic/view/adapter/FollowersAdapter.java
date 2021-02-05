package com.saqcess.qicpic.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.FollowerItemLayoutBinding;
import com.saqcess.qicpic.model.Follower;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {
    private List<Follower> followerList;
    private LayoutInflater layoutInflater;
    private FollowersAdapterListener listener;

    public FollowersAdapter(List<Follower> followerList, FollowersAdapterListener listener) {
        this.followerList = followerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        FollowerItemLayoutBinding binding= DataBindingUtil.inflate( layoutInflater,R.layout.follower_item_layout, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setFollower(followerList.get(position));
        holder.binding.llFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFollowerClick(followerList.get(position));
                }
            }
        });
        holder.binding.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRemoveFollowerClick(followerList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final FollowerItemLayoutBinding binding;
        public MyViewHolder(final FollowerItemLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

    public interface FollowersAdapterListener{

        void onRemoveFollowerClick(Follower follower);

        void onFollowerClick(Follower follower);
    }
}
