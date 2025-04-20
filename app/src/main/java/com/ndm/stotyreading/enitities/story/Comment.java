package com.ndm.stotyreading.enitities.story;

import com.ndm.stotyreading.enitities.user.LoginResponse;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String userName;
    private String content;
    private String createdAt;
    private LoginResponse.User user;

    public Comment(String id, String userName, String content) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(LoginResponse.User user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;  // ✅ Cập nhật nội dung bình luận
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public LoginResponse.User getUser() {
        return user;
    }
}
