package org.exoplatform.bch.es.security;

/**
 * Created by bdechateauvieux on 6/8/15.
 */
public class Identity {
    private final String userId;

    public Identity(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
