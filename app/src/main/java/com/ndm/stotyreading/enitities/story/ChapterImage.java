package com.ndm.stotyreading.enitities.story;

import android.os.Parcel;
import android.os.Parcelable;

public class ChapterImage implements Parcelable {
    private long id;
    private String chapter_id;
    private String image_url;
    private int order;
    private String description;

    public long getId() {
        return id;
    }

    public String getChapterId() {
        return chapter_id;
    }

    public String getImageUrl() {
        return image_url;
    }

    public int getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    // ✅ Constructor dùng cho Parcelable
    protected ChapterImage(Parcel in) {
        id = in.readLong();
        chapter_id = in.readString();
        image_url = in.readString();
        order = in.readInt();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(chapter_id);
        dest.writeString(image_url);
        dest.writeInt(order);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ✅ CREATOR cho Parcelable
    public static final Creator<ChapterImage> CREATOR = new Creator<ChapterImage>() {
        @Override
        public ChapterImage createFromParcel(Parcel in) {
            return new ChapterImage(in);
        }

        @Override
        public ChapterImage[] newArray(int size) {
            return new ChapterImage[size];
        }
    };
}
