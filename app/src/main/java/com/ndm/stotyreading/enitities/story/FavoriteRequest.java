package com.ndm.stotyreading.enitities.story;

public class FavoriteRequest {
    private String story_id;

    public FavoriteRequest( String story_id) {
        this.story_id = story_id;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }
}

