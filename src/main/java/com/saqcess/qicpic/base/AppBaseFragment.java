package com.saqcess.qicpic.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.view.fragment.BaseFragment;

public class AppBaseFragment extends BaseDialogFragment {
    private Dialog alertDialogProgressBar;
    @Override
    public int getLayoutResourceId() {
        return 0;
    }

    @Override
    public void initializeComponent() {

    }

    @Override
    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
        return 0;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressBar();
    }


    public void displayProgressBar(boolean isCancellable) {
        displayProgressBar(isCancellable, "");
    }

    public void displayProgressBar(boolean isCancellable, String loaderMsg) {
        dismissProgressBar();
        if (getActivity() == null) return;
        alertDialogProgressBar = new Dialog(getActivity(),
                R.style.DialogWait);
        alertDialogProgressBar.setCancelable(isCancellable);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.dialog_wait);
        TextView tv_loader_msg = alertDialogProgressBar.findViewById(R.id.tv_loader_msg);
        if (loaderMsg != null && !loaderMsg.trim().isEmpty()) {
            tv_loader_msg.setText(loaderMsg);
        } else {
            tv_loader_msg.setVisibility(View.GONE);
        }

        try {
            alertDialogProgressBar.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            alertDialogProgressBar.show();
        } catch (Exception ignore) {

        }
    }


    public void dismissProgressBar() {
        try {
            if (getActivity() != null && alertDialogProgressBar != null && alertDialogProgressBar.isShowing()) {
                alertDialogProgressBar.dismiss();
            }
        } catch (Exception ignore) {

        }
    }



}
