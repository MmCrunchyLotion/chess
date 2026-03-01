package models;

import com.google.gson.Gson;
import java.util.UUID;

public class AuthData {

    private final String username;
    private final String authToken;

    public AuthData(String username, String authToken) {
        if (authToken == null) {
            this.authToken = generateToken();
        } else {
            this.authToken = authToken;
        }
        this.username = username;
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
