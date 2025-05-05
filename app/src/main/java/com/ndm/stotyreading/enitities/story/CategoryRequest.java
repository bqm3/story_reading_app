package com.ndm.stotyreading.enitities.story;

public class CategoryRequest {
    private String name;
    private String description;

    public CategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters (optional, tùy thuộc vào cách bạn sử dụng)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
