package server.websocket;

import chess.ChessGame;
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
                case MAKE_MOVE -> makeMove(gameId, game);
                case LEAVE -> leaveGame(gameId, username, session);
                case RESIGN -> resign(gameId, username, session);
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

    private void leaveGame(int gameID, String visitorName, Session session) throws IOException {
        var message = String.format("%s left the game, waiting for new player...", visitorName);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }

    public void makeMove(int gameID, ChessGame game) {
        try {
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(gameID, null, notification);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void resign(int gameID, String visitorName, Session session) throws IOException {
        var message = String.format("%s has resigned, the game has ended", visitorName);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }
}
