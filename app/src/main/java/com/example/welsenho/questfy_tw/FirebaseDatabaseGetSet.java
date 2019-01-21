package com.example.welsenho.questfy_tw;

public class FirebaseDatabaseGetSet {


    private Boolean isSelected;
    private String editInitImageUploadViewUri;

    //For getting articles properties
    private String Content;
    private String Title;
    private String Upload_Date;
    private String User_Image;
    private String User_Name;
    private String Majors;
    private String Article_ID;

    //For Major choose
    private String Major;



    //GetSet for firebase
    public String getMajor() {
        return Major;
    }

    public void setMajor(String major) {
        Major = major;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getEditInitImageUploadViewUri() {
        return editInitImageUploadViewUri;
    }

    public void setEditInitImageUploadViewUri(String editInitImageUploadViewUri) {
        this.editInitImageUploadViewUri = editInitImageUploadViewUri;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUpload_Date() {
        return Upload_Date;
    }

    public void setUpload_Date(String upload_Date) {
        Upload_Date = upload_Date;
    }

    public String getUser_Image() {
        return User_Image;
    }

    public void setUser_Image(String user_Image) {
        User_Image = user_Image;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getMajors() {
        return Majors;
    }

    public void setMajors(String majors) {
        Majors = majors;
    }

    public String getArticle_ID() {
        return Article_ID;
    }

    public void setArticle_ID(String article_ID) {
        Article_ID = article_ID;
    }
}
