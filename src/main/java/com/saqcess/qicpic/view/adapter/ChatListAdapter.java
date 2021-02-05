package com.saqcess.qicpic.view.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.base.AppBaseRecycleAdapter;
import com.saqcess.qicpic.base.BaseRecycleAdapter;
import com.saqcess.qicpic.model.GetMessageData;
import com.saqcess.qicpic.view.activity.ActivityChatMessage;

import java.util.List;

public class ChatListAdapter extends AppBaseRecycleAdapter {

    Context context;
    List<GetMessageData> chatList;
    ActivityChatMessage activityChatMessage;

    public ChatListAdapter(Context applicationContext, List<GetMessageData> chatList, ActivityChatMessage activityChatMessage) {
        this.chatList = chatList;
        context = applicationContext;
        this.activityChatMessage = activityChatMessage;
    }

    @Override
    public BaseRecycleAdapter.BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.view_chat_list));
    }

    @Override
    public int getDataCount() {
        return chatList.size();
    }

    public void updateList(List<GetMessageData> list){
        chatList=list;
        notifyDataSetChanged();
    }
    public class ViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        RelativeLayout rl_r;
        TextView message;
        TextView timestamp;
        RelativeLayout rl_l;
        TextView message_l;
        TextView timestamp_l;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_r = itemView.findViewById(R.id.rl_r);
            message = itemView.findViewById(R.id.message);
            timestamp = itemView.findViewById(R.id.timestamp);

            rl_l = itemView.findViewById(R.id.rl_l);
            message_l = itemView.findViewById(R.id.message_l);
            timestamp_l = itemView.findViewById(R.id.timestamp_l);
        }

        @Override
        public void setData(int position) {
            GetMessageData dataModel = chatList.get(position);
        setMessage(this,position,dataModel);
        }

        private void setMessage(ViewHolder viewHolder, int position, GetMessageData dataModel) {
            if(dataModel.getPosition().equals("right")){
                viewHolder.rl_l.setVisibility(View.GONE);
                viewHolder.rl_r.setVisibility(View.VISIBLE);
                viewHolder.message.setText(dataModel.getMessage());
                viewHolder.timestamp.setText(dataModel.getEntryTime());
            }else{
                viewHolder.rl_l.setVisibility(View.VISIBLE);
                viewHolder.rl_r.setVisibility(View.GONE);
                viewHolder.message_l.setText(dataModel.getMessage());
                viewHolder.timestamp_l.setText(dataModel.getEntryTime());
            }
        }
    }
}
