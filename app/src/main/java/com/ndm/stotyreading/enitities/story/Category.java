package com.ndm.stotyreading.enitities.story;

import java.io.Serializable;

public class Category  implements Serializable {
    private String id;
    private String name;
    private String description;
    private String icon_url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return icon_url;
    }
}
