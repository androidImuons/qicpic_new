package com.saqcess.qicpic.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.DiscoverPeopleItemLytBinding;
import com.saqcess.qicpic.viewmodel.UnFollowerData;

import java.util.List;

public class UnFollowUserAdapter extends RecyclerView.Adapter<UnFollowUserAdapter.ViewHolder> {
    List<UnFollowerData> unFollowerUserList;
    UnFollowUserAdapterListener listener;
    private LayoutInflater layoutInflater;

    public UnFollowUserAdapter(List<UnFollowerData> unFollowerUserList, UnFollowUserAdapterListener listener) {
        this.unFollowerUserList = unFollowerUserList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        DiscoverPeopleItemLytBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.discover_people_item_lyt, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setUnFollowUser(unFollowerUserList.get(position));
        holder.binding.tvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onFollowRequestClicked(unFollowerUserList.get(position));
                }
            }
        });

        holder.binding.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRemoveUserClicked(unFollowerUserList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return unFollowerUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         private final DiscoverPeopleItemLytBinding binding;

        public ViewHolder(final DiscoverPeopleItemLytBinding itemBinding) {
            super(itemBinding.getRoot());
            this.binding = itemBinding;
        }
    }

    public interface UnFollowUserAdapterListener {
        void onFollowRequestClicked(UnFollowerData unFollowerData);

        void onRemoveUserClicked(UnFollowerData unFollowerData);
    }
}
