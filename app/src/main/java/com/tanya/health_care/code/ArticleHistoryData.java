package com.tanya.health_care.code;

import java.util.Date;

public class ArticleHistoryData {

    public String uid;
    public String articleUid;
    public Date lastAdded;

    public ArticleHistoryData(){}

    public ArticleHistoryData(String uid, String articleUid, Date lastAdded) {
        this.uid = uid;
        this.articleUid = articleUid;
        this.lastAdded = lastAdded;
    }
}
