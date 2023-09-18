package com.mca.mtechproject;

public class StudentData {

    private String fullName,rollNumber,email,mobileNumber,course,password,guideEmail,examiner1Email,examiner2Email,timeDuration,imageURL;
    public  StudentData(){}     // Needed for Firebase

    public StudentData(String fullName, String rollNumber, String email, String mobileNumber, String course, String password,String guideEmail,String examiner1Email,String examiner2Email,String timeDuration) {
        this.fullName = fullName;
        this.rollNumber = rollNumber;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.course = course;
        this.password = password;
        this.guideEmail=guideEmail;
        this.examiner1Email=examiner1Email;
        this.examiner2Email=examiner2Email;
        this.timeDuration=timeDuration;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(String timeDuration) {
        this.timeDuration = timeDuration;
    }

    public String getGuideEmail() {
        return guideEmail;
    }

    public void setGuideEmail(String guideEmail) {
        this.guideEmail = guideEmail;
    }

    public String getExaminer1Email() {
        return examiner1Email;
    }

    public void setExaminer1Email(String examiner1Email) {
        this.examiner1Email = examiner1Email;
    }

    public String getExaminer2Email() {
        return examiner2Email;
    }

    public void setExaminer2Email(String examiner2Email) {
        this.examiner2Email = examiner2Email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
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

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
