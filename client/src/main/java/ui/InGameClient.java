package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import models.GameData;

import java.util.Objects;

public class InGameClient {
    public final WebSocketFacade webSocketFacade;
    public int gameID;
    public String username;
    public String authToken;
    public GameData gameData;
    public String color;
    public ChessGame game;
    public boolean back = false;


    public InGameClient(String serverUrl, NotificationHandler notificationHandler) {
        try {
            this.webSocketFacade = new WebSocketFacade(serverUrl,notificationHandler);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(int gameID, String username, String authToken, GameData gameData, String color) {
        this.gameID = gameID;
        this.username = username;
        this.authToken = authToken;
        this.gameData = gameData;
        this.color = color;
        this.game = gameData.game();
        try {
            webSocketFacade.connectToGame(authToken,gameID);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public String leave() throws ResponseException {
        webSocketFacade.leaveGame(authToken,gameID);
        back = true;
        return "left game";
    }

    public String redraw() {
        if (Objects.equals(color, "black")) {
            return new DrawBoard(game).drawBlack();
        }
        return new DrawBoard(game).drawWhite();
    }

    public String highlight(String space) throws Exception {
        int row = 0;
        int column = 0;
        try {
            String letter = space.substring(0,1);
            row = Integer.parseInt(space.substring(1, 2));
            switch (letter) {
                case "a" -> column = 1;
                case "b" -> column = 2;
                case "c" -> column = 3;
                case "d" -> column = 4;
                case "e" -> column = 5;
                case "f" -> column = 6;
                case "g" -> column = 7;
                case "h" -> column = 8;
                default -> throw new Exception("incorrect formatting");
            }
        } catch (Exception e){return "incorrect formatting";}
        chess.ChessPosition piece = new ChessPosition(row,column);
        try {
            if (Objects.equals(color, "black")) {
                return new HighlightDrawBoard(game, piece).drawBlack();
            }
            return new HighlightDrawBoard(game, piece).drawWhite();
        } catch (Exception e) {
            return "No piece to highlight";
        }
    }

    public String resign() throws ResponseException {
        webSocketFacade.resign(authToken,gameID);
        return "you resigned the game";
    }

    public String move(String startPos, String endPos, String promo) throws ResponseException {
        try {
            String startLetter = startPos.substring(0, 1);
            String startNumber = startPos.substring(1, 2);
            String endLetter = endPos.substring(0, 1);
            String endNumber = endPos.substring(1, 2);
            int sc1 = 0;
            int sc2 = Integer.parseInt(startNumber);
            int ec1 = 0;
            int ec2 = Integer.parseInt(endNumber);
            switch (startLetter) {
                case "a" -> sc1 = 1;
                case "b" -> sc1 = 2;
                case "c" -> sc1 = 3;
                case "d" -> sc1 = 4;
                case "e" -> sc1 = 5;
                case "f" -> sc1 = 6;
                case "g" -> sc1 = 7;
                case "h" -> sc1 = 8;
            }
            switch (endLetter) {
                case "a" -> ec1 = 1;
                case "b" -> ec1 = 2;
                case "c" -> ec1 = 3;
                case "d" -> ec1 = 4;
                case "e" -> ec1 = 5;
                case "f" -> ec1 = 6;
                case "g" -> ec1 = 7;
                case "h" -> ec1 = 8;
            }
            ChessPosition start = new ChessPosition(sc2, sc1);
            ChessPosition end = new ChessPosition(ec2, ec1);
            ChessMove move;
            if (promo == null) {
                move = new ChessMove(start, end, null);
            } else {
                ChessPiece.PieceType promotion = ChessPiece.PieceType.QUEEN;
                switch (promo) {
                    case "r" -> promotion = ChessPiece.PieceType.ROOK;
                    case "k" -> promotion = ChessPiece.PieceType.KNIGHT;
                    case "b" -> promotion = ChessPiece.PieceType.BISHOP;
                    case "q" -> promotion = ChessPiece.PieceType.QUEEN;
                }
                move = new ChessMove(start, end, promotion);
            }
            webSocketFacade.move(authToken, gameID, move);
            return "moving";
        } catch (Exception e){
            return "incorrect formatting";
        }
    }
}