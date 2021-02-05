package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.base.AppBaseRecycleAdapter;
import com.saqcess.qicpic.customeview.ReadMoreTextView;
import com.saqcess.qicpic.model.GetPostRecordModel;
import com.saqcess.qicpic.model.LikeUserModel;
import com.saqcess.qicpic.view.activity.ViewAllCommentActivity;
import com.saqcess.qicpic.view.activity.ViewPostActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

public class PostViewAdapter extends AppBaseRecycleAdapter {
    Context context;
    List<GetPostRecordModel> postDataModelList;
    private boolean readMore;
    ViewPostActivity activity;
    private SessionManager session;
    PostViewInterface postViewInterface;
    private String tag = "PostRecycleViewAdapter";
    private boolean is_like;

    public PostViewAdapter(Context applicationContext, List<GetPostRecordModel> post, ViewPostActivity ozogramHomeActivity) {
        context = applicationContext;
        postDataModelList = post;
        activity = ozogramHomeActivity;
        session = new SessionManager(context);
        postViewInterface =  activity;
    }

    public int getViewHeight() {
        return 0;
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new StoryUserListView(inflateLayout(R.layout.view_post_view_list));
    }

    @Override
    public int getDataCount() {
        return postDataModelList == null ? 0 : postDataModelList.size();
    }


    public void updateList(List<GetPostRecordModel> post) {
        postDataModelList = post;
        is_like = false;
        notifyDataSetChanged();
    }

    public class StoryUserListView extends BaseViewHolder implements ReadMoreTextView.ReadMoreTextViewListener {
        CircleImageView iv_user_image;
        TextView txt_post_user_name;
        ImageView iv_more;
        ViewPager view_pager;
        ImageView iv_heart;
        ImageView iv_commit;
        ImageView iv_share;
        CircleIndicator circle;
        ImageView iv_tag;
        TextView txt_t_view;
        TextView txt_t_comment;
        ReadMoreTextView tv_message;
        ProgressBar pb_image;
        TextView txt_comment;

        public StoryUserListView(View itemView) {
            super(itemView);
            iv_user_image = itemView.findViewById(R.id.iv_user_image);
            txt_post_user_name = itemView.findViewById(R.id.txt_post_user_name);
            iv_more = itemView.findViewById(R.id.iv_more);
            view_pager = itemView.findViewById(R.id.view_pager);
            iv_heart = itemView.findViewById(R.id.iv_heart);
            iv_commit = itemView.findViewById(R.id.iv_commit);
            iv_share = itemView.findViewById(R.id.iv_share);
            circle = itemView.findViewById(R.id.circle);
            iv_tag = itemView.findViewById(R.id.iv_tag);
            txt_t_view = itemView.findViewById(R.id.txt_t_view);
            txt_t_comment = itemView.findViewById(R.id.txt_t_comment);
            tv_message = itemView.findViewById(R.id.tv_message);
            pb_image = itemView.findViewById(R.id.pb_image);
            txt_comment = itemView.findViewById(R.id.txt_comment);

        }

        @Override
        public void setData(int index) {
            //int position = (postDataModelList.size() - 1) - index;
            int position = index;
            final int pos = position;

            setViewPager(this, position);
            GetPostRecordModel model = postDataModelList.get(position);
           // activity.loadImage(context, iv_user_image, pb_image, model.getUser().getProfile_picture(), R.drawable.ic_profile);
            Glide.with(context)
                    .load(model.getUser().getProfile_picture())
                    .placeholder(R.drawable.profile_icon)
                    .into(iv_user_image);
            if (model.getLikesCount() > 0) {
                if (model.getLikesCount() == 1) {
                    txt_t_view.setText("Liked by " + model.getLikeUsers().get(0).getFullname());
                } else {
                    txt_t_view.setText("Liked by " + model.getLikeUsers().get(0).getFullname() + " and other " + model.getLikesCount());
                }
            } else {
                txt_t_view.setVisibility(View.GONE);
            }
            if (model.getCommentsCount() > 0) {
                if (model.getCommentsCount() == 1) {
                    txt_comment.setText(model.getComments().get(0).getComment() + " Comment by " + model.getComments().get(0).getFullname());
                    txt_t_comment.setText("View " + model.getCommentsCount() + " Comment");
                } else {
                    txt_comment.setText(model.getComments().get(0).getComment() + " Comment by " + model.getComments().get(0).getFullname());
                    txt_t_comment.setText("View all " + model.getCommentsCount() + " Comments");
                }
                txt_comment.setVisibility(View.VISIBLE);
                txt_t_comment.setVisibility(View.VISIBLE);
            } else {
                txt_comment.setVisibility(View.GONE);
                txt_t_comment.setVisibility(View.GONE);
            }
            txt_post_user_name.setText(model.getUser().getFullname());
            if (model.getCaption().equals("")) {
                tv_message.setText("No Story");
            } else {
                tv_message.setTag(position);
                tv_message.setReadMoreTextViewListener(this);
                tv_message.setText(model.getCaption());
                tv_message.readMore = model.isReadMore();
                tv_message.setText(model.getCaption(), TextView.BufferType.NORMAL);
            }
            List<LikeUserModel> likeUserModellist = model.getLikeUsers();
            is_like = false;

            for (int i = 0; i < likeUserModellist.size(); i++) {
                if (likeUserModellist.get(i).getUserId().equals(session.getUserDetails().get(session.KEY_USERID))) {
                    is_like = true;
                    break;
                } else {
                    is_like = false;
                }
            }
            if (is_like) {
                iv_heart.setBackgroundResource(R.drawable.ic_heart_fill);
                iv_heart.setTag(1);
                //iv_heart.setSelected(true);
            } else {
                iv_heart.setBackgroundResource(R.drawable.ic_heart);
                iv_heart.setTag(0);
            }

            if (model.getSaved()==1){
                iv_tag.setBackgroundResource(R.drawable.ic__saved);

            }
            setClick(pos);

        }

        @Override
        public void onReadMoreChange(ReadMoreTextView textView) {
            int position = Integer.parseInt(textView.getTag().toString());
            postDataModelList.get(position).setReadMore(textView.readMore);
            if (textView.readMore && getRecyclerView() != null) {
                getRecyclerView().scrollToPosition(position);
            }
        }

        @Override
        public void onClick(View v) {

        }

        private void setClick(int pos) {

            iv_heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((Integer) view.getTag() == 0) {
                        iv_heart.setTag(1);
                        postViewInterface.clickLike(pos, "like");
                        iv_heart.setBackgroundResource(R.drawable.ic_heart_fill);
                    } else {
                        iv_heart.setTag(0);
                        postViewInterface.clickLike(pos, "unlike");
                        iv_heart.setBackgroundResource(R.drawable.ic_heart);
                    }
                }
            });
            iv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postViewInterface.clickComment(pos);
                }
            });
            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postViewInterface.sendPost(pos);
                }
            });
            iv_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postViewInterface.savePOST(pos);
                }
            });
            iv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.moreaction();
                }
            });
            txt_t_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewAllCommentActivity.class);
                    intent.putExtra("post_id", postDataModelList.get(pos).getId());
                   // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }


    private void setViewPager(StoryUserListView viewmodel, int position) {
        GetPostRecordModel model = postDataModelList.get(position);
        PostViewPagerAdapter postPagerAdapter = new PostViewPagerAdapter(context, postDataModelList.get(position).getPostGalleryPath(),
                PostViewAdapter.this, activity);
        viewmodel.view_pager.setAdapter(postPagerAdapter);
        viewmodel.circle.setViewPager(viewmodel.view_pager);
        viewmodel.view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                postPagerAdapter.videoPlayerManager.stopAnyPlayback();
            }

            @Override
            public void onPageSelected(int pagePosition) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void insert(int position, GetPostRecordModel data) {
        postDataModelList.add(position, data);
        notifyItemInserted(position);
    }

    public interface PostViewInterface {
        public void clickLike(int pos, String flag);

        public void clickComment(int pos);

        public void sendPost(int pos);

        public void savePOST(int pos);

    }
}
