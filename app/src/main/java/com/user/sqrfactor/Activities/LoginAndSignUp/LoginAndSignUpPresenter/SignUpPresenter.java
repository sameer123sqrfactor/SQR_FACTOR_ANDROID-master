package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpPresenter;

import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpContractInterface;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel.SignUpModel;
import com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel.UserSignUpDataClass;
import com.user.sqrfactor.Pojo.CountryClass;

import java.util.ArrayList;

public class SignUpPresenter implements LoginAndSignUpContractInterface.signUpPresenter {

     private LoginAndSignUpContractInterface.signUpView signUpView;
     private LoginAndSignUpContractInterface.signUpmodel signUpmodel;
     private boolean isManualSignUpClicked=false ;
     private boolean isSignUpWithFacebookClicked=false ;



    public SignUpPresenter(LoginAndSignUpContractInterface.signUpView signUpView) {
        this.signUpView = signUpView;
        this.signUpmodel=new SignUpModel(this);
    }


    public boolean validateSignUpCredentials(UserSignUpDataClass userSignUpDataClass) {
        return true;
    }

    @Override
    public void getCountryName() {
        signUpmodel.getCountryName();
    }

    @Override
    public void onFinishedCountryLoading(ArrayList<String> countryName, ArrayList<CountryClass> countryClassArrayList) {
        signUpView.OnSucessFulCountryLoading(countryName,countryClassArrayList);
    }

    @Override
    public void onSuccessFullSignup(String mobileNumber, boolean isGoToHomeEnabled) {
        if(isManualSignUpClicked) {
            signUpView.goToOtpPageAfterSucessFulSignUp(mobileNumber);
        }else if(isSignUpWithFacebookClicked){
            if(isGoToHomeEnabled){
                signUpView.goToHomeScreenAfterSuceesFulSignUp();
            }else {
                signUpView.goToSocialFormPageAfterSucessFulSignUp();
            }
        }
    }

    @Override
    public void onFacbookSuccessfullSignUp() {
       signUpmodel.getUserDataAfterFacebookSucessFullSignUp();

    }

    @Override
    public void onLoginFailure(String errorMessage) {

    }

    @Override
    public void manualSignUpBtnClick(UserSignUpDataClass userSignUpDataClass) {
        isManualSignUpClicked=true;
       if(validateSignUpCredentials(userSignUpDataClass)){
          signUpmodel.sendSignUpData(userSignUpDataClass);
       }else {
           signUpView.showErrorMessage("check signup data");
       }

    }

    @Override
    public void signUpWithMobileBtnClick() {

         signUpView.openSignUpWithMobileActivity();
    }

    @Override
    public void signUpWithFacebookBtnClick() {
        isSignUpWithFacebookClicked=true;
         signUpView.signUpWithFacebook();
    }
}
