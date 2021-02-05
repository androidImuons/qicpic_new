package com.saqcess.qicpic.view.activity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;
import com.saqcess.qicpic.customeview.ReadMoreTextView;
import com.saqcess.qicpic.databinding.ActivityViewAllCommentBinding;
import com.saqcess.qicpic.model.CommentModel;
import com.saqcess.qicpic.model.CommonResponse;
import com.saqcess.qicpic.model.GetPostRecordModel;
import com.saqcess.qicpic.model.GetPostResponseModel;
import com.saqcess.qicpic.view.adapter.AllCommentViewAdapter;
import com.saqcess.qicpic.view.dialog.ReplyCommentDialog;
import com.saqcess.qicpic.view.dialog.SendPostDialog;
import com.saqcess.qicpic.view.listeners.CommonResponseInterface;
import com.saqcess.qicpic.view.listeners.GetPostDataListener;
import com.saqcess.qicpic.viewmodel.ViewAllCommentViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAllCommentActivity extends BaseActivity implements CommonResponseInterface, GetPostDataListener {
    ActivityViewAllCommentBinding commentBinding;
    public ViewAllCommentViewModel commentViewModel;
    private GetPostRecordModel postDataObject = new GetPostRecordModel();
    private AllCommentViewAdapter allCommentViewAdapter;
    private String tag = "ViewAllCommentActivity";
    private int action_position;
    private ReplyCommentDialog replyDialogInstance;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_all_comment);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        commentViewModel = ViewModelProviders.of(ViewAllCommentActivity.this).get(ViewAllCommentViewModel.class);
        commentBinding.setViewcomment(commentViewModel);
        commentBinding.executePendingBindings();
        commentBinding.setLifecycleOwner(ViewAllCommentActivity.this);
        commentViewModel.commonResponseInterface = ViewAllCommentActivity.this;
        commentViewModel.postDataListener = this;

        Log.d(tag, "post id" + getPostId());
        initUI();
    }

    SessionManager sessionManager;

    private void initUI() {
        commentBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentBinding.swipeLayout.setRefreshing(true);
                commentViewModel.getPost(getApplicationContext(), getPostId().toString());
            }
        });
        sessionManager = new SessionManager(getApplicationContext());
        loadImage(getApplicationContext(), commentBinding.ivUserPhoto, commentBinding.pbImage, sessionManager.getUserDetails().get(sessionManager.KEY_PROFILE_PICTURE), R.drawable.ic_profile);

        allCommentViewAdapter = new AllCommentViewAdapter(getApplicationContext(), postDataObject, ViewAllCommentActivity.this);
        commentBinding.recycleview.setHasFixedSize(true);
        commentBinding.recycleview.setNestedScrollingEnabled(true);
        commentBinding.recycleview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        commentBinding.recycleview.setAdapter(allCommentViewAdapter);

        commentBinding.txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentBinding.etComment.getText().toString().isEmpty()) {
                    showSnackbar(commentBinding.etComment, "Type Comment...!", Snackbar.LENGTH_SHORT);
                } else {
                    commentViewModel.postComment(getApplicationContext(), postDataObject.getId().toString(), commentBinding.etComment.getText().toString());
                }
            }
        });

        commentBinding.llIncludeBottom.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        commentBinding.llIncludeBottom.txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(getPostId()));
                SendPostDialog instance = SendPostDialog.getInstance(bundle, ViewAllCommentActivity.this);
                instance.show(getSupportFragmentManager(), instance.getClass().getSimpleName());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        commentViewModel.getPost(getApplicationContext(), getPostId().toString());
    }

    private Integer getPostId() {
        Bundle extras = getIntent().getExtras();
        return (extras == null ? 0 : extras.getInt("post_id", 0));
    }

    public void updatlist() {
        commentViewModel.getPost(getApplicationContext(), getPostId().toString());
    }

    @Override
    public void onCommoStarted() {

        showProgressDialog("Wait...");
    }

    @Override
    public void onCommonSuccess(LiveData<CommonResponse> userProfileResponse) {
        userProfileResponse.observe(this, new Observer<CommonResponse>() {
            @Override
            public void onChanged(CommonResponse responseModel) {
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        commentBinding.etComment.setText("");
                        commentViewModel.getPost(getApplicationContext(), getPostId().toString());
                    } else {
                        commentBinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(commentBinding.nestedScroll, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    commentBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    @Override
    public void onCommonFailure(String message) {
        commentBinding.swipeLayout.setRefreshing(false);
        hideProgressDialog();
    }

    @Override
    public void onStarted() {
        showProgressDialog("Wait...!");
    }

    @Override
    public void onSuccess(LiveData<GetPostResponseModel> postResponse) {
        postResponse.observe(this, new Observer<GetPostResponseModel>() {
            @Override
            public void onChanged(GetPostResponseModel responseModel) {
                //save access token
                hideProgressDialog();
                try {
                    if (responseModel.getCode() == 200 && responseModel.getStatus().equalsIgnoreCase("OK")) {
                        Log.d("Home", "Response : Code" + responseModel.getCode());
                        setPost(responseModel.getData());
                    } else {
                        commentBinding.swipeLayout.setRefreshing(false);
                        Log.d("Home", "Response fail: Code" + responseModel.getCode());
                        showSnackbar(commentBinding.nestedScroll, responseModel.getMessage(), Snackbar.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                } finally {
                    commentBinding.swipeLayout.setRefreshing(false);
                    hideProgressDialog();
                }
            }
        });
    }

    private void setPost(List<GetPostRecordModel> data) {
        postDataObject = data.get(0);
        setTopData(postDataObject);
        Log.d(tag, "---list size" + postDataObject.getComments().size());
        allCommentViewAdapter.update(postDataObject);
       // allCommentViewAdapter.updatePostion(action_position, postDataObject.getComments().get(action_position));
        if(replyDialogInstance!=null){
            replyDialogInstance.updateReply(data.get(0).getComments().get(action_position),data.get(0).getComments().get(action_position).getReplyList());
        }

    }

    private void setTopData(GetPostRecordModel postDataObject) {
        loadImage(getApplicationContext(), commentBinding.ivPostUserProfile, commentBinding.pbImage, postDataObject.getUser().getProfile_picture(), R.drawable.ic_profile);

        commentBinding.txtUserName.setText(postDataObject.getUser().getFullname());
        if (postDataObject.getCaption().equals("")) {
            commentBinding.txtPostCaption.setText("No Story");
        } else {

            commentBinding.txtPostCaption.setReadMoreTextViewListener(new ReadMoreTextView.ReadMoreTextViewListener() {
                @Override
                public void onReadMoreChange(ReadMoreTextView textView) {

                }
            });
            commentBinding.txtPostCaption.setText(postDataObject.getCaption());
            commentBinding.txtPostCaption.readMore = postDataObject.isReadMore();
            commentBinding.txtPostCaption.setText(postDataObject.getCaption(), TextView.BufferType.NORMAL);
        }
    }

    @Override
    public void onFailure(String message) {
        hideProgressDialog();
        commentBinding.swipeLayout.setRefreshing(false);
    }

    public void likeComment(int position, Integer id) {
        action_position = position;
        commentViewModel.likeComment(getApplicationContext(), String.valueOf(id));
    }

    public void replyToComment(int position, String id, String message) {
        action_position = position;
        commentViewModel.replyToComment(getApplicationContext(), id, message);
    }

    public void getallComment(CommentModel commentModel, int position) {
        action_position = position;

        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(commentModel.getId().toString()));
        bundle.putParcelable("comment", commentModel);
        bundle.putParcelableArrayList("reply", (ArrayList<? extends Parcelable>) commentModel.getReplyList());
         replyDialogInstance = ReplyCommentDialog.getInstance(bundle, ViewAllCommentActivity.this);
        replyDialogInstance.show(getSupportFragmentManager(), replyDialogInstance.getClass().getSimpleName());

    }
}