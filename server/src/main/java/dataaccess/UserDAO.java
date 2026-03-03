package dataaccess;

import models.AuthData;
import models.User;

import java.util.Collection;

public interface UserDAO {
    void register(User user) throws DataAccessException;
    User getUser(String username) throws DataAccessException;
    void addToken(AuthData authData) throws DataAccessException;
    void removeToken(String token) throws DataAccessException;
    String getToken(String token) throws DataAccessException;
    void clear();
}
