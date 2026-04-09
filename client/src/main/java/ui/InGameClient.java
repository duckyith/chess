package ui;

import client.websocket.WebSocketFacade;
import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.UserData;

public class InGameClient {
    private final ServerFacade server;
    public final WebSocketFacade webSocketFacade;
    public int gameID;
    public String username;
    public String authToken;
    public GameData gameData;
    public boolean back = false;

    public InGameClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        try {
            this.webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int gameID, String username, String authToken, GameData gameData) {
        this.gameID = gameID;
        this.username = username;
        this.authToken = authToken;
        this.gameData = gameData;
        try {
            webSocketFacade.connectToGame(username,gameID);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public String leave() throws ResponseException {
        webSocketFacade.leaveGame(authToken,gameID);
        back = true;
        return "left game";
    }

    public String redraw() {
        return new DrawBoard(gameData.game()).drawWhite();
    }
}