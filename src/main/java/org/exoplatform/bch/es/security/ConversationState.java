package org.exoplatform.bch.es.security;

/**
 * Created by bdechateauvieux on 6/8/15.
 */
public class ConversationState {
    /**
     * ThreadLocal keeper for ConversationState.
     */
    private static ThreadLocal<ConversationState> current = new ThreadLocal<ConversationState>();

    /**
     * See {@link Identity}.
     */
    private Identity identity;

    /**
     * @return current ConversationState or null if it was not preset
     */
    public static ConversationState getCurrent()
    {
        return current.get();
    }


    public ConversationState(Identity identity) {
        this.identity = identity;
    }
    /**
     * Preset current ConversationState.
     *
     * @param state ConversationState
     */
    public static void setCurrent(ConversationState state) {
        current.set(state);
    }

    /**
     * @return Identity  the user identity object
     */
    public Identity getIdentity()
    {
        return identity;
    }
}
