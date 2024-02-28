package com.tanya.health_care.code;


public class GetSplittedPathChild {
    public String getSplittedPathChild(String email){
        email = email.replaceAll("[^A-Za-zA]", "");
//        autosave.setDbSetUserTableName(email);
        return email;
    }
}
