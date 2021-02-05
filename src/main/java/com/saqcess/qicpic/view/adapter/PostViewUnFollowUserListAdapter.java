package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.model.PostGalleryPath;
import com.saqcess.qicpic.model.PostUserDataData;
import com.saqcess.qicpic.model.UnfollowUserRecordDataModel;
import com.saqcess.qicpic.view.activity.ViewPostActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewUnFollowUserListAdapter extends PagerAdapter {
    Context context;
    List<UnfollowUserRecordDataModel> unfollowUserRecordDataModelList;
    PostViewUnFollowUserListerner listerner;


    public PostViewUnFollowUserListAdapter(Context applicationContext, List<UnfollowUserRecordDataModel> unFollowUserList,PostViewUnFollowUserListerner listener) {
        this.context = applicationContext;
        this.unfollowUserRecordDataModelList = unFollowUserList;
        this.listerner=listener;
    }



    public void update(List<UnfollowUserRecordDataModel> list) {
        this.unfollowUserRecordDataModelList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return unfollowUserRecordDataModelList.size();
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null;
        ViewModel viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_follow_list, null);
            viewHolder = new ViewModel(view);
            view.setTag(viewHolder);
            container.addView(view);
        } else {
            viewHolder = (ViewModel) view.getTag();
        }

        setData(viewHolder,position);
       return view;
    }


    public void setData(ViewModel viewModel,int position) {
        setImage(viewModel, position);
        UnfollowUserRecordDataModel obj = unfollowUserRecordDataModelList.get(position);
        setPagerAdapter(viewModel, obj, position);

    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }
    public class ViewModel  {
        CircleImageView iv_profile_view;
        TextView txt_user_name;
        TextView txt_user_post;
        ViewPager view_pager_user_post;
        TextView txt_follow;
        ProgressBar progress_bar;
        TextView txt_no_post;

        public ViewModel(View itemView) {

            iv_profile_view = itemView.findViewById(R.id.iv_profile_view);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_user_post = itemView.findViewById(R.id.txt_user_post);
            view_pager_user_post = itemView.findViewById(R.id.view_pager_user_post);
            txt_follow = itemView.findViewById(R.id.txt_follow);
            progress_bar = itemView.findViewById(R.id.progress_bar);
            txt_no_post = itemView.findViewById(R.id.txt_no_post);
        }


    }
    private void setPagerAdapter(ViewModel viewModel, UnfollowUserRecordDataModel obj, int position) {
        List<PostUserDataData> url_array = obj.getPostDat();
        ArrayList<PostGalleryPath> postArray = new ArrayList<>();
        for (int i = 0; i < url_array.size(); i++) {
            PostUserDataData galleryobj = url_array.get(i);
            postArray.addAll(galleryobj.getPostGalleryPath());
        }
        if (postArray.size() > 0) {
            viewModel.txt_no_post.setVisibility(View.GONE);
            viewModel.view_pager_user_post.setVisibility(View.VISIBLE);

            ViewPostPagerAdapter postPagerAdapter = new ViewPostPagerAdapter(context, postArray,
                    PostViewUnFollowUserListAdapter.this);
            viewModel.view_pager_user_post.setAdapter(postPagerAdapter);

        } else {
            viewModel.txt_no_post.setVisibility(View.VISIBLE);
            viewModel.view_pager_user_post.setVisibility(View.GONE);
        }
    }

    private void setImage(ViewModel viewModel, int position) {
        UnfollowUserRecordDataModel obj = unfollowUserRecordDataModelList.get(position);
      //  listerner.loadImage(context, viewModel.iv_profile_view, viewModel.progress_bar, unfollowUserRecordDataModelList.get(position).getProfilePicture(), R.drawable.ic_profile);
        Glide.with(context)
                .load(unfollowUserRecordDataModelList.get(position).getProfilePicture())
                .placeholder(R.drawable.profile_icon)
                .into(viewModel.iv_profile_view);
        viewModel.txt_user_name.setText(obj.getFullname());

        viewModel.txt_follow.setTag(0);
        viewModel.txt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String action;
                if((Integer)viewModel.txt_follow.getTag()==0){
                    action="follow";
                    viewModel.txt_follow.setText("Following");
                    viewModel.txt_follow.setTag(1);
                }else{
                    viewModel.txt_follow.setText("Follow");
                    viewModel.txt_follow.setTag(0);
                    action="unfollow";
                }
                listerner.followUnfollow(context, obj.getUserId(), action);
            }
        });
    }

    public interface PostViewUnFollowUserListerner {


        void followUnfollow(Context context, String userId, String action);
    }
}
