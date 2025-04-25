package com.ndm.stotyreading.enitities.user;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String UserName;
    private String Password;
    private String Fullname;
    private Integer Age;

    public RegisterRequest() {
    }

    public RegisterRequest(String userName, String password, String fullname, Integer age) {
        UserName = userName;
        Password = password;
        Fullname = fullname;
        Age = age;
    }    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String fullname) {
        Fullname = fullname;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer age) {
        Age = age;
    }



    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

