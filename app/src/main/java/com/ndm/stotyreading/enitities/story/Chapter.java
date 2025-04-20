package com.ndm.stotyreading.enitities.story;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Chapter implements Parcelable {
    private String id;
    private String story_id;
    private int chapter_number;
    private String title;
    private String release_date;
    private int views;
    private String created_at;
    private boolean is_viewed; // 🔥 Thêm biến kiểm tra đã đọc chưa
    private List<ChapterImage> chapterImages;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChapterImages(List<ChapterImage> chapterImages) {
        this.chapterImages = chapterImages;
    }

    public void setChapter_number(int chapter_number) {
        this.chapter_number = chapter_number;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setIs_viewed(boolean is_viewed) {
        this.is_viewed = is_viewed;
    }

    // ✅ Getters
    public String getId() {
        return id;
    }

    public String getStoryId() {
        return story_id;
    }

    public int getChapterNumber() {
        return chapter_number;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public int getViews() {
        return views;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public boolean isViewed() {
        return is_viewed;
    }

    public List<ChapterImage> getChapterImages() {
        return chapterImages;
    }

    // ✅ Constructor cho Parcelable
    protected Chapter(Parcel in) {
        id = in.readString();
        story_id = in.readString();
        chapter_number = in.readInt();
        title = in.readString();
        release_date = in.readString();
        views = in.readInt();
        created_at = in.readString();
        is_viewed = in.readByte() != 0;
        chapterImages = in.createTypedArrayList(ChapterImage.CREATOR); // 🔥 thêm dòng này
    }


    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(story_id);
        dest.writeInt(chapter_number);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeInt(views);
        dest.writeString(created_at);
        dest.writeByte((byte) (is_viewed ? 1 : 0));
        dest.writeTypedList(chapterImages); // 🔥 thêm dòng này
    }


    @Override
    public int describeContents() {
        return 0;
    }

    // ✅ Parcelable CREATOR
    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}
