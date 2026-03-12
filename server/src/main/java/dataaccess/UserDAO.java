package dataaccess;

import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UserDAO {
    void register(UserData userData);
    UserData getUser(String username);
    void addToken(AuthData authData);
    void removeToken(String token);
    String getToken(String token);
    String getTokenByUser(String user);
    String getUserByToken (String token);
    void create(GameData game);
    ArrayList<GameData> list();
    GameData getGame(String gameID);
    void updateGame(GameData modifiedGame);
    void clear();
}
