package com.example.welsenho.questfy_tw;

public class FirebaseDatabaseGetSet {

    private String Major;
    private Boolean isSelected;
    private String editInitImageUploadViewUri;

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
}
