package ui;

import exception.ResponseException;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL implements NotificationHandler {
    public String username = null;
    public String authToken = null;
    public State state = State.SIGNEDOUT;
    LoggedOutClient loggedOutClient;
    LoggedInClient loggedInClient;
    InGameClient inGameClient;

    public REPL(String serverUrl) {
        loggedOutClient = new LoggedOutClient(serverUrl);
        loggedInClient = new LoggedInClient(serverUrl);
        inGameClient = new InGameClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_WHITE + "Welcome to chess! Sign in to start.");
        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state == State.SIGNEDOUT){result = evalLoggedOut(line);}
                if (state == State.SIGNEDIN){result = evalLoggedIn(line);}
                if (state == State.INGAME){result = evalInGame(line);}
                updateState();
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_WHITE + "\n");
        String printState = "";
        switch (state) {
            case State.SIGNEDOUT -> printState = "[LOGGED_OUT]";
            case State.SIGNEDIN -> printState = "[LOGGED_IN]";
            case State.INGAME -> printState = "[IN_GAME]";
        }
        System.out.print(printState);
        System.out.print(">>>");
    }

    public String evalLoggedOut(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "register" -> loggedOutClient.register(params);
            case "login" -> loggedOutClient.login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String evalLoggedIn(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "logout" -> loggedInClient.logout(authToken, username);
            case "create" -> loggedInClient.create(params[0], authToken);
            case "list" -> loggedInClient.list(authToken);
            case "play" -> loggedInClient.play(authToken,params);
            case "observe" -> loggedInClient.observe(params[0]);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String evalInGame(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "move" -> "not implemented";
            case "resign" -> "not implemented";
            case "leave" -> inGameClient.leave();
            case "redraw" -> inGameClient.redraw();
            case "highlight" -> "not implemented";
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email@email.com>
                    - login <username> <password>
                    - quit
                    """;
        }
        if (state == State.SIGNEDIN) {
            return """
                    - logout
                    - create <gameName>
                    - list
                    - play <gameNumber> <COLOR>
                    - observe <gameNumber>
                    - quit
                    """;
        }
        return """
                - move <from> <to>
                - resign
                - redraw
                - highlight <piece>
                - leave
                - quit
                """;
    }

    public void updateState() {
        if (loggedOutClient.forward){
            username = loggedOutClient.username;
            authToken = loggedOutClient.authToken;
            state = State.SIGNEDIN;
            loggedOutClient.forward = false;
        }
        if (loggedInClient.back){
            username = null;
            authToken = null;
            state = State.SIGNEDOUT;
            loggedInClient.back = false;
        }
        if (loggedInClient.forward){
            state = State.INGAME;
            loggedInClient.forward = false;
            inGameClient.update(loggedInClient.gameID, loggedInClient.username, loggedInClient.authToken, loggedInClient.gameData);
        }
        if (inGameClient.back){
            state = State.SIGNEDIN;
            inGameClient.back = false;
        }
    }

    @Override
    public void notify(ServerMessage message) {

    }
}
