package com.cx.asset.service;

public final class SessionContext {

    private static final ThreadLocal<String> SESSION_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    private SessionContext() {}

    public static void set(String sessionId, String userId) {
        SESSION_ID.set(sessionId);
        USER_ID.set(userId);
    }

    public static String getSessionId() {
        return SESSION_ID.get();
    }

    public static String getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        SESSION_ID.remove();
        USER_ID.remove();
    }
}
