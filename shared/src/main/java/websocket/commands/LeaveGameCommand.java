package websocket.commands;

public class LeaveGameCommand extends UserGameCommand{
    public LeaveGameCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
