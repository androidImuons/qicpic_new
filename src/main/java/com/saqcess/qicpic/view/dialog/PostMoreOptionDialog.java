package com.saqcess.qicpic.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.DeviceScreenUtil;
import com.saqcess.qicpic.base.AppBaseFragment;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.repository.FollowUnFollow;
import com.saqcess.qicpic.view.fragment.BaseFragment;

import java.util.HashMap;
import java.util.Map;

public class PostMoreOptionDialog extends AppBaseFragment {
  public   UnfollowUser unfollowUser;
    public static PostMoreOptionDialog getInstance(Bundle bundle) {
        PostMoreOptionDialog messageDialog = new PostMoreOptionDialog();
        messageDialog.setArguments(bundle);

        return messageDialog;
    }
    private String getPostId() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("id", "0"));
    }
    private String getUId() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("u_id", "0"));
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_more_option;
    }

    @Override
    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
        return 0;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = DeviceScreenUtil.getInstance().getWidth(0.90f);
        wlmp.dimAmount = 0.8f;
    }

TextView txt_share;
    TextView txtUnfollow;
    @Override
    public void initializeComponent() {
        super.initializeComponent();
        txt_share=getView().findViewById(R.id.txt_share_to);
        txtUnfollow=getView().findViewById(R.id.txt_unfollow);

        onClick();
    }
    private LiveData<CommonResponse> commonResponseLiveData;
    private void onClick() {
        txt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id",getPostId());
                SendPostDialog instance = SendPostDialog.getInstance(bundle );
                instance.show(getChildFragmentManager(), instance.getClass().getSimpleName());
            }
        });
        txtUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> likeparam = new HashMap<>();
                likeparam.put("user_id", getUId());
                likeparam.put("action", "unfollow");
                commonResponseLiveData = new MutableLiveData<CommonResponse>();
                commonResponseLiveData = new FollowUnFollow().followUnFollow(likeparam, getContext());
                dismiss();
                Toast.makeText(getContext(),"UnFollow Successfully...!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface UnfollowUser{
        public void onSuccessUnFollow(LiveData<CommonResponse> data);
    }
}
