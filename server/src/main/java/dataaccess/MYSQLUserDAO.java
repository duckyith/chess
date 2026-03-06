package dataaccess;

import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.ArrayList;

public class MYSQLUserDAO implements UserDAO{
    @Override
    public void register(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addToken(AuthData authData) {

    }

    @Override
    public void removeToken(String token) {

    }

    @Override
    public String getToken(String token) {
        return "";
    }

    @Override
    public void create(GameData game) {

    }

    @Override
    public ArrayList<GameData> list() {
        return null;
    }

    @Override
    public GameData getGame(String gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData modifiedGame) {

    }

    @Override
    public void clear() {

    }
}
