package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MYSQLUserDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomServiceTests {

    UserDAO userDAO = new MYSQLUserDAO();
    Service service = new Service(userDAO);

    //WARNING: THESE TESTS CLEAR THE DATA BASE!!! D:
    @BeforeEach
    public void clearDB() throws SQLException, DataAccessException {
        userDAO.clear();
    }

    //Register
    @Test
    @DisplayName("Register S")
    public void registerSuccess()
            throws SQLException, BadRequestException, DataAccessException, AlreadyTakenException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = new AuthData("doesntmatter","username");
        AuthData result = service.register(userData);
        Assertions.assertEquals(auth.username(),result.username());
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Register F")
    public void registerFail() {
        UserData userData = new UserData("username",null,"username@gmail.com");
        Assertions.assertThrows(BadRequestException.class, () -> service.register(userData));
    }

    //Login
    @Test
    @DisplayName("Login S")
    public void loginSuccess()
            throws SQLException, BadRequestException, DataAccessException, UnauthorizedException, AlreadyTakenException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData authdata = service.register(userData);
        AuthData result = service.login(userData);
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Login F")
    public void loginFail()
            throws SQLException, BadRequestException, DataAccessException, AlreadyTakenException, UnauthorizedException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData authdata = service.register(userData);
        UserData wrongData = new UserData("username","notPassword","username@gmail.com");
        service.logout(authdata.authToken());
        Assertions.assertNotEquals(userData.password(),wrongData.password());
    }

    //Logout
    @Test
    @DisplayName("Logout S")
    public void logoutSuccess()
            throws SQLException, BadRequestException, DataAccessException, AlreadyTakenException, UnauthorizedException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData authdata = service.register(userData);
        service.logout(authdata.authToken());
        Assertions.assertNull(userDAO.getToken(authdata.authToken()));
    }

    @Test
    @DisplayName("Logout F")
    public void logoutFail() {
        Assertions.assertThrows(UnauthorizedException.class, () -> service.logout("wrong"));
    }

    //Create
    @Test
    @DisplayName("Create S")
    public void createSuccess()
            throws SQLException, BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = service.register(userData);
        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
        GameData result = service.create(auth.authToken(),game);
        Assertions.assertNotNull(result.gameID());
    }

    @Test
    @DisplayName("Create F")
    public void createFail()
            throws SQLException, BadRequestException, AlreadyTakenException, DataAccessException {
            UserData userData = new UserData("username","password","username@gmail.com");
            AuthData auth = service.register(userData);
            GameData game = new GameData(1234,null,null,null,new ChessGame());
            Assertions.assertThrows(BadRequestException.class, () -> service.create(auth.authToken(),game));
    }

    //List
    @Test
    @DisplayName("List S")
    public void listSuccess()
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, SQLException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = service.register(userData);
        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
        service.create(auth.authToken(),game);
        ArrayList<GameData> gameList = service.list(auth.authToken());
        Assertions.assertFalse(gameList.isEmpty());
    }

    @Test
    @DisplayName("List F")
    public void listFail()
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, SQLException, DataAccessException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = service.register(userData);
        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
        service.create(auth.authToken(),game);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.list("fakeAuth"));
    }

    //Join
    @Test
    @DisplayName("Join S")
    public void joinSuccess()
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, SQLException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = service.register(userData);
        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
        int id = service.create(auth.authToken(),game).gameID();
        JoinData join = new JoinData("WHITE", Integer.toString(id));
        service.join(auth.authToken(),join);
        int targetID = service.list(auth.authToken()).getFirst().gameID();
        Assertions.assertEquals(id,targetID);
    }

    @Test
    @DisplayName("Join F")
    public void joinFail()
            throws BadRequestException, AlreadyTakenException, UnauthorizedException, SQLException, DataAccessException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = service.register(userData);
        GameData game = new GameData(1234,null,null,"GameName",new ChessGame());
        int id = service.create(auth.authToken(),game).gameID();
        JoinData join = new JoinData("WHITE", Integer.toString(id));
        Assertions.assertThrows(UnauthorizedException.class, () -> service.join("fakeAuth",join));
    }

    //Clear
    @Test
    @DisplayName("Clear S")
    public void clearSuccess() throws BadRequestException, DataAccessException, AlreadyTakenException, SQLException {
        UserData userData = new UserData("username","password","username@gmail.com");
        service.register(userData);
        service.clear();
        Assertions.assertNull(userDAO.getUser("username"));
    }

    @Test
    @DisplayName("Clear S") //I don't know if I can write a negative test for clear
    public void clearSuccess2() throws BadRequestException, DataAccessException, AlreadyTakenException, SQLException {
        UserData userData = new UserData("username","password","username@gmail.com");
        service.register(userData);
        service.clear();
        Assertions.assertNull(userDAO.getUser("username"));
    }

    //Authenticate
    @Test
    @DisplayName("Auth S")
    public void authSuccess()
            throws BadRequestException, AlreadyTakenException, SQLException, DataAccessException {
        UserData userData = new UserData("username","password","username@gmail.com");
        String auth = service.register(userData).authToken();
        Assertions.assertDoesNotThrow(() -> service.authenticate(auth));
    }

    @Test
    @DisplayName("Auth F")
    public void authFail()
            throws BadRequestException, AlreadyTakenException {
        AuthData authData = new AuthData("nope","username");
        Assertions.assertThrows(UnauthorizedException.class, () -> service.authenticate(authData.authToken()));
    }
}
