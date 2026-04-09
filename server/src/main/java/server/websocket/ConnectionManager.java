package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    public final Gson gson = new Gson();

    public void add(Integer gameID, Session session) {
        connections
                .computeIfAbsent(gameID, id -> ConcurrentHashMap.newKeySet())
                .add(session);
    }

    public void remove(Integer gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage notification) throws IOException {
        Set<Session> sessions = connections.get(gameID);
        String msg = gson.toJson(notification);
        for (Session s : sessions) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(msg);
            }
        }
    }
}