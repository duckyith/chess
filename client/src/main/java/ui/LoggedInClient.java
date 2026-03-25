package ui;

import exception.ResponseException;
import models.AuthData;
import models.UserData;

public class LoggedInClient {
    private final ServerFacade server;
    public String username;
    public String authToken;
    public boolean forward = false;
    public boolean back = false;

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
}