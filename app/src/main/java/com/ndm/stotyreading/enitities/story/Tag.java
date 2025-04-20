package com.ndm.stotyreading.enitities.story;

import java.io.Serializable;

public class Tag  implements Serializable {
    private int id;
    private String name;
    private StoryTag StoryTag;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public StoryTag getStoryTag() {
        return StoryTag;
    }
}
