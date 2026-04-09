package client.websocket;

import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
}