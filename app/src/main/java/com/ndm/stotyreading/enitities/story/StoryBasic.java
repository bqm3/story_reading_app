package com.ndm.stotyreading.enitities.story;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class StoryBasic implements Parcelable {
    private String id;
    private String title;
    private String author;
    private String genre_id;
    private String description;
    private String cover_image;
    private String status;
    private List<Chapter> chapters;

    // Constructor
    public StoryBasic(String id, String title, String author, String genre_id,
                      String description, String cover_image, String status, List<Chapter> chapters) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre_id = genre_id;
        this.description = description;
        this.cover_image = cover_image;
        this.status = status;
        this.chapters = chapters;
    }

    public StoryBasic() {

    }


    // Getters...
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenreId() { return genre_id; }
    public String getDescription() { return description; }
    public String getCoverImage() { return cover_image; }
    public String getStatus() { return status; }
    public List<Chapter> getChapters() { return chapters; }

    // 🔽 Parcelable part
    protected StoryBasic(Parcel in) {
        id = in.readString();
        title = in.readString();
        author = in.readString();
        genre_id = in.readString();
        description = in.readString();
        cover_image = in.readString();
        status = in.readString();
        chapters = in.createTypedArrayList(Chapter.CREATOR); // 👈 vì Chapter là Parcelable
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(genre_id);
        dest.writeString(description);
        dest.writeString(cover_image);
        dest.writeString(status);
        dest.writeTypedList(chapters);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoryBasic> CREATOR = new Creator<StoryBasic>() {
        @Override
        public StoryBasic createFromParcel(Parcel in) {
            return new StoryBasic(in);
        }

        @Override
        public StoryBasic[] newArray(int size) {
            return new StoryBasic[size];
        }
    };

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public void setId(String storyId) {
        this.id = storyId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String genre_id) {
        this.genre_id = genre_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

