package com.ndm.stotyreading.enitities.story;

import java.util.List;

public class StoryWithChaptersResponse {
    private boolean success;
    private Story story;
    private List<Chapter> chapters;

    public boolean isSuccess() {
        return success;
    }

    public Story getStory() {
        return story;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
