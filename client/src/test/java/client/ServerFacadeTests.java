package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MYSQLUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.sql.SQLException;
import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    public ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
    static UserDAO userDAO = new MYSQLUserDAO();

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        userDAO.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Register Success")
    public void registerSuccess() throws ResponseException {
        UserData userData = new UserData("user1", "password", "user1@gmail.com");
        AuthData auth = serverFacade.register(userData);
        Assertions.assertEquals("user1", auth.username());
    }

    @Test
    @DisplayName("Register F")
    public void registerFail() {
        UserData userData = new UserData("username",null,"username@gmail.com");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(userData));
    }
//
//    //Login
//    @Test
//    @DisplayName("Login S")
//    public void loginSuccess() throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData authdata = serverFacade.register(userData);
//        AuthData result = serverFacade.login(userData);
//        Assertions.assertNotNull(result.authToken());
//    }
//
//    @Test
//    @DisplayName("Login F")
//    public void loginFail()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData authdata = serverFacade.register(userData);
//        UserData wrongData = new UserData("username","notPassword","username@gmail.com");
//        serverFacade.logout(authdata.authToken());
//        Assertions.assertNotEquals(userData.password(),wrongData.password());
//    }
//
//    //Logout
//    @Test
//    @DisplayName("Logout S")
//    public void logoutSuccess()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData authdata = serverFacade.register(userData);
//        serverFacade.logout(authdata.authToken());
//        Assertions.assertNull(userDAO.getToken(authdata.authToken()));
//    }
//
//    @Test
//    @DisplayName("Logout F")
//    public void logoutFail() {
//        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("wrong"));
//    }
//
//    //Create
//    @Test
//    @DisplayName("Create S")
//    public void createSuccess()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
//        GameData result = serverFacade.create(auth.authToken(),game);
//        Assertions.assertNotNull(result.gameID());
//    }
//
//    @Test
//    @DisplayName("Create F")
//    public void createFail()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,null,new ChessGame());
//        Assertions.assertThrows(ResponseException.class, () -> serverFacade.create(auth.authToken(),game));
//    }
//
//    //List
//    @Test
//    @DisplayName("List S")
//    public void listSuccess()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
//        serverFacade.create(auth.authToken(),game);
//        ArrayList<GameData> gameList = serverFacade.list(auth.authToken());
//        Assertions.assertFalse(gameList.isEmpty());
//    }
//
//    @Test
//    @DisplayName("List F")
//    public void listFail()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
//        serverFacade.create(auth.authToken(),game);
//        Assertions.assertThrows(ResponseException.class, () -> serverFacade.list("fakeAuth"));
//    }
//
//    //Join
//    @Test
//    @DisplayName("Join S")
//    public void joinSuccess()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
//        int id = serverFacade.create(auth.authToken(),game).gameID();
//        JoinData join = new JoinData("WHITE", Integer.toString(id));
//        serverFacade.play(auth.authToken(),join);
//        int targetID = serverFacade.list(auth.authToken()).getFirst().gameID();
//        Assertions.assertEquals(id,targetID);
//    }
//
//    @Test
//    @DisplayName("Join F")
//    public void joinFail()
//            throws ResponseException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = serverFacade.register(userData);
//        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
//        int id = serverFacade.create(auth.authToken(),game).gameID();
//        JoinData join = new JoinData("WHITE", Integer.toString(id));
//        Assertions.assertThrows(ResponseException.class, () -> serverFacade.play("fakeAuth",join));
//    }

}
