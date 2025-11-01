package models;

import java.util.UUID;

public class AuthData {

    String auth;
    String username;

    public AuthData(String username) {
        this.auth = generateToken();
        this.username = username;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
