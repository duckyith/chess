package dataaccess;

import chess.ChessGame;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.Service;
import service.UnauthorizedException;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomDBTests {

    UserDAO userDAO = new MYSQLUserDAO();

    //WARNING: THESE TESTS CLEAR THE DATA BASE!!! D:
    @BeforeEach
    public void clearDB() throws SQLException, DataAccessException {
        userDAO.clear();
        userDAO.register(new UserData("u","p","e"));
    }

    @Test
    @DisplayName("Register S")
    public void registerSuccess(){
        Assertions.assertTrue(BCrypt.checkpw("p",getter("password","users","username","u")));
    }

    @Test
    @DisplayName("Register F")
    public void registerFail(){
        Assertions.assertFalse(BCrypt.checkpw("l",getter("password","users","username","u")));
    }

    @Test
    @DisplayName("GetUser S")
    public void getUserSuccess(){
        Assertions.assertNotNull(userDAO.getUser("u"));
    }

    @Test
    @DisplayName("GetUser F")
    public void getUserFail(){
        Assertions.assertNull(userDAO.getUser("p"));
    }

    @Test
    @DisplayName("AddToken S")
    public void addTokenSuccess(){
        userDAO.addToken(new AuthData("a","u"));
        Assertions.assertEquals("a",getter("token","tokens","user","u"));
    }

    @Test
    @DisplayName("AddToken F")
    public void addTokenFail(){
        userDAO.addToken(new AuthData("a","p"));
        Assertions.assertNull(getter("token","tokens","user","u"));
    }

    @Test
    @DisplayName("RemoveToken S")
    public void removeTokenSuccess(){
        userDAO.addToken(new AuthData("a","u"));
        userDAO.removeToken("a");
        Assertions.assertNull(getter("token","tokens","token","a"));
    }

    @Test
    @DisplayName("RemoveToken F")
    public void removeTokenFail(){
        userDAO.addToken(new AuthData("a","u"));
        userDAO.removeToken("u");
        Assertions.assertNotNull(getter("token","tokens","user","u"));
    }

    @Test
    @DisplayName("GetToken S")
    public void getTokenSuccess(){
        userDAO.addToken(new AuthData("a","u"));
        Assertions.assertEquals("a",userDAO.getToken("a"));
    }

    @Test
    @DisplayName("GetToken F")
    public void getTokenFail(){
        userDAO.addToken(new AuthData("a","p"));
        Assertions.assertNotEquals("u",userDAO.getToken("a"));
    }

    @Test
    @DisplayName("GetUserByToken S")
    public void getUserByTokenSuccess(){
        userDAO.addToken(new AuthData("a","u"));
        Assertions.assertEquals("u",userDAO.getUserByToken("a"));
    }

    @Test
    @DisplayName("GetUserByToken F")
    public void getUserByTokenFail(){
        userDAO.addToken(new AuthData("a","p"));
        Assertions.assertNull(userDAO.getUserByToken("u"));
    }

    @Test
    @DisplayName("Clear S")
    public void clearSuccess(){
        userDAO.clear();
        Assertions.assertNull(userDAO.getUser("u"));
    }

    //Not sure how to write a fail for this one
    @Test
    @DisplayName("Clear F")
    public void clearFail(){
        userDAO.clear();
        Assertions.assertNull(userDAO.getUser("p"));
    }

    @Test
    @DisplayName("Create S")
    public void createSuccess(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        Assertions.assertNotNull(getter("gameID","games","gameID","1234"));
    }

    @Test
    @DisplayName("Create F")
    public void createFail(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        Assertions.assertNull(getter("gameID","games","game","2222"));
    }

    @Test
    @DisplayName("List S")
    public void listSuccess(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        ArrayList<GameData> games = userDAO.list();
        Assertions.assertEquals(1234,games.getFirst().gameID());
    }

    @Test
    @DisplayName("List F")
    public void listFail(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        userDAO.clear();
        ArrayList<GameData> games = userDAO.list();
        ArrayList<GameData> empty = new ArrayList<>();
        Assertions.assertEquals(empty,games);
    }

    @Test
    @DisplayName("GetGame S")
    public void getGameSuccess(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        Assertions.assertEquals(Integer.toString(userDAO.getGame("1234").gameID()),getter("gameID","games","gameID","1234"));
    }

    @Test
    @DisplayName("GetGame F")
    public void getGameFail(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        GameData gameNew = new GameData(4321,null,null,"game",new ChessGame());
        userDAO.create(newGame);
        userDAO.create(gameNew);
        Assertions.assertNotEquals(userDAO.getGame("4321").gameID(),getter("gameID","games","game","2222"));
    }

    @Test
    @DisplayName("UpdateGame S")
    public void updateSuccess(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        GameData gameNew = new GameData(1234,"u",null,"game",new ChessGame());
        userDAO.create(newGame);
        userDAO.updateGame(gameNew);
        Assertions.assertEquals("u",userDAO.getGame("1234").whiteUsername());
    }

    @Test
    @DisplayName("UpdateGame F")
    public void updateFail(){
        GameData newGame = new GameData(1234,null,null,"game",new ChessGame());
        GameData gameNew = new GameData(2345,"u",null,"game",new ChessGame());
        userDAO.create(newGame);
        userDAO.updateGame(gameNew);
        Assertions.assertNotEquals("u",userDAO.getGame("1234").whiteUsername());
    }

    public String getter(String select, String from, String get, String val) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format("SELECT %s FROM %s WHERE %s = '%s'",select,from,get,val))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error", e);
        }
        return null;
    }
}
