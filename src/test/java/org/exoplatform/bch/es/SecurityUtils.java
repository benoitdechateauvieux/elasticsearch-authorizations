package org.exoplatform.bch.es;

import org.exoplatform.bch.es.security.ConversationState;
import org.exoplatform.bch.es.security.Identity;
import org.exoplatform.bch.es.security.MembershipEntry;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by bdechateauvieux on 6/8/15.
 */
public class SecurityUtils {

    public static void setCurrentUser(String userId, String... memberships) {
        Set<MembershipEntry> membershipEntrySet = new HashSet<>();
        if (memberships!=null) {
            for (String membership : memberships) {
                String[] membershipSplit = membership.split(":");
                membershipEntrySet.add(new MembershipEntry(membershipSplit[1], membershipSplit[0]));
            }
        }
        ConversationState.setCurrent(new ConversationState(new Identity(userId, membershipEntrySet)));
    }
}
