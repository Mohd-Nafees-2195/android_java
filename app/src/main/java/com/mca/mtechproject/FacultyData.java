package com.mca.mtechproject;

public class FacultyData {
    private  String fullName,email,mobileNumber,department, examiner1, examiner2,password,role,imageURL;

    public FacultyData(){}
    public FacultyData(String fullName, String email, String mobileNumber, String department, String examiner1, String examiner2, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.department = department;
        this.examiner1 = examiner1;
        this.examiner2 = examiner2;
        this.password = password;
        this.role=role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getExaminer1() {
        return examiner1;
    }

    public void setExaminer1(String examiner1) {
        this.examiner1 = examiner1;
    }

    public String getExaminer2() {
        return examiner2;
    }

    public void setExaminer2(String examiner2) {
        this.examiner2 = examiner2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
