package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import models.AuthData;
import models.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterTest {

    UserDAO userDAO = new MemoryUserDAO();
    Service service = new Service(userDAO);

    @Test
    @DisplayName("Successful Register")
    public void registerSuccess() throws BadRequestException, DataAccessException, AlreadyTakenException {
        UserData userData = new UserData("username","password","username@gmail.com");
        AuthData auth = new AuthData("doesntmatter","username");
        AuthData result = service.register(userData);
        Assertions.assertEquals(auth.username(),result.username());
        Assertions.assertNotNull(result.authToken());
    }
}
