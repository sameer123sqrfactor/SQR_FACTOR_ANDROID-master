package com.user.sqrfactor.Activities.LoginAndSignUp.LoginAndSignUpModel;

import android.text.TextUtils;

import com.user.sqrfactor.Fragments.MDToast;

import java.io.Serializable;

public class UserSignUpDataClass implements Serializable {

    private String userType,firstName,lastName,userName,userEmail,userMobileNumber,userPassword,confirmPassword,
                companyName_text,organizationName_text,country_val,collegeName_text;

    public UserSignUpDataClass(String userType, String firstName, String lastName, String userName, String userEmail, String userMobileNumber, String userPassword, String confirmPassword, String companyName_text, String organizationName_text, String country_val, String collegeName_text) {
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userMobileNumber = userMobileNumber;
        this.userPassword = userPassword;
        this.confirmPassword = confirmPassword;
        this.companyName_text = companyName_text;
        this.organizationName_text = organizationName_text;
        this.country_val = country_val;
        this.collegeName_text = collegeName_text;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCompanyName_text() {
        return companyName_text;
    }

    public void setCompanyName_text(String companyName_text) {
        this.companyName_text = companyName_text;
    }

    public String getOrganizationName_text() {
        return organizationName_text;
    }

    public void setOrganizationName_text(String organizationName_text) {
        this.organizationName_text = organizationName_text;
    }

    public String getCountry_val() {
        return country_val;
    }

    public void setCountry_val(String country_val) {
        this.country_val = country_val;
    }

    public String getCollegeName_text() {
        return collegeName_text;
    }

    public void setCollegeName_text(String collegeName_text) {
        this.collegeName_text = collegeName_text;
    }
}
