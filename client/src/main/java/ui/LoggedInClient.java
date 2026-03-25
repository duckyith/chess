package ui;

import chess.ChessGame;
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
        activeGames = new ArrayList<>();
        if (games.games().isEmpty()) {
            return "No Active Games";
        }
        for (int i = 0; i < games.games().size(); i++) {
            GameData game = games.games().get(i);
            gamesString.append(String.format("%d. %s White: %s Black: %s\n", i + 1, game.gameName(), game.whiteUsername(), game.blackUsername()));
            activeGames.add(game);
        }
        return gamesString.toString();
    }

    public String play(String gameNumber, String color, String authToken) throws ResponseException {
        if (!Objects.equals(color, "black") && !Objects.equals(color, "white")) {
            return "color invalid, options: black or white";
        }
        String capsColor;
        if (Objects.equals(color, "white")) {
            capsColor = "WHITE";
        } else {capsColor = "BLACK";}
        int gameID = activeGames.get(Integer.parseInt(gameNumber)-1).gameID();
        JoinData joinData = new JoinData(capsColor, Integer.toString(gameID));
        server.play(joinData, authToken);
        forward = true;
        return draw(gameNumber,capsColor);
    }

    public String draw(String gameNumber, String color) {
        ChessGame game = activeGames.get(Integer.parseInt(gameNumber)-1).game();
        if (Objects.equals(color, "WHITE")) {
            return "white side";
        }
        return "black side";
    }
}