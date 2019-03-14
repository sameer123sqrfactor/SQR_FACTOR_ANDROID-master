package com.user.sqrfactor.Pojo;

import org.json.JSONArray;

import java.util.ArrayList;

public class SubmissionClass {
    private String user_id;
    private String id;
    private String slug;
    private String title;
    private String code;
    private String coverUrl;
    private String pdfUrl;
    private JSONArray commentsArray;
    private JSONArray likesArray;
    private ArrayList<Integer> participationIdArray;

    public SubmissionClass(String id, String title, String code, String coverUrl, String pdfUrl, JSONArray commentsArray,JSONArray  likesArray) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.coverUrl = coverUrl;
        this.pdfUrl = pdfUrl;
        this.commentsArray = commentsArray;
        this.likesArray=likesArray;
    }

    public SubmissionClass(String user_id,String id, String title, String code, String coverUrl, JSONArray commentsArray,JSONArray  likesArray,String slug,ArrayList<Integer> participationIdArray) {
        this.user_id=user_id;
        this.id = id;
        this.title = title;
        this.code = code;
        this.coverUrl = coverUrl;
        this.commentsArray = commentsArray;
        this.likesArray=likesArray;
        this.slug=slug;
        this.participationIdArray=participationIdArray;
    }

    public ArrayList<Integer> getParticipationIdArray() {
        return participationIdArray;
    }

    public void setParticipationIdArray(ArrayList<Integer> participationIdArray) {
        this.participationIdArray = participationIdArray;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public JSONArray getLikesArray() {
        return likesArray;
    }

    public void setLikesArray(JSONArray likesArray) {
        this.likesArray = likesArray;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public JSONArray getCommentsArray() {
        return commentsArray;
    }

    public void setCommentsArray(JSONArray commentsArray) {
        this.commentsArray = commentsArray;
    }
}
