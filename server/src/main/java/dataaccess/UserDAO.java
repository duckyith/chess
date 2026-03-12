package dataaccess;

import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO {
    void register(UserData userData) throws DataAccessException, SQLException;
    UserData getUser(String username) throws DataAccessException, SQLException;
    void addToken(AuthData authData) throws SQLException, DataAccessException;
    void removeToken(String token) throws DataAccessException, SQLException;
    String getToken(String token) throws DataAccessException, SQLException;
    void create(GameData game);
    ArrayList<GameData> list();
    GameData getGame(String gameID);
    void updateGame(GameData modifiedGame);
    void clear() throws DataAccessException, SQLException;
}
