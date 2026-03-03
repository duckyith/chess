package dataaccess;

import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.ArrayList;

public interface UserDAO {
    void register(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void addToken(AuthData authData) throws DataAccessException;
    void removeToken(String token) throws DataAccessException;
    String getToken(String token) throws DataAccessException;
    void create(GameData game) throws DataAccessException;
    ArrayList<GameData> list() throws DataAccessException;
    void clear();
}
