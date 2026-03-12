package dataaccess;

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
    public String getTokenByUser(String user) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT token FROM tokens WHERE user = '%s'"
                    ,user))) {
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
        String game = gson.toJson(gameData);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO games (gameID,gameData) VALUES (%d,'%s')"
                    ,gameID,game))) {
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
                    "SELECT gameData FROM games"))) {
                var rs = statement.executeQuery();
                GameData gameDisplay;
                GameData tempGame;
                while (rs.next()) {
                    tempGame = gson.fromJson(rs.getString(1), GameData.class);
                    gameDisplay = new GameData(tempGame.gameID(), tempGame.whiteUsername(), tempGame.blackUsername(), tempGame.gameName(), null);
                    games.add(gameDisplay);
                }
                if(!games.isEmpty()){return games;};
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Internal Server Error",e);
        }
        return null;
    }

    @Override
    public GameData getGame(String stringID) {
        int gameID = Integer.parseInt(stringID);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "SELECT gameID,gameData FROM games WHERE gameID = %d"
                    ,gameID))) {
                var rs = statement.executeQuery();
                if (rs.next()) {
                    return gson.fromJson(rs.getString(2), GameData.class);
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
        String game = gson.toJson(gameData);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(String.format(
                    "INSERT INTO games (gameID,gameData) VALUES (%d,'%s')"
                    ,gameID,game))) {
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
              `gameData` TEXT NOT NULL,
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
