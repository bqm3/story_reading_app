package com.ndm.stotyreading.enitities.story;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Story implements Serializable {
    private String id;
    private String title;
    private String author;
    private String genre_id;
    private String description;
    private String cover_image;
    private String status;
    private int views;
    private double rating;
    private boolean isLiked;
    private String created_at;
    private String updated_at;

    private Category category;
    private List<Chapter> chapters;
    private List<Tag> tags;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    public String getId() {
        return Objects.toString(id, "");
    }

    public String getTitle() {
        return Objects.toString(title, "");
    }

    public String getAuthor() {
        return Objects.toString(author, "");
    }

    public String getGenreId() {
        return Objects.toString(genre_id, "");
    }

    public String getDescription() {
        return Objects.toString(description, "");
    }

    public String getCoverImage() {
        return Objects.toString(cover_image, "");
    }

    public String getStatus() {
        return Objects.toString(status, "");
    }

    public int getViews() {
        return views;
    }

    public double getRating() {
        return rating;
    }

    public String getCreatedAt() {
        return Objects.toString(created_at, "");
    }

    public String getUpdatedAt() {
        return Objects.toString(updated_at, "");
    }

    public Category getCategory() {
        return category;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public List<Tag> getTags() {
        return tags;
    }
}