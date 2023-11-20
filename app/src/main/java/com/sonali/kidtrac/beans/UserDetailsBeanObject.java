package com.sonali.kidtrac.beans;

import java.util.Random;

public class UserDetailsBeanObject {
    private String userId, userName, userEmail;

    public UserDetailsBeanObject(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = generateUID();
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    private String generateUID() {
        StringBuffer currentTime = new StringBuffer(Long.toHexString(System.currentTimeMillis()));
        return currentTime.reverse().substring(0, currentTime.length() - 3).toString().toUpperCase()
                + (new Random().nextInt(8888) + 1111);
    }
}
