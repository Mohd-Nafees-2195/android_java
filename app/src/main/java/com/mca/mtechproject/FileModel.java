package com.mca.mtechproject;

public class FileModel {
    String fileName,fileUrl,message;
    int visibility;

    public FileModel() {}

    public FileModel(String fileName, String fileUrl,String message,int visibility) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.message=message;
        this.visibility=visibility;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
