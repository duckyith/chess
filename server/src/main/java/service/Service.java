package service;

import dataaccess.UserDAO;
import models.AuthData;
import models.User;

public class Service {
    private final UserDAO userDAO;

    public Service(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register (User user) {
        return null;
    }
}
