package com.saqcess.qicpic.appupdate;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.saqcess.qicpic.repository.AppUpdateRepository;


/**
 * Created by ubuntu on 29/5/17.
 */

public class AppUpdateChecker  {


    Context context;
    AppUpdateAvailableListener onAppUpdateAvailable;

    public void setOnAppUpdateAvailable(AppUpdateAvailableListener onAppUpdateAvailable) {
        this.onAppUpdateAvailable = onAppUpdateAvailable;
    }

    public AppUpdateChecker(Context context, AppUpdateAvailableListener onAppUpdateAvailable) {
        this.context = context;
        this.onAppUpdateAvailable = onAppUpdateAvailable;
    }
    public LiveData<AppVersionModel> appVersionModelLiveData;
    public void checkForUpdate() {
      //api call
        Long version_code = AppUpdateUtils.getAppInstalledVersionCode(context).longValue();
        AppUpdateRepository appUpdateRepository=new AppUpdateRepository();
        appVersionModelLiveData= appUpdateRepository.appUpdate(context,version_code);
        onAppUpdateAvailable.appUpdateAvailable(appVersionModelLiveData);
    }
    public interface AppUpdateAvailableListener {
        void appUpdateAvailable(LiveData<AppVersionModel> appUpdatemodal);
    }

}
