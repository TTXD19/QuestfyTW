package com.example.welsenho.questfy_tw;

public class FirebaseDatabaseGetSet{


    private Boolean isSelected;
    private String editInitImageUploadViewUri;

    //For getting articles properties
    private String Content;
    private String Title;
    private String Upload_Date;
    private String User_Image;
    private String User_image_uri;
    private String User_Name;
    private String Majors;
    private String Article_ID;
    private String userStatusMessage;
    private String userSpeciality;
    private String createDate;
    private String MeetDate;
    private String MeetTime;
    private String MeetPlace;
    private String MeetAddress;
    private String isMeet;
    private long uploadTimeStamp;
    private int AnswerCount;
    private int Article_like_count;

    //For Major choose
    private String Major;

    //For readAnswer
    private String AnswerContent;
    private String AnswerID;
    private String UpdateDate;
    private String UserID;
    private String UserImage;
    private String UserName;

    //For gettingUserProfile
    private String ID;
    private String userUid;

    //For CheckingUserCompleteInfo
    private String CompleteInformationCheck;

    //For getting university name and it's courses name
    private String schoolName;
    private String courseName;

    //For getting request friend list
    private String SenderUid;
    private String RequestName;
    private String RecieverUid;


    //For getting friend information
    private String FriendName;
    private String FriendImage;
    private String FriendUid;
    private String LatestMessage;

    //For getting message information
    private String Message;
    private String MessageUserUid;
    private String MessageType;

    //For getting daily question information
    private String dailyQuestionArticleUid;
    private String dailyQuestionType;
    private String dailyQuestionImage;
    private String dailyQuestionTitle;
    private String dailyQuestionSubject;
    private String dailyQuestionAuthor;
    private String dailyQuestionContent;
    private String dailyQuestionComment;


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

    public String getAnswerContent() {
        return AnswerContent;
    }

    public void setAnswerContent(String answerContent) {
        AnswerContent = answerContent;
    }

    public String getAnswerID() {
        return AnswerID;
    }

    public void setAnswerID(String answerID) {
        AnswerID = answerID;
    }

    public String getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(String updateDate) {
        UpdateDate = updateDate;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getCompleteInformationCheck() {
        return CompleteInformationCheck;
    }

    public void setCompleteInformationCheck(String completeInformationCheck) {
        CompleteInformationCheck = completeInformationCheck;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSenderUid() {
        return SenderUid;
    }

    public void setSenderUid(String senderUid) {
        SenderUid = senderUid;
    }

    public String getRequestName() {
        return RequestName;
    }

    public void setRequestName(String requestName) {
        RequestName = requestName;
    }

    public String getRecieverUid() {
        return RecieverUid;
    }

    public void setRecieverUid(String recieverUid) {
        RecieverUid = recieverUid;
    }

    public String getFriendName() {
        return FriendName;
    }

    public void setFriendName(String friendName) {
        FriendName = friendName;
    }

    public String getFriendImage() {
        return FriendImage;
    }

    public void setFriendImage(String friendImage) {
        FriendImage = friendImage;
    }

    public String getFriendUid() {
        return FriendUid;
    }

    public void setFriendUid(String friendUid) {
        FriendUid = friendUid;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMessageUserUid() {
        return MessageUserUid;
    }

    public void setMessageUserUid(String messageUserUid) {
        MessageUserUid = messageUserUid;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getLatestMessage() {
        return LatestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        LatestMessage = latestMessage;
    }

    public int getArticle_like_count() {
        return Article_like_count;
    }

    public void setArticle_like_count(int article_like_count) {
        Article_like_count = article_like_count;
    }

    public String getDailyQuestionArticleUid() {
        return dailyQuestionArticleUid;
    }

    public void setDailyQuestionArticleUid(String dailyQuestionArticleUid) {
        this.dailyQuestionArticleUid = dailyQuestionArticleUid;
    }

    public String getDailyQuestionType() {
        return dailyQuestionType;
    }

    public void setDailyQuestionType(String dailyQuestionType) {
        this.dailyQuestionType = dailyQuestionType;
    }

    public String getDailyQuestionTitle() {
        return dailyQuestionTitle;
    }

    public void setDailyQuestionTitle(String dailyQuestionTitle) {
        this.dailyQuestionTitle = dailyQuestionTitle;
    }

    public String getDailyQuestionSubject() {
        return dailyQuestionSubject;
    }

    public void setDailyQuestionSubject(String dailyQuestionSubject) {
        this.dailyQuestionSubject = dailyQuestionSubject;
    }

    public String getDailyQuestionAuthor() {
        return dailyQuestionAuthor;
    }

    public void setDailyQuestionAuthor(String dailyQuestionAuthor) {
        this.dailyQuestionAuthor = dailyQuestionAuthor;
    }

    public String getDailyQuestionImage() {
        return dailyQuestionImage;
    }

    public void setDailyQuestionImage(String dailyQuestionImage) {
        this.dailyQuestionImage = dailyQuestionImage;
    }

    public String getDailyQuestionContent() {
        return dailyQuestionContent;
    }

    public void setDailyQuestionContent(String dailyQuestionContent) {
        this.dailyQuestionContent = dailyQuestionContent;
    }

    public String getDailyQuestionComment() {
        return dailyQuestionComment;
    }

    public void setDailyQuestionComment(String dailyQuestionComment) {
        this.dailyQuestionComment = dailyQuestionComment;
    }

    public String getUserStatusMessage() {
        return userStatusMessage;
    }

    public void setUserStatusMessage(String userStatusMessage) {
        this.userStatusMessage = userStatusMessage;
    }

    public String getUserSpeciality() {
        return userSpeciality;
    }

    public void setUserSpeciality(String userSpeciality) {
        this.userSpeciality = userSpeciality;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUser_image_uri() {
        return User_image_uri;
    }

    public void setUser_image_uri(String user_image_uri) {
        User_image_uri = user_image_uri;
    }

    public String getMeetDate() {
        return MeetDate;
    }

    public void setMeetDate(String meetDate) {
        MeetDate = meetDate;
    }

    public String getMeetTime() {
        return MeetTime;
    }

    public void setMeetTime(String meetTime) {
        MeetTime = meetTime;
    }

    public String getMeetPlace() {
        return MeetPlace;
    }

    public void setMeetPlace(String meetPlace) {
        MeetPlace = meetPlace;
    }

    public String getMeetAddress() {
        return MeetAddress;
    }

    public void setMeetAddress(String meetAddress) {
        MeetAddress = meetAddress;
    }

    public int getAnswerCount() {
        return AnswerCount;
    }

    public void setAnswerCount(int answerCount) {
        AnswerCount = answerCount;
    }

    public String getIsMeet() {
        return isMeet;
    }

    public void setIsMeet(String isMeet) {
        this.isMeet = isMeet;
    }

    public long getUploadTimeStamp() {
        return uploadTimeStamp;
    }

    public void setUploadTimeStamp(long uploadTimeStamp) {
        this.uploadTimeStamp = uploadTimeStamp;
    }
}
