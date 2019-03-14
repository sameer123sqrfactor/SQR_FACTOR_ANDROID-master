package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpPresenter;

import android.text.TextUtils;

import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpContractInterface;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel.LoginModel;

public class LoginPresenter implements LoginAndSignUpContractInterface.loginPresenter {

    private LoginAndSignUpContractInterface.loginView loginView;
    private LoginAndSignUpContractInterface.loginModel loginModel;
    private boolean rememberLoginCheckbox=false;



    public LoginPresenter(LoginAndSignUpContractInterface.loginView loginView) {
        this.loginView=loginView;
        loginModel=new LoginModel(this);
    }

    @Override
    public boolean validateLoginCredentials(String username, String password) {

    return true;

    }

    @Override
    public void manualLoginBtnClick(String username, String password) {
        loginView.showProgressBar();
        if(validateLoginCredentials(username,password)){



            if (TextUtils.isEmpty(username)) {
                loginView.showErrorMessage("email is required");
            } else if (TextUtils.isEmpty(password)) {
                loginView.showErrorMessage("password is required");
            } else {
                if (rememberLoginCheckbox) {
//                    loginPrefsEditor.putBoolean("saveLogin", true);
//                    loginPrefsEditor.putString("username", username);
//                    loginPrefsEditor.putString("password", password);
//                    loginPrefsEditor.commit();

                    loginModel.rememberMeLoginCredentials(username,password,true);
                    loginModel.sendloginData(username,password);
                } else {
                    loginModel.clearLoginCredentials();
                    loginModel.sendloginData(username,password);

                }
            }




        }else {
            loginView.hideProgressBar();
            loginView.showErrorMessage("login credentials not correct");
        }

    }

    @Override
    public void loginRememberBtnClicked(boolean b) {

        if(b){
            rememberLoginCheckbox=true;

        }else {
            rememberLoginCheckbox=false;
        }


    }

    @Override
    public void loginWithMobileBtnClick() {

        loginView.openLoginWithMobileActivity();
    }

    @Override
    public void loginWithFacebookBtnClick() {
        loginView.loginWithFacebook();

    }


    @Override
    public void forgetPasswordBtnClicked() {
      loginView.openResetPasswordActivity();
    }

    @Override
    public void onSuccessFulLogin() {
        loginView.hideProgressBar();
        loginView.onSuceesFulLogin();

    }

    @Override
    public void onFacbookSuccessfulLogin() {
       loginModel.getUserDataAfterFacebookSucessFullLogin();

    }

    @Override
    public void onLoginFailure(String errorMessage) {
       loginView.showErrorMessage(errorMessage);
    }
}
