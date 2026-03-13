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
import service.AlreadyTakenException;
import service.BadRequestException;
import service.Service;
import service.UnauthorizedException;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomDBTests {

    UserDAO userDAO = new MemoryUserDAO();
    Service service = new Service(userDAO);

    //WARNING: THESE TESTS CLEAR THE DATA BASE!!! D:
    @BeforeEach
    public void clearDB() throws SQLException, DataAccessException {
        userDAO.clear();
    }

    //Register
//    @Test
//    @DisplayName("Register S")
//    public void registerSuccess()
//            throws BadRequestException, DataAccessException, AlreadyTakenException, SQLException {
//        UserData userData = new UserData("username","password","username@gmail.com");
//        AuthData auth = new AuthData("doesntmatter","username");
//        AuthData result = service.register(userData);
//        Assertions.assertEquals(auth.username(),result.username());
//        Assertions.assertNotNull(result.authToken());
//    }
}
