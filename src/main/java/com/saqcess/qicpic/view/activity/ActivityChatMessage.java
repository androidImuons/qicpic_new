package com.saqcess.qicpic.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.databinding.ActivityChatMessageBinding;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetMessageData;
import com.saqcess.qicpic.model.GetMessageResponse;
import com.saqcess.qicpic.view.adapter.ChatListAdapter;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.OnChatGetMessage;
import com.saqcess.qicpic.viewmodel.ChatMessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ActivityChatMessage extends AppCompatActivity implements OnChatGetMessage, CommonResponseInterface {

    ActivityChatMessageBinding chatMessageBinding;
    ChatMessageViewModel chatMessageViewModel;
    Bundle bundle;
    private String to_user_id;
    private List<GetMessageData> chatList = new ArrayList<>();
    private ChatListAdapter chatListAdapter;
    private ArrayList<String> fileList = new ArrayList<>();
    private String tag = "ActivityChatMessage";
    private String image;
    private String bio;
    private String name;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatMessageBinding = DataBindingUtil.setContentView(ActivityChatMessage.this, R.layout.activity_chat_message);
        chatMessageViewModel = ViewModelProviders.of(ActivityChatMessage.this).get(ChatMessageViewModel.class);
        chatMessageBinding.setChat(chatMessageViewModel);
        chatMessageViewModel.chatGetMessage = (OnChatGetMessage) ActivityChatMessage.this;
        chatMessageViewModel.commonResponseInterface = (CommonResponseInterface) ActivityChatMessage.this;
        bundle = getIntent().getExtras();
        to_user_id = bundle.getString("id");
        image = bundle.getString("image");
        bio = bundle.getString("bio");
        name = bundle.getString("name");
        type = bundle.getInt("type");
        Glide.with(getApplicationContext())
                .load(image)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.profile_icon)
                .into(chatMessageBinding.toolBarInclude.ivProfileImage);

        chatMessageBinding.toolBarInclude.txtUserName.setText(bio);
        chatMessageBinding.toolBarInclude.txtSubName.setText(name);

        setAdapter();
        onClick();
        chatMessageBinding.message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (i2 > 0) {
                    chatMessageBinding.ivGallery.setVisibility(View.GONE);
                    chatMessageBinding.ivMicq.setVisibility(View.GONE);
                    chatMessageBinding.ivSend.setVisibility(View.VISIBLE);
                } else {
                    chatMessageBinding.ivGallery.setVisibility(View.VISIBLE);
                    chatMessageBinding.ivMicq.setVisibility(View.GONE);
                    chatMessageBinding.ivSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void onClick() {
        chatMessageBinding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessageViewModel.sedMessage(getApplicationContext(), to_user_id, chatMessageBinding.message.getText().toString(), fileList);
            }
        });
        chatMessageBinding.toolBarInclude.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chatMessageBinding.ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatGalleryActivity.class);
                intent.putExtra("id", to_user_id);
                startActivity(intent);
            }
        });
    }

    private void setAdapter() {
        chatListAdapter = new ChatListAdapter(getApplicationContext(), chatList, ActivityChatMessage.this);
        chatMessageBinding.listChatMessage.setHasFixedSize(true);
        chatMessageBinding.listChatMessage.setNestedScrollingEnabled(true);
        chatMessageBinding.listChatMessage.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        chatMessageBinding.listChatMessage.setAdapter(chatListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatMessageViewModel.getMessage(getApplicationContext(), ActivityChatMessage.this, to_user_id);
    }

    @Override
    public void onSuccessGetMessage(LiveData<GetMessageResponse> postResponse) {
        postResponse.observe(ActivityChatMessage.this, new Observer<GetMessageResponse>() {
            @Override
            public void onChanged(GetMessageResponse getMessageResponse) {
                if (getMessageResponse.getCode() == 200 && getMessageResponse.getStatus().equals("OK")) {
                    setListAdapter(getMessageResponse.getData());
                }
            }
        });
    }

    private void setListAdapter(List<GetMessageData> data) {
        chatList = data;
        chatListAdapter.updateList(data);
        chatMessageBinding.listChatMessage.scrollToPosition(data.size() - 1);
    }

    @Override
    public void onCommoStarted() {

    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> userProfileResponse) {
        userProfileResponse.observe(ActivityChatMessage.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse commonResponse) {
                if (commonResponse.getCode() == 200) {
                    chatMessageBinding.message.setText("");
                    chatMessageViewModel.getMessage(getApplicationContext(), ActivityChatMessage.this, to_user_id);
                }
            }
        });
    }

    @Override
    public void onCommonFailure(String message) {

    }
}