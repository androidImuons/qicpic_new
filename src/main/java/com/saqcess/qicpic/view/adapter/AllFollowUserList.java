package com.saqcess.qicpic.view.adapter;

import android.view.View;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.base.AppBaseRecycleAdapter;
import com.saqcess.qicpic.base.BaseRecycleAdapter;

public class AllFollowUserList extends AppBaseRecycleAdapter {
    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.view_follow_list));
    }

    @Override
    public int getDataCount() {
        return 10;
    }
    public class ViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(int position) {

        }
    }
}
