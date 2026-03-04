package dataaccess;

import models.AuthData;
import models.GameData;
import models.UserData;

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
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void addToken(AuthData authData) {
        tokens.put(authData.authToken(), authData.username());
    }

    @Override
    public void removeToken(String token) throws DataAccessException {
        System.out.println("token to be removed");
        System.out.println(token);
        System.out.println("should be here");
        System.out.println(tokens.get(token));
        tokens.remove(token);
        System.out.println("should NOT be here");
        System.out.println(tokens.get(token));
    }

    @Override
    public String getToken(String token) throws DataAccessException {
        return tokens.get(token);
    }

    @Override
    public void create(GameData game) throws DataAccessException {
        games.put(Integer.toString(game.gameID()), game);
    }

    @Override
    public ArrayList<GameData> list() throws DataAccessException {
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
