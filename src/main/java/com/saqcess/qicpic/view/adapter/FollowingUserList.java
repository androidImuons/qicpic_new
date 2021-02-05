package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.base.AppBaseRecycleAdapter;
import com.saqcess.qicpic.model.FolloweDataModel;
import com.saqcess.qicpic.view.dialog.SendPostDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingUserList extends AppBaseRecycleAdapter {
    Context context;
    List<FolloweDataModel> dataModelList;
    SendPostDialog sendPostDialog;
    public FollowingUserList(Context context, List<FolloweDataModel> followingList, SendPostDialog sendPostDialog) {
        this.context=context;
        this.dataModelList=followingList;
        this.sendPostDialog=sendPostDialog;
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.view_follower_list));
    }

    @Override
    public int getDataCount() {
        return dataModelList.size();
    }

    public void updateList(List<FolloweDataModel> data) {
        this.dataModelList=data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends AppBaseRecycleAdapter.BaseViewHolder {
        CircleImageView iv_profile;
        TextView txt_name;
        TextView txt_send;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_send = itemView.findViewById(R.id.txt_send);

        }

        @Override
        public void setData(int position) {
            sendPost(this,position);
        }
    }

    private void sendPost(ViewHolder viewHolder, int position) {
        Glide.with(getContext())
                .load(dataModelList.get(position).getProfile_picture())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_icon)
                .into(viewHolder.iv_profile);
        viewHolder.txt_name.setText(dataModelList.get(position).getFullname());
        viewHolder.txt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sendPostDialog.sendPost(position);
            }
        });
    }
}
