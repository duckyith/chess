package client;

import chess.*;
import ui.REPL;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client ♕");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new REPL(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
