package service;

import dataaccess.*;
import models.AuthData;
import models.User;

import java.util.ArrayList;
import java.util.UUID;

public class Service {
    private final UserDAO userDAO;

    public Service(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register (User user) throws DataAccessException, AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.register(user);
        AuthData authData = new AuthData(generateToken(),user.username());
        userDAO.addToken(authData);
        return authData;
    }

    public AuthData login (User user) throws DataAccessException, BadRequestException, UnauthorizedException {
        User targetUser = userDAO.getUser(user.username());
        if (targetUser == null || userDAO.getToken(targetUser.username()) != null){
            throw new BadRequestException("Error: bad request");
        }
        if (!targetUser.password().equals(user.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authData = new AuthData(generateToken(),user.username());
        userDAO.addToken(authData);
        return authData;
    }

    public void logout (String authToken) throws DataAccessException, UnauthorizedException {
        if (authToken == null){
            throw new UnauthorizedException("not collecting the request properly");
        }
        if (userDAO.getToken(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        userDAO.removeToken(userDAO.getToken(authToken));
    }

    public void clear () {
        userDAO.clear();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
