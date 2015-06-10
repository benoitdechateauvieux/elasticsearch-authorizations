package org.exoplatform.bch.es;

import io.searchbox.annotations.JestId;

/**
 * Created by bdechateauvieux on 6/4/15.
 */
public class Page {
    @JestId
    private String id = Long.toString(System.currentTimeMillis()); // Default Id
    private String title;
    private String[] permissions;

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
