package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class ChapterDetailResponse {
    private Chapter chapter;
    private List<ChapterImage> images;
    private List<Comment> comments;

    public Chapter getChapter() {
        return chapter;
    }

    public List<ChapterImage> getImages() {
        return images;
    }

    public List<Comment> getComments() {
        return comments;
    }
}

