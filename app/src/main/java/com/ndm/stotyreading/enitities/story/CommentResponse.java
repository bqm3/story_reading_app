package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class CommentResponse {
    private boolean success;
    private String message;
    private List<Comment> comments;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Comment> getComments() {
        return comments;
    }
}