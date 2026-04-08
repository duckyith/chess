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
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import service.UnauthorizedException;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
        session.getRemote().sendString("\"hello world\"");
        Gson gson = new Gson();
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            gameId = command.getGameID();
            String username = userDAO.getUserByToken(command.getAuthToken());
            ChessGame game = userDAO.getGame(Integer.toString(gameId)).game(); //wrong? placeholder
            connections.add(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(gameId, username, session, "white"); //wrong, placeholder
                case MAKE_MOVE -> makeMove(gameId, game);
                case LEAVE -> leaveGame(gameId, username, session);
                case RESIGN -> resign(gameId, username, session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //sendMessage(session, gameId, new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: " + ex.getMessage()));
            session.getRemote().sendString("\"something went wrong\"");
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(int gameID, String visitorName, Session session, String color) throws IOException {
        connections.add(gameID, session);
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
