package mockdatabase;

import models.AuthData;
import java.util.ArrayList;
import java.util.Collection;

public class AuthTokens {

    private Collection<AuthData> tokens;

    public AuthTokens() {
        this.tokens = new ArrayList<>();
    }

    public Collection<AuthData> getTokens() {
        return tokens;
    }

    public void addToken(AuthData token) {
        tokens.add(token);
    }

    public void removeToken(AuthData token) {
        tokens.remove(token);
    }

    public void clearTokens() {
        tokens.clear();
    }
}
