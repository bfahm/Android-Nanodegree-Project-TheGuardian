package com.xencosworks.ianguard;

import java.util.Date;

/**
 * Created by Bola on 2/7/2019.
 */

public class Article {
    private String webTitle;
    private String sectionName;
    private Date datePublished;
    private String webUrl;
    private String author;

    public Article(String webTitle, String sectionName, Date datePublished, String author, String webUrl) {
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.datePublished = datePublished;
        this.author = author;
        this.webUrl = webUrl;
    }


    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getAuthor() {
        return author;
    }
}
