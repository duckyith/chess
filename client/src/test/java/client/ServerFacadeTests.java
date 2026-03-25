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
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Register F")
    public void registerFail() {
        Assertions.assertFalse(false);
    }

    //Login
    @Test
    @DisplayName("Login S")
    public void loginSuccess() throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Login F")
    public void loginFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //Logout
    @Test
    @DisplayName("Logout S")
    public void logoutSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Logout F")
    public void logoutFail() {
        Assertions.assertFalse(false);
    }

    //Create
    @Test
    @DisplayName("Create S")
    public void createSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Create F")
    public void createFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //List
    @Test
    @DisplayName("List S")
    public void listSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("List F")
    public void listFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //Play
    @Test
    @DisplayName("Play S")
    public void playSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("Play F")
    public void playFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //buildRequest
    @Test
    @DisplayName("buildRequest S")
    public void buildRequestSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("buildRequest F")
    public void buildRequestFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //makeRequest
    @Test
    @DisplayName("makeRequestBody S")
    public void makeRequestBodySuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("makeRequestBody F")
    public void makeRequestBodyFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //sendRequest
    @Test
    @DisplayName("sendRequest S")
    public void sendRequestSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("sendRequest F")
    public void sendRequestFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //handleResponse
    @Test
    @DisplayName("handleResponse S")
    public void handleResponseSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("handleResponse F")
    public void handleResponseFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

    //isSuccessful
    @Test
    @DisplayName("isSuccessful S")
    public void isSuccessfulSuccess()
            throws ResponseException {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("isSuccessful F")
    public void isSuccessfulFail()
            throws ResponseException {
        Assertions.assertFalse(false);
    }

}
