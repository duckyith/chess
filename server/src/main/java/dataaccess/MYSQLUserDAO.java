package dataaccess;

import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MYSQLUserDAO implements UserDAO {

    private final Gson gson = new Gson();

    public MYSQLUserDAO () throws DataAccessException, SQLException {
        configureDatabase();
    }

    @Override
    public void register(UserData userData) throws DataAccessException, SQLException {
        String username = userData.username();
        String password = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        String email = userData.email();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO users (username,password,email) VALUES ('%s','%s','%s')"
                    ,username,password,email))) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT username, password FROM users WHERE username = '%s'"
                    ,username))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    UserData userData = new UserData(rs.getString(1), rs.getString(2),null);
                    return userData;
                }
            }
        }
        return null;
    }

    @Override
    public void addToken(AuthData authData) throws SQLException, DataAccessException {
        String token = authData.authToken();
        String user = authData.username();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO tokens (token,user) VALUES ('%s','%s')"
                    ,token,user))) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void removeToken(String token) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "DELETE FROM tokens WHERE token = '%s'"
                    ,token))) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public String getToken(String token) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT token FROM tokens WHERE token = '%s'"
                    ,token))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    String test = rs.getString(1);
                    return test;
                }
            }
        }
        return null;
    }

    @Override
    public String getTokenByUser(String user) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT token FROM tokens WHERE user = '%s'"
                    ,user))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    String test = rs.getString(1);
                    return test;
                }
            }
        }
        return null;
    }

    @Override
    public void create(GameData gameData) throws DataAccessException, SQLException {
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        String game = gson.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO games (gameID,whiteUsername,blackUsername,gameName,game) VALUES ('%s','%s','%s','%s','%s)"
                    ,gameID,whiteUsername,blackUsername,gameName,game))) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public ArrayList<GameData> list() throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "DELETE FROM games WHERE gameID = '%s'"
                    ,gameData.gameID()))) {
                statement.executeUpdate();
            }
        }
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        String game = gson.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO games (gameID,whiteUsername,blackUsername,gameName,game) VALUES ('%s','%s','%s','%s','%s)"
                    ,gameID,whiteUsername,blackUsername,gameName,game))) {
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void clear() throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(new String(
                    "TRUNCATE TABLE users"))) {
                statement.executeUpdate();
            }
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(new String(
                    "TRUNCATE TABLE tokens"))) {
                statement.executeUpdate();
            }
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  tokens (
              `token` varchar(256) NOT NULL,
              `user` varchar(256) NOT NULL,
              PRIMARY KEY (`token`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` INT NOT NULL,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

}
