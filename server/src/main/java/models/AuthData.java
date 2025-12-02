package models;

import com.google.gson.Gson;

import java.util.UUID;

public class AuthData {

    private String username;
    private String authToken;

    public AuthData(String username, String authToken) {
        if (authToken == null) {
            this.authToken = generateToken();
        } else {
            this.authToken = authToken;
        }
        this.username = username;
    }

    public void resetToken() {
        this.authToken = generateToken();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
