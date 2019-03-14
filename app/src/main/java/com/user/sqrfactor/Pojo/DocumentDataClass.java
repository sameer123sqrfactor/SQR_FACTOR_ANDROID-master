package com.user.sqrfactor.Pojo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DocumentDataClass implements Serializable {

    private int id,user_id,isApproved;
    private String user_type,graduation_proof,id_proof,architect_proof,registration_certificate,cretaed_at
            ,updated_at,deleted_at;
    private JSONObject jsonObject;

    public DocumentDataClass(int id, int user_id, int isApproved, String user_type, String graduation_proof, String id_proof, String architect_proof, String registration_certificate, String cretaed_at, String updated_at, String deleted_at) {
        this.id = id;
        this.user_id = user_id;
        this.isApproved = isApproved;
        this.user_type = user_type;
        this.graduation_proof = graduation_proof;
        this.id_proof = id_proof;
        this.architect_proof = architect_proof;
        this.registration_certificate = registration_certificate;
        this.cretaed_at = cretaed_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }
    public DocumentDataClass(JSONObject jsonObject) {
        this.jsonObject=jsonObject;


        try {
            this.id = jsonObject.getInt("id");
            this.user_id = jsonObject.getInt("user_id");
            this.isApproved = jsonObject.getInt("isApproved");
            this.user_type = jsonObject.getString("isApproved");
            this.graduation_proof = jsonObject.getString("graduation_proof");
            this.id_proof = jsonObject.getString("id_proof");;
            this.architect_proof = jsonObject.getString("architect_proof");;
            this.registration_certificate = jsonObject.getString("registration_certificate");;
            this.cretaed_at = jsonObject.getString("cretaed_at");;
            this.updated_at = jsonObject.getString("updated_at");;
            this.deleted_at = jsonObject.getString("deleted_at");;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getGraduation_proof() {
        return graduation_proof;
    }

    public void setGraduation_proof(String graduation_proof) {
        this.graduation_proof = graduation_proof;
    }

    public String getId_proof() {
        return id_proof;
    }

    public void setId_proof(String id_proof) {
        this.id_proof = id_proof;
    }

    public String getArchitect_proof() {
        return architect_proof;
    }

    public void setArchitect_proof(String architect_proof) {
        this.architect_proof = architect_proof;
    }

    public String getRegistration_certificate() {
        return registration_certificate;
    }

    public void setRegistration_certificate(String registration_certificate) {
        this.registration_certificate = registration_certificate;
    }

    public String getCretaed_at() {
        return cretaed_at;
    }

    public void setCretaed_at(String cretaed_at) {
        this.cretaed_at = cretaed_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
