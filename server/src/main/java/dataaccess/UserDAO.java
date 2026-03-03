package dataaccess;

import models.AuthData;
import models.User;

public interface UserDAO {
    void register(User user) throws DataAccessException;
    void addToken(AuthData authData) throws DataAccessException;
    User get(String username) throws DataAccessException;
}
