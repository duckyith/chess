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

    public String play(String authToken, String... params) throws ResponseException {
        int index;
        if (params.length != 2){
            return "missing or too many fields. play # color";
        }
        String gameNumber = params[0];
        String color = params[1];
        try {
            index = Integer.parseInt(gameNumber);
        } catch (NumberFormatException ex){
            return "please enter a game number from the list";
        }
        if (!(index > 0 && index < activeGames.size()+1)) {
            return "out of range, use command list to see options";
        }
        if (!Objects.equals(color, "black") && !Objects.equals(color, "white")) {
            return "color invalid, options: black or white";
        }
        String capsColor;
        if (Objects.equals(color, "white")) {
            capsColor = "WHITE";
        } else {capsColor = "BLACK";}
        GameData game = activeGames.get(Integer.parseInt(gameNumber)-1);
        int gameID = game.gameID();
        JoinData joinData = new JoinData(capsColor, Integer.toString(gameID));
        try {
            server.play(joinData, authToken);
        } catch (ResponseException ex) {
            return "already taken";
        }
        forward = true;
        if (Objects.equals(color, "white")) {
            return new DrawBoard(game.game()).drawWhite();
        } else {return new DrawBoard(game.game()).drawBlack();}
    }

    public String observe(String gameNumber) {
        int index;
        try {
            index = Integer.parseInt(gameNumber);
        } catch (NumberFormatException ex){
            return "please enter a game number from the list";
        }
        if (!(index > 0 && index < activeGames.size()+1)) {
            return "out of range, use command list to see options";
        }
        GameData game = activeGames.get(Integer.parseInt(gameNumber)-1);
        forward = true;
        return new DrawBoard(game.game()).drawWhite();
    }
}