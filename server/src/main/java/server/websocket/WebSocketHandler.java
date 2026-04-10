package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.UserDAO;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import models.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import service.UnauthorizedException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userDAO;

    public WebSocketHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        int gameId = -1;
        Session session = ctx.session;
        Gson gson = new Gson();
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
            ChessMove move = null;
            if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                command = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                move = ((MakeMoveCommand) command).getMove();
            }

            if (command.getGameID() == null || command.getGameID() < 0) {
                throw new IllegalArgumentException("Error, invalid game ID: " + command.getGameID());
            }
            if (userDAO.getGame(Integer.toString(command.getGameID())) == null) {
                throw new IllegalArgumentException("Error, game not found: " + command.getGameID());
            }
            if (command.getAuthToken() == null || userDAO.getUserByToken(command.getAuthToken()) == null) {
                throw new IllegalArgumentException("Error, bad token: " + command.getGameID());
            }

            gameId = command.getGameID();
            String username = userDAO.getUserByToken(command.getAuthToken());
            GameData gameData = userDAO.getGame(Integer.toString(gameId));
            ChessGame game = gameData.game();
            String color = "an observer";
            if (Objects.equals(gameData.whiteUsername(), username)) {
                color = "white";
            }
            if (Objects.equals(gameData.blackUsername(), username)) {
                color = "black";
            }

            switch (command.getCommandType()) {
                case CONNECT -> connect(gameId, username, session, color, game);
                case MAKE_MOVE -> makeMove(gameId, game, move, username, session, color, gameData);
                case LEAVE -> leaveGame(gameId, username, session, gameData, color);
                case RESIGN -> resign(gameId, username, gameData, game, color);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage());
            String message = gson.toJson(errorMessage);
            session.getRemote().sendString(message);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String visitorName, Session session, String color, ChessGame game) throws IOException {
        connections.add(gameID, session);
        LoadGameMessage loadGameMsg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(loadGameMsg));
        var message = String.format("%s joined the game as %s", visitorName, color);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
    }

    private void leaveGame(int gameID, String visitorName, Session session, GameData gameData, String color) throws IOException {
        var message = String.format("%s left the game", visitorName);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();
        if (Objects.equals(color, "white")) {
            white = null;
        }
        if (Objects.equals(color, "black")) {
            black = null;
        }
        GameData newGameData = new GameData(gameID,white,black,gameData.gameName(),gameData.game());
        userDAO.updateGame(newGameData);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }

    public void makeMove(int gameID, ChessGame game, ChessMove move, String username,
                         Session session, String color, GameData gameData) {
        try {
            if (!game.isActive()) {
                throw new IllegalArgumentException("Error, this game has ended");
            }
            if (!Objects.equals(color, "white") && !Objects.equals(color, "black")) {
                throw new IllegalArgumentException("Error, you are an observer");
            }
            ChessGame.TeamColor capsColor = ChessGame.TeamColor.WHITE;
            if (color.equals("black")) {capsColor = ChessGame.TeamColor.BLACK;}
            if (!Objects.equals(game.getTeamTurn(), capsColor)) {
                throw new IllegalArgumentException("Error, not your turn");
            }
            game.makeMove(move);
            GameData newGameData = new GameData(gameID,gameData.whiteUsername(),gameData.blackUsername(), gameData.gameName(),game);
            userDAO.updateGame(newGameData);
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(gameID, null, notification);
            var message = String.format("%s made the move %s", username,move);
            var newNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, session, newNotification);
            var stateMessage = "";
            if (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)) {
                if (game.isInCheck(ChessGame.TeamColor.WHITE) || game.isInCheck(ChessGame.TeamColor.BLACK)) {
                    stateMessage = String.format("Checkmate, game over");
                    game.setGameInactive();
                } else {
                    stateMessage = String.format("Check");
                }
                newNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, stateMessage);
                connections.broadcast(gameID, null, newNotification);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(int gameID, String username, GameData gameData, ChessGame game, String color) throws IOException {
        if (!Objects.equals(color, "white") && !Objects.equals(color, "black")) {
            throw new IllegalArgumentException("Error, you are an observer");
        }
        if (!game.isActive()) {
            throw new IllegalArgumentException("Error, this game has ended");
        }
        var message = String.format("%s has resigned, the game has ended", username);
        game.setGameInactive();
        GameData newGameData = new GameData(gameID,gameData.whiteUsername(),gameData.blackUsername(), gameData.gameName(),game);
        userDAO.updateGame(newGameData);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, null, notification);
    }
}
