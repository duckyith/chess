package dataaccess;

import models.AuthData;
import models.User;

import java.util.*;

public class MemoryUserDAO implements UserDAO {

    Map<String, User> Users = new HashMap<>();
    Map<String, String> Tokens = new HashMap<>();
    Map<String, String> Games = new HashMap<>();

    @Override
    public void register(User user) {
        Users.put(user.username(),user);
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        return Users.get(username);
    }

    @Override
    public void addToken(AuthData authData) {
        Tokens.put(authData.authToken(), authData.username());
    }

    @Override
    public void removeToken(String token) throws DataAccessException {
        System.out.println("token to be removed");
        System.out.println(token);
        System.out.println("should be here");
        System.out.println(Tokens.get(token));
        Tokens.remove(token);
        System.out.println("should NOT be here");
        System.out.println(Tokens.get(token));
    }

    @Override
    public String getToken(String token) throws DataAccessException {
        return Tokens.get(token);
    }

    @Override
    public void clear() {
        Tokens.clear();
        Users.clear();
        Games.clear();
    }

}
