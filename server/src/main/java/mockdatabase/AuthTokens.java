package mockdatabase;

import java.util.ArrayList;
import java.util.Collection;
import models.AuthData;

public class AuthTokens {

    private final Collection<AuthData> tokens;

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
