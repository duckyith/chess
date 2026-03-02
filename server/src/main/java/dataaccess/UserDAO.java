package dataaccess;

import models.User;

public interface UserDAO {
    void create(User user) throws DataAccessException;
}
