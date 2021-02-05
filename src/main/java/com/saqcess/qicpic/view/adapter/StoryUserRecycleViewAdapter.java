package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.view.View;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.base.AppBaseRecycleAdapter;
import com.saqcess.qicpic.model.GetPostRecordModel;

import java.util.List;

public class StoryUserRecycleViewAdapter extends AppBaseRecycleAdapter {
    Context context;
    List<GetPostRecordModel> postDataModelList;

    public StoryUserRecycleViewAdapter(Context applicationContext, List<GetPostRecordModel> post) {
        context = applicationContext;
        postDataModelList = post;
    }

    public int getViewHeight() {
        return 0;
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new StoryUserListView(inflateLayout(R.layout.view_home_story_list));
    }

    @Override
    public int getDataCount() {
         return postDataModelList == null ? 0 : postDataModelList.size();
    }

    public void updateList(List<GetPostRecordModel> post) {
        postDataModelList=post;
        notifyDataSetChanged();
    }

    public class StoryUserListView extends BaseViewHolder {

        public StoryUserListView(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(int position) {

        }

    }
}
