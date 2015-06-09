package org.exoplatform.bch.es.security;

import java.util.Set;

/**
 * Created by bdechateauvieux on 6/8/15.
 */
public class Identity {
    private final String userId;

    /**
     * Memberships.
     */
    private final Set<MembershipEntry> memberships;

    public Set<MembershipEntry> getMemberships() {
        return memberships;
    }

    public Identity(String userId, Set<MembershipEntry> memberships) {
        this.userId = userId;
        this.memberships = memberships;
    }

    public String getUserId() {
        return userId;
    }
}
