package mockdatabase;

import models.UserData;
import java.util.ArrayList;
import java.util.Collection;

public class Users {

    private Collection<UserData> users;

    public Users() {
        this.users = new ArrayList<>();
    }

    public Collection<UserData> getUsers() {
        return users;
    }

    public void addUser(UserData user) {
        users.add(user);
    }

    public void clearUsers() {
        users.clear();
    }
}
