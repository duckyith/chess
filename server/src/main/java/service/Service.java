package service;

import chess.ChessGame;
import dataaccess.*;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.util.UUID;

public class Service {
    private final UserDAO userDAO;

    public Service(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register (UserData userData) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (userDAO.getUser(userData.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.register(userData);
        AuthData authData = new AuthData(generateToken(), userData.username());
        userDAO.addToken(authData);
        return authData;
    }

    public AuthData login (UserData userData) throws DataAccessException, BadRequestException, UnauthorizedException {
        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData targetUserData = userDAO.getUser(userData.username());
        if (targetUserData == null || userDAO.getToken(targetUserData.username()) != null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (!targetUserData.password().equals(userData.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authData = new AuthData(generateToken(), userData.username());
        userDAO.addToken(authData);
        return authData;
    }

    public void logout (String authToken) throws DataAccessException, UnauthorizedException {
        if (userDAO.getToken(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        System.out.println(authToken);
        userDAO.removeToken(authToken);
    }

    public GameData create(String authToken, GameData game) throws DataAccessException, UnauthorizedException {
        authenticate(authToken);
        int gameID = (int)(Math.random() * 9000) + 1000;
        GameData newGame = new GameData(gameID,null,null,game.gameName(),new ChessGame());
        userDAO.create(newGame);
        return new GameData(gameID,null,null,null,null);
    }

    public void clear () {
        userDAO.clear();
    }

    public void authenticate(String authToken) throws UnauthorizedException, DataAccessException{
        if (userDAO.getToken(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
