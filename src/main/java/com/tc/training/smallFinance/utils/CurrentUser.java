package com.tc.training.smallFinance.utils;

import com.tc.training.smallFinance.model.User;

public class CurrentUser {
    private static final ThreadLocal<User> threadUser = new ThreadLocal<>();

    public static void set(User user) {
        threadUser.set(user);
    }

    public static User get() {
        if (threadUser.get() == null) {
//            throw new ElementNotFound("user does not exist currently");
        } return threadUser.get();
    }

}

