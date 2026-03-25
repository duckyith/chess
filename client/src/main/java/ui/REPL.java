package ui;

import exception.ResponseException;
import models.UserData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL {
    public String username = null;
    public String authToken = null;
    public State state = State.SIGNEDOUT;
    public State nextState;
    LoggedOutClient loggedOutClient;

    public REPL(String serverUrl) {
        ServerFacade server = new ServerFacade(serverUrl);
        loggedOutClient = new LoggedOutClient(serverUrl);
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
        System.out.print(SET_TEXT_COLOR_WHITE + "\n" + ">>> ");
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
            case "logout" -> "not implemented";
            case "create" -> "not implemented";
            case "list" -> "not implemented";
            case "play" -> "not implemented";
            case "observe" -> "not implemented";
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
            case "leave" -> "not implemented";
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
                    - register <username> <password> <email@email.com>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - move <from> <to>
                - resign
                - leave
                - quit
                """;
    }

    public void updateState() {
        if (loggedOutClient.success){
            username = loggedOutClient.username;
            authToken = loggedOutClient.authToken;
            state = State.SIGNEDIN;
            loggedOutClient.success = false;
        }
    }
}
