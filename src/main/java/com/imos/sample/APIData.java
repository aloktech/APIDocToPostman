/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.imos.sample;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alok
 */
public class APIData {

    private String api;

    private List<String> apiUses;

    private String apiVersion;

    private String apiParam;

    private String apiPermission;

    private String apiDescription;

    private String apiName;

    private String apiGroup;

    private String apiSuccess;

    private String apiMethod;
    
    private String apiUrl;

    public APIData() {
        apiUses = new ArrayList<>();
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public List<String> getApiUses() {
        return apiUses;
    }

    public void setApiUses(List<String> apiUses) {
        this.apiUses = apiUses;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getApiParam() {
        return apiParam;
    }

    public void setApiParam(String apiParam) {
        this.apiParam = apiParam;
    }

    public String getApiGroup() {
        return apiGroup;
    }

    public void setApiGroup(String apiGroup) {
        this.apiGroup = apiGroup;
    }

    public String getApiPermission() {
        return apiPermission;
    }

    public void setApiPermission(String apiPermission) {
        this.apiPermission = apiPermission;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiSuccess() {
        return apiSuccess;
    }

    public void setApiSuccess(String apiSuccess) {
        this.apiSuccess = apiSuccess;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public String toString() {
        return "APIData{" + "api=" + api + ", apiUses=" + apiUses + ", apiVersion=" 
            + apiVersion + ", apiParam=" + apiParam + ", apiPermission=" + apiPermission 
            + ", apiDescription=" + apiDescription + ", apiName=" + apiName + ", apiGroup=" 
            + apiGroup + ", apiSuccess=" + apiSuccess + ", apiMethod=" + apiMethod + ", apiUrl=" 
            + apiUrl + '}';
    }


}
