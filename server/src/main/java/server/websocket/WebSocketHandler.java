package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;
        Gson gson = new Gson();
//        try {
//            UserGameCommand command = gson.fromJson(
//                    wsMessageContext.message(), UserGameCommand.class);
//
//            gameId = command.getGameID();
//            String username = getUsername(command.getAuthString());
//            saveSession(gameId, session);
//
//            switch (command.getCommandType()) {
//                case CONNECT -> connect(session, username, (ConnectCommand) command);
//                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
//                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
//                case RESIGN -> resign(session, username, (ResignCommand) command);
//            }
//        } catch (UnauthorizedException ex) {
//            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
//        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void joined(String visitorName, Session session, String color) throws IOException {
        connections.add(session);
        var message = String.format("%s joined the game as %s", visitorName, color);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
    }

    private void left(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the game, waiting for new player...", visitorName);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeMove(String petName, String sound) {
        try {
            var notification = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            connections.broadcast(null, notification);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
