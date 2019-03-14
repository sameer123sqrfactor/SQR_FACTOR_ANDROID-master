package com.user.sqrfactor.Activities.LoginAndSignUp;

import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel.UserSignUpDataClass;
import com.user.sqrfactor.Pojo.CountryClass;

import java.util.ArrayList;

public interface LoginAndSignUpContractInterface  {


    interface loginPresenter{

        //actions
        boolean validateLoginCredentials(String username, String password);
        void onSuccessFulLogin();
        void onFacbookSuccessfulLogin();
        void onLoginFailure(String errorMessage);

        //userEvent
        void manualLoginBtnClick(String username, String password);
        void loginWithMobileBtnClick();
        void loginWithFacebookBtnClick();
        void loginRememberBtnClicked(boolean b);
        void forgetPasswordBtnClicked();


    }

    interface signUpPresenter{
        //actions
        void getCountryName();
        void onFinishedCountryLoading(ArrayList<String> countryName, ArrayList<CountryClass> countryClassArrayList);
        void onSuccessFullSignup(String userMobileNumber, boolean b);
        void onFacbookSuccessfullSignUp();
        void onLoginFailure(String errorMessage);

        //userEvent
        void manualSignUpBtnClick(UserSignUpDataClass userSignUpDataClass);
        void signUpWithMobileBtnClick();
        void signUpWithFacebookBtnClick();


    }


    interface loginView{
        void showProgressBar();
        void hideProgressBar();
        void showErrorMessage(String errorMessage);
        void onSuceesFulLogin();
        void openLoginWithMobileActivity();
        void openResetPasswordActivity();
        void loginWithFacebook();


    }
    interface signUpView{
        void showProgressBar();
        void hideProgressBar();

        void OnSucessFulCountryLoading(ArrayList<String> countryName,ArrayList<CountryClass> countryClassArrayList);
        void showErrorMessage(String errorMessage);
        void openSignUpWithMobileActivity();
        void signUpWithFacebook();
        void goToHomeScreenAfterSuceesFulSignUp();
        void goToSocialFormPageAfterSucessFulSignUp();
        void goToOtpPageAfterSucessFulSignUp(String mobileNumber);

    }

    interface signUpmodel{

        void sendSignUpData(UserSignUpDataClass userSignUpDataClass);
        void getUserDataAfterSignUp();
        void getUserDataAfterFacebookSucessFullSignUp();
        void getCountryName();



    }
    interface loginModel{

        void sendloginData(final String userName, final String password);
        void getUserDataAfterFacebookSucessFullLogin();
        void rememberMeLoginCredentials(String usename,String password,boolean isChecked);
        void clearLoginCredentials();
    }




}
