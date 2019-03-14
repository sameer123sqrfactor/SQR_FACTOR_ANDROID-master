package com.user.sqrfactor.Activities.SettingModule.SettingPresenters;

import com.user.sqrfactor.Activities.SettingModule.SettingInterface;
import com.user.sqrfactor.Activities.SettingModule.SettingModels.SettingModel;

public class SettingPresenter implements SettingInterface.settingPresenter {


    SettingInterface.settingModel settingModel;
    SettingInterface.settingView settingView;

    public SettingPresenter(SettingInterface.settingView settingView) {
        this.settingView = settingView;
        settingModel=new SettingModel(this);
    }

    @Override
    public void getUserSettingDataFromServer() {
         settingModel.getUserSettingDataFromServerAndUpdateLocalDb();
    }
}
