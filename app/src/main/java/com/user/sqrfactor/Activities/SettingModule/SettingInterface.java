package com.user.sqrfactor.Activities.SettingModule;

public interface SettingInterface {

    interface settingPresenter{

        void getUserSettingDataFromServer();

    }

    interface settingModel{

        void getUserSettingDataFromServerAndUpdateLocalDb();
    }

    interface settingView{

    }
}
