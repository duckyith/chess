package dataaccess;

import models.AuthData;
import models.GameData;
import models.UserData;

import java.sql.SQLException;
import java.util.*;

public class MemoryUserDAO implements UserDAO {

    Map<String, UserData> users = new HashMap<>();
    Map<String, String> tokens = new HashMap<>();
    Map<String, GameData> games = new HashMap<>();

    @Override
    public void register(UserData userData) {
        users.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addToken(AuthData authData) {
        tokens.put(authData.authToken(), authData.username());
    }

    @Override
    public void removeToken(String token) {
        tokens.remove(token);
    }

    @Override
    public String getToken(String token) {
        return tokens.get(token);
    }

    @Override
    public String getTokenByUser(String user) {
        return "";
    }

    @Override
    public void create(GameData game) {
        games.put(Integer.toString(game.gameID()), game);
    }

    @Override
    public ArrayList<GameData> list() {
        ArrayList<GameData> games = new ArrayList<>();
        for (GameData game : this.games.values()) {
            games.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return games;
    }

    @Override
    public GameData getGame(String gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData modifiedGame) {
        games.replace(Integer.toString(modifiedGame.gameID()), modifiedGame);
    }

    @Override
    public void clear() {
        tokens.clear();
        users.clear();
        games.clear();
    }

}
