package com.ndm.stotyreading.enitities.story;

public class FavoriteResponse {
    private String message;
    private boolean favorited;

    public FavoriteResponse(String message, boolean favorited) {
        this.message = message;
        this.favorited = favorited;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    // getters & setters
}
