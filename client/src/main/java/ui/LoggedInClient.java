package ui;

import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class LoggedInClient {
    private final ServerFacade server;
    public String username;
    public String authToken;
    public boolean forward = false;
    public boolean back = false;
    public ArrayList<GameData> activeGames = new ArrayList<>();

    public LoggedInClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String logout(String authToken, String username) throws ResponseException {
            server.logout(authToken);
            this.username = null;
            this.authToken = null;
            back = true;
            return String.format("You signed out as %s.", username);
    }

    public String create(String gameName, String authToken) throws ResponseException {
        server.create(gameName, authToken);
        return String.format("%s created", gameName);
    }

    public String list(String authToken) throws ResponseException {
        Games games = server.list(authToken);
        StringBuilder gamesString = new StringBuilder();
        if (games.games().isEmpty()) {
            activeGames = null;
            return "No Active Games";
        }
        for (int i = 0; i < games.games().size(); i++) {
            GameData game = games.games().get(i);
            gamesString.append(String.format("%d. %s White: %s Black: %s\n", i + 1, game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return gamesString.toString();
    }

    public String play(String gameNumber, String color, String authToken) throws ResponseException {
        if (!Objects.equals(color, "BLACK") && !Objects.equals(color, "WHITE")) {
            return "color invalid, options: BLACK or WHITE";
        }
        int gameID = activeGames.get(Integer.parseInt(gameNumber)).gameID();
        JoinData joinData = new JoinData(color, Integer.toString(gameID));
        server.play(joinData, authToken);
        return draw(gameNumber,color);
    }
}