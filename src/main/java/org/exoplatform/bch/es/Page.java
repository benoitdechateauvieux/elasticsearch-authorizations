package org.exoplatform.bch.es;

import io.searchbox.annotations.JestId;

/**
 * Created by bdechateauvieux on 6/4/15.
 */
public class Page {
    @JestId
    private String id = Long.toString(System.currentTimeMillis()); // Default Id
    private String title;
    private String[] allowedUsers;
    private String[] allowedRoles;
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String[] getAllowedUsers() {
        return allowedUsers;
    }

    public void setAllowedUsers(String[] allowedUsers) {
        this.allowedUsers = allowedUsers;
    }

    public String[] getAllowedRoles() {
        return allowedRoles;
    }

    public void setAllowedRoles(String[] allowedRoles) {
        this.allowedRoles = allowedRoles;
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
