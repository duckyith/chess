package service;

import chess.ChessGame;
import dataaccess.*;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class Service {
    private final UserDAO userDAO;

    public Service(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthData register (UserData userData)
            throws BadRequestException, AlreadyTakenException {
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (userDAO.getUser(userData.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.register(userData);
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        userDAO.addToken(authData);
        return authData;
    }

    public AuthData login (UserData userData)
            throws BadRequestException, UnauthorizedException {
        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Error: bad request");
        }
        UserData targetUserData = userDAO.getUser(userData.username());
        if (targetUserData == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (!BCrypt.checkpw(userData.password(), targetUserData.password())){
            throw new UnauthorizedException("Error: unauthorized");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.username());
        userDAO.addToken(authData);
        return authData;
    }

    public void logout (String authToken)
            throws UnauthorizedException {
        if (userDAO.getToken(authToken) == null){
            throw new UnauthorizedException("Error: unauthorized");
        }
        System.out.println(authToken);
        userDAO.removeToken(authToken);
    }

    public GameData create(String authToken, GameData game)
            throws UnauthorizedException, BadRequestException {
        authenticate(authToken);
        if (game.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        int gameID = (int)(Math.random() * 9000) + 1000;
        GameData newGame = new GameData(gameID,null,null,game.gameName(),new ChessGame());
        userDAO.create(newGame);
        return new GameData(gameID,null,null,null,null);
    }

    public ArrayList<GameData> list(String authToken)
            throws UnauthorizedException {
        authenticate(authToken);
        return userDAO.list();
    }

    public void join (String authToken, JoinData request)
            throws BadRequestException, AlreadyTakenException, UnauthorizedException {
        authenticate(authToken);
        String player = userDAO.getUserByToken(authToken);
        GameData game = userDAO.getGame(request.gameID());
        String color = request.playerColor();

        if (color == null
                || request.gameID() == null
                || (!color.equals("WHITE") && !color.equals("BLACK"))
                || game == null) {
            throw new BadRequestException("Error: bad request");
        }
        if ((color.equals("WHITE") && game.whiteUsername() != null)
                || (color.equals("BLACK") && game.blackUsername() != null)) {
            throw new AlreadyTakenException("Error: already taken");
        }
        if (color.equals("WHITE")) {
            GameData modifiedGame = new GameData(game.gameID(), player, game.blackUsername(), game.gameName(), game.game());
            userDAO.updateGame(modifiedGame);
        } else {
            GameData modifiedGame = new GameData(game.gameID(), game.whiteUsername(), player, game.gameName(), game.game());
            userDAO.updateGame(modifiedGame);
        }

    }

    public void clear () throws SQLException, DataAccessException {
        userDAO.clear();
    }

    public void authenticate(String authToken)
            throws UnauthorizedException {
        if (userDAO.getToken(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }
}
