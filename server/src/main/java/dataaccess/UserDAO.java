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
    String getTokenByUser(String user) throws DataAccessException, SQLException;
    void create(GameData game) throws DataAccessException, SQLException;
    ArrayList<GameData> list() throws DataAccessException, SQLException;
    GameData getGame(String gameID) throws DataAccessException, SQLException;
    void updateGame(GameData modifiedGame) throws DataAccessException, SQLException;
    void clear() throws DataAccessException, SQLException;
}
