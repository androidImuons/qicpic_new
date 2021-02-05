package com.saqcess.qicpic.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.base.AppBaseDialog;
import com.saqcess.qicpic.databinding.DialogReplyCommentBinding;
import com.saqcess.qicpic.model.CommentModel;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.FolloweDataModel;
import com.saqcess.qicpic.model.FollowingUserResponse;
import com.saqcess.qicpic.view.activity.BaseActivity;
import com.saqcess.qicpic.view.activity.ViewAllCommentActivity;
import com.saqcess.qicpic.view.adapter.FollowingUserList;
import com.saqcess.qicpic.view.adapter.ReplyCommentListAdapter;
import com.saqcess.qicpic.view.fragment.BaseFragment;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.FollowingUserListInterface;
import com.saqcess.qicpic.viewmodel.ViewAllCommentViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentDialog extends AppBaseDialog implements
        CommonResponseInterface, FollowingUserListInterface, EditCommentDialog.SendCallBack {

    private String tag = "ReplyCommentDialog";
    public ViewAllCommentViewModel shareUserListViewMode;
    private List<FolloweDataModel> followingList = new ArrayList<>();
    private FollowingUserList followingListAdapter;
    private SessionManager session;
    private boolean is_like;
    private ReplyCommentListAdapter commentListAdapter;
    private int activie_postion;

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_reply_comment;
    }

    public ArrayList<CommentModel> getReply() {
        Bundle extras = getArguments();
        ArrayList<CommentModel> commentModels = new ArrayList<>();
        return (extras == null ? commentModels : extras.getParcelableArrayList("reply"));
    }

    public CommentModel getCommentModel() {
        Bundle extras = getArguments();
        return (extras == null ? null : extras.getParcelable("comment"));
    }

    //    public static ReplyCommentDialog getInstance(Bundle bundle, OzogramHomeActivity galleryActivity) {
//        ReplyCommentDialog dialog = new ReplyCommentDialog();
//        dialog.setArguments(bundle);
//        return dialog;
//    }
    static ViewAllCommentActivity CommentActivity;

    public static ReplyCommentDialog getInstance(Bundle bundle, ViewAllCommentActivity allCommentActivity) {
        ReplyCommentDialog dialog = new ReplyCommentDialog();
        CommentActivity = allCommentActivity;
        dialog.setArguments(bundle);
        return dialog;
    }

    private String getCommentId() {
        Bundle extras = getArguments();
        return (extras == null ? "0" : extras.getString("id", "0"));
    }

    @Override
    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
        return 0;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.dimAmount = 0.8f;

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout bottom_sheet;
    CardView cv_data;
    CircleImageView iv_profile_image;
    EditText edt_message;
    EditText edt_search;
    RecyclerView recycle_replyList;
    private SessionManager sessionManager;

    DialogReplyCommentBinding sendPostBinding;

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        sendPostBinding = DataBindingUtil.bind(getView());
        shareUserListViewMode = ViewModelProviders.of(this).get(ViewAllCommentViewModel.class);
        shareUserListViewMode.commonResponseInterface = this;
        sendPostBinding.setCommentreply(shareUserListViewMode);
        bottom_sheet = getView().findViewById(R.id.bottom_sheet);
        cv_data = getView().findViewById(R.id.cv_data);
        initializeBottomSheet();
         commentListAdapter = new ReplyCommentListAdapter(getContext(), getReply(), ReplyCommentDialog.this);
        sendPostBinding.recycleReplyList.setHasFixedSize(true);
        sendPostBinding.recycleReplyList.setNestedScrollingEnabled(true);
        sendPostBinding.recycleReplyList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        sendPostBinding.recycleReplyList.setAdapter(commentListAdapter);

        session = new SessionManager(getActivity());
        sendPostBinding.txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        setComment();
    }
    public void updateReply(CommentModel comments, List<CommentModel> commentModel){
        setCommentmodel(comments);
        setreply(commentModel);
        commentListAdapter.updatePostion(activie_postion,commentModel);
        setComment();
    }

    private void setreply(List<CommentModel> commentModel) {
        Bundle extras = getArguments();
        extras.putParcelableArrayList("reply", (ArrayList<? extends Parcelable>) commentModel);
    }

    private void setCommentmodel(CommentModel commentModel) {
        Bundle extras = getArguments();
        extras.putParcelable("comment",commentModel);
    }

    private void setComment() {
        sendPostBinding.txtUserName.setText(getCommentModel().getFullname());
        sendPostBinding.tvMessage.setText(getCommentModel().getComment());
        sendPostBinding.txtTime.setText(getCommentModel().getTime());
        sendPostBinding.txtTotalLike.setText(getCommentModel().getComment_likes_count() + " Like");
        ((BaseActivity) getActivity()).loadImage(getActivity(), sendPostBinding.ivProfile, sendPostBinding.pbImage, getCommentModel().getProfile_picture(), R.drawable.ic_profile);
        ((BaseActivity) getActivity()).loadImage(getContext(), sendPostBinding.ivUserPhoto, sendPostBinding.pbImage, session.getUserDetails().get(session.KEY_PROFILE_PICTURE), R.drawable.ic_profile);

        sendPostBinding.etComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtReply(0, getCommentModel());
            }
        });

        sendPostBinding.txtReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtReply(0, getCommentModel());
            }
        });


        is_like = false;
        for (int i = 0; i < getCommentModel().getComment_like_users().size(); i++) {
            if (getCommentModel().getComment_like_users().get(i).getUserId().equals(session.getUserDetails().get(session.KEY_USERID))) {
                is_like = true;
                break;
            } else {
                is_like = false;
            }
        }
        if (is_like) {
            sendPostBinding.ivHeart.setBackgroundResource(R.drawable.ic_heart_fill);
            sendPostBinding.ivHeart.setTag(1);
            Log.d(tag, "---like --");
        } else {
            sendPostBinding.ivHeart.setBackgroundResource(R.drawable.ic_heart);
            sendPostBinding.ivHeart.setTag(0);
            Log.d(tag, "--no-like --");
        }


        sendPostBinding.ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag, "---like click--");
                if ((Integer) view.getTag() == 0) {
                    shareUserListViewMode.likeComment(getContext(), getCommentModel().getId().toString());
                }
            }
        });

    }


    private void initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onCommoStarted() {

    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> commonResponseLiveData) {
        commonResponseLiveData.observe(ReplyCommentDialog.this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse responseModel) {
                try {
                    CommentActivity.updatlist();
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onCommonFailure(String message) {
        showSnackbar(sendPostBinding.recycleReplyList, message, Snackbar.LENGTH_SHORT);

    }

    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    @Override
    public void onSuccessFollowingUser(LiveData<FollowingUserResponse> postResponse) {
        postResponse.observe(this, new Observer<FollowingUserResponse>() {
            @Override
            public void onChanged(FollowingUserResponse responseModel) {
                try {


                } catch (Exception e) {
                } finally {

                }
            }
        });
    }


    public void edtReply(int position, CommentModel commentModel) {
        activie_postion=position;
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(commentModel.getId()));
        EditCommentDialog instance = EditCommentDialog.getInstance(bundle);
        instance.show(getChildFragmentManager(), instance.getClass().getSimpleName());
        instance.sendCallBack = (EditCommentDialog.SendCallBack) ReplyCommentDialog.this;
    }

    @Override
    public void sendMessage(String msg, String pos) {
        shareUserListViewMode.replyToComment(getContext(), pos, msg);
    }

    @Override
    public void sendError(String err) {

    }
}
