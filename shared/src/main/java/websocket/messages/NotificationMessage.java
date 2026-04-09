package websocket.messages;

public class NotificationMessage extends ServerMessage {
    public String message;
    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }
}
