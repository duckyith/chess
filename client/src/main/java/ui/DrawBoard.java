package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;

public class DrawBoard {
    ChessGame game;

    public DrawBoard (ChessGame game) {
        this.game = game;
    }

    public String drawWhite() {
        String board = "";
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j --) {
                if (i < 1 || j < 1 || i > 8 || j > 8) {
                    board += SET_BG_COLOR_WHITE;
                    board = pickBGLetter(board, i, j);
                } else {
                    if (i % 2 == 0 && j % 2 == 0){
                        board += SET_BG_COLOR_LIGHT_GREY;
                    } else {board += SET_BG_COLOR_DARK_GREY;}
                    if (game.getBoard().getPiece(new ChessPosition(i, j)) == null) {
                        board += " ";
                    } else {
                        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, j));
                        board = drawPiece(board, piece);
                    }
                }
            }
            board += "\n";
        }
        return board;
    }

    public String drawBlack() {

        return "empty";
    }

    public String drawPiece(String board, ChessPiece piece){
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case KING -> board += WHITE_KING;
                case QUEEN -> board += WHITE_QUEEN;
                case BISHOP -> board += WHITE_BISHOP;
                case KNIGHT -> board += WHITE_KNIGHT;
                case ROOK -> board += WHITE_ROOK;
                case PAWN -> board += WHITE_PAWN;
            }
        } else {
            switch (piece.getPieceType()) {
                case KING -> board += BLACK_KING;
                case QUEEN -> board += BLACK_QUEEN;
                case BISHOP -> board += BLACK_BISHOP;
                case KNIGHT -> board += BLACK_KNIGHT;
                case ROOK -> board += BLACK_ROOK;
                case PAWN -> board += BLACK_PAWN;
            }
        }
        return board;
    }

    public String pickBGLetter(String board, int row, int col) {
        if (row == 0 || row == 9) {
            switch (col) {
                case 0, 9 -> board += " ";
                case 1 -> board += "a";
                case 2 -> board += "b";
                case 3 -> board += "c";
                case 4 -> board += "d";
                case 5 -> board += "e";
                case 6 -> board += "f";
                case 7 -> board += "g";
                case 8 -> board += "h";
            }
        } else {
            switch (row) {
                case 1 -> board += "1";
                case 2 -> board += "2";
                case 3 -> board += "3";
                case 4 -> board += "4";
                case 5 -> board += "5";
                case 6 -> board += "6";
                case 7 -> board += "7";
                case 8 -> board += "8";
            }
        }
        return board;
    }
}
