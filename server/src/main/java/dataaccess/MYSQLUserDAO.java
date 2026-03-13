package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import models.AuthData;
import models.GameData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class MYSQLUserDAO implements UserDAO {

    private final Gson gson = new Gson();

    public MYSQLUserDAO () {
        configureDatabase();
    }

    @Override
    public void register(UserData userData)  {
        String username = userData.username();
        String password = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        String email = userData.email();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO users (username,password,email) VALUES ('%s','%s','%s')"
                    ,username,password,email))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT username, password FROM users WHERE username = '%s'"
                    ,username))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString(1), rs.getString(2),null);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        return null;
    }

    @Override
    public void addToken(AuthData authData) {
        String token = authData.authToken();
        String user = authData.username();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO tokens (token,user) VALUES ('%s','%s')"
                    ,token,user))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public void removeToken(String token) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "DELETE FROM tokens WHERE token = '%s'"
                    ,token))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public String getToken(String token) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT token FROM tokens WHERE token = '%s'"
                    ,token))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        return null;
    }

    @Override
    public String getUserByToken(String token) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT user FROM tokens WHERE token = '%s'"
                    ,token))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        return null;
    }

    @Override
    public void create(GameData gameData) {
        int gameID = gameData.gameID();
        String gameName = gameData.gameName();
        String game = gson.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO games (gameID,gameName,game) VALUES (?,?,?)")) {
                statement.setInt(1, gameID);
                statement.setString(2, gameName);
                statement.setString(3, game);
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public ArrayList<GameData> list() {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT gameID,whiteUsername,blackUsername,gameName FROM games"))) {
                var rs = statement.executeQuery();
                GameData gameDisplay;
                while (rs.next()) {
                    gameDisplay = new GameData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), null);
                    games.add(gameDisplay);
                }
                return games;
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public GameData getGame(String stringID) {
        int gameID;
        if (stringID != null) {
            gameID = Integer.parseInt(stringID);
        } else {return null;}
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT gameID,whiteUsername,blackUsername,gameName,game FROM games WHERE gameID = %d"
                    ,gameID))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    ChessGame game = gson.fromJson(rs.getString(5), ChessGame.class);
                    return new GameData(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), game);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "DELETE FROM games WHERE gameID = %d"
                    ,gameData.gameID()))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        int gameID = gameData.gameID();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String gameName = gameData.gameName();
        String game = gson.toJson(gameData.game());
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(
                    "INSERT INTO games (gameID,whiteUserName,blackUsername,gameName,game) VALUES (?,?,?,?,?)")) {
                statement.setInt(1, gameID);
                statement.setString(2, whiteUsername);
                statement.setString(3, blackUsername);
                statement.setString(4, gameName);
                statement.setString(5, game);
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(new String(
                    "TRUNCATE TABLE users"))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(new String(
                    "TRUNCATE TABLE tokens"))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(new String(
                    "TRUNCATE TABLE games"))) {
                statement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
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
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
    }

}
