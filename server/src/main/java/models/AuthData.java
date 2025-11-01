package models;

import com.google.gson.Gson;

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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
