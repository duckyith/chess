package ui;

import exception.ResponseException;
import models.AuthData;
import models.UserData;

public class InGameClient {
    private final ServerFacade server;
    public String username;
    public String authToken;
    public boolean back = false;

    public InGameClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String leave(){
        back = true;
        return "left game";
    }


}