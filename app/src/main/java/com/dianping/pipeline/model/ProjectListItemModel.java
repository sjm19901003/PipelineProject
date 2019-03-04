package com.dianping.pipeline.model;


import com.dianping.pipeline.BaseModel;

public class ProjectListItemModel extends BaseModel {
    public String projectName;
    public String projectCity;
    public String projectDate;
    public String projectOwner;

    public ProjectListItemModel() {
        this("", "", "", "");
    }

    public ProjectListItemModel(String name, String city, String date, String owner) {
        this.projectName = name;
        this.projectCity = city;
        this.projectDate = date;
        this.projectOwner = owner;
    }

    public String toString() {
        return projectName + ";" + projectCity + ";" + projectDate + ";" + projectOwner;
    }
}
