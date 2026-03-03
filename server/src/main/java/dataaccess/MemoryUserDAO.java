package dataaccess;

import models.AuthData;
import models.User;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    Map<String, User> Users = new HashMap<>();
    Map<String, String> Tokens = new HashMap<>();

    @Override
    public void register(User user) {
        String userToAdd = user.username();
        Users.put(userToAdd,user);
    }

    @Override
    public void addToken(AuthData authData) {
        Tokens.put(authData.username(), authData.authToken());
    }

    @Override
    public User get(String username) throws DataAccessException {
        if (Users.containsKey(username)) {
            return Users.get(username);
        }
        return null;
    }

}
