package com.user.sqrfactor.Pojo;

import org.json.JSONArray;

import java.util.ArrayList;

public class ResultClass {

    private int type;
    private String id;
    private String coverUrl;
    private String title;
    private String code;
    private int likesCount;
    private int commentsCount;
    private String prizeTitle;
    private String heading;
    private JSONArray likesArray;
    private JSONArray commentArray;
    private ArrayList<Integer> participationIdArray;
    private String slug;


    public ResultClass(int type, String id, String coverUrl, String title, String code, int likesCount, int commentsCount, String prizeTitle, String heading) {
        this.type = type;
        this.id = id;
        this.coverUrl = coverUrl;
        this.title = title;
        this.code = code;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.prizeTitle = prizeTitle;
        this.heading = heading;
    }

    public ResultClass(int type, String id, String coverUrl, String title, String code, int likesCount, int commentsCount, String prizeTitle, String heading,JSONArray likesArray,JSONArray commentArray,ArrayList<Integer> participationIdArray,String slug) {
        this.type = type;
        this.id = id;
        this.coverUrl = coverUrl;
        this.title = title;
        this.code = code;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.prizeTitle = prizeTitle;
        this.heading = heading;
        this.likesArray=likesArray;
        this.commentArray=commentArray;
        this.participationIdArray=participationIdArray;
        this.slug=slug;
    }

    public ArrayList<Integer> getParticipationIdArray() {
        return participationIdArray;
    }

    public void setParticipationIdArray(ArrayList<Integer> participationIdArray) {
        this.participationIdArray = participationIdArray;
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

    public JSONArray getCommentArray() {
        return commentArray;
    }

    public void setCommentArray(JSONArray commentArray) {
        this.commentArray = commentArray;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getPrizeTitle() {
        return prizeTitle;
    }

    public void setPrizeTitle(String prizeTitle) {
        this.prizeTitle = prizeTitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
