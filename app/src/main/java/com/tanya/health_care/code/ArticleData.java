package com.tanya.health_care.code;


public class ArticleData {

    public String uid;
    public String title;
    public String description;
    public String image;
    public String access;

    public ArticleData(){}

    public ArticleData(String uid,String title, String description, String image, String access) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.image = image;
        this.access = access;
    }
}
