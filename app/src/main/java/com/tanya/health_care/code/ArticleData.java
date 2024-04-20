package com.tanya.health_care.code;


public class ArticleData {

    public String uid;
    public String title;
    public String description;
    public String image;
    public String category;
    public String access;

    public ArticleData(){}

    public ArticleData(String uid,String title, String description, String image, String category, String access) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.category = category;
        this.image = image;
        this.access = access;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
