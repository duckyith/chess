package ui;

import exception.ResponseException;
import models.AuthData;
import models.UserData;

public class LoggedOutClient {
    private final ServerFacade server;
    public String username;
    public String authToken;
    public boolean forward = false;

    public LoggedOutClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
            if (authData != null && authData.authToken() != null) {
                username = authData.username();
                authToken = authData.authToken();
                forward = true;
                return String.format("User registered. You signed in as %s.", params[0]);
            }
        }
        return "missing a field";
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            AuthData authData = server.login(new UserData(params[0], params[1], null));
            if (authData != null && authData.authToken() != null) {
                username = authData.username();
                authToken = authData.authToken();
                forward = true;
                return String.format("You signed in as %s.", params[0]);
            }
        }
        return "missing a field";
    }
}