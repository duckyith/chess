package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import models.AuthData;
import models.User;
import java.util.UUID;

public class Service {
    private final UserDAO userDAO;

    public Service(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register (User user) throws DataAccessException, AlreadyTakenException {
        if (userDAO.get(user.username()) != null){
            throw new AlreadyTakenException("Error: username already taken");
        }
        userDAO.register(user);
        return login(user);
    }

    public AuthData login (User user) throws DataAccessException{
        AuthData authData = new AuthData(user.username(),generateToken());
        userDAO.addToken(authData);
        return authData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
