package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ui.EscapeSequences.*;

public class HighlightDrawBoard {
    ChessGame game;
    ChessPosition piece;
    ArrayList<ChessMove> highlighted = new ArrayList<>();
    Map<Integer, List<Integer>> map = new HashMap<>();

    public HighlightDrawBoard(ChessGame game, ChessPosition piece) {
        this.piece = piece;
        this.game = game;
        map.computeIfAbsent(piece.getRow(), k -> new ArrayList<>()).add(piece.getColumn());
        highlighted.addAll(game.validMoves(piece));
        int row;
        int col;
        for (int i = 0; i < highlighted.size(); i++) {
            row = highlighted.get(i).getEndPosition().getRow();
            col = highlighted.get(i).getEndPosition().getColumn();
            map.computeIfAbsent(row, k -> new ArrayList<>()).add(col);
        }
    }

    public String drawWhite() {
        String board = "";
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j --) {
                if (i < 1 || j < 1 || i > 8 || j > 8) {
                    board += SET_BG_COLOR_WHITE;
                    board = pickBGWhite(board, i, j);
                } else {
                    if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)){
                        board += SET_BG_COLOR_LIGHT_BROWN;
                        board += checkSpecialLight(board,i,j);
                    } else {
                        board += SET_BG_COLOR_DARK_BROWN;
                        board += checkSpecialDark(board,i,j);
                    }
                    if (game.getBoard().getPiece(new ChessPosition(i, 9-j)) == null) {
                        board += EMPTY;
                    } else {
                        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, 9-j));
                        board = drawPiece(board, piece);
                    }
                }
            }
            board += RESET_BG_COLOR;
            board += "\n";
        }
        return board;
    }

    public String checkSpecialLight(String board, int row, int col){
        if (map.containsKey(row) && map.get(row).contains(col)){
            return board + SET_BG_COLOR_GREEN;
        }
        if (row == piece.getRow() && col == piece.getColumn()){
            return board + SET_BG_COLOR_RED;
        }
        return board;
    }

    public String checkSpecialDark(String board, int row, int col){
        if (map.containsKey(row) && map.get(row).contains(col)){
            return board + SET_BG_COLOR_DARK_GREEN;
        }
        if (row == piece.getRow() && col == piece.getColumn()){
            return board + SET_BG_COLOR_RED;
        }
        return board;
    }

    public String drawBlack() {
        String board = "";
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j --) {
                if (i < 1 || j < 1 || i > 8 || j > 8) {
                    board += SET_BG_COLOR_WHITE;
                    board = pickBGBlack(board, i, j);
                } else {
                    if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)){
                        board += SET_BG_COLOR_LIGHT_BROWN;
                        board += checkSpecialLight(board,i,j);
                    } else {
                        board += SET_BG_COLOR_DARK_BROWN;
                        board += checkSpecialDark(board,i,j);}
                    if (game.getBoard().getPiece(new ChessPosition(9-i, j)) == null) {
                        board += "   "; //EMPTY
                    } else {
                        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(9-i, j));
                        board = drawPiece(board, piece);
                    }
                }
            }
            board += RESET_BG_COLOR;
            board += "\n";
        }
        return board;
    }

    public String drawPiece(String board, ChessPiece piece){
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            board += SET_TEXT_COLOR_WHITE;
            switch (piece.getPieceType()) {
                case KING -> board += BLACK_KING;
                case QUEEN -> board += BLACK_QUEEN;
                case BISHOP -> board += BLACK_BISHOP;
                case KNIGHT -> board += BLACK_KNIGHT;
                case ROOK -> board += BLACK_ROOK;
                case PAWN -> board += BLACK_PAWN;
            }
        } else {
            board += SET_TEXT_COLOR_BLACK;
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

    public String pickBGWhite(String board, int row, int col) {
        board += SET_TEXT_COLOR_BLACK;
        if (row == 0 || row == 9) {
            switch (col) {
                case 0, 9 -> board += "   ";
                case 1 -> board += "h ";
                case 2 -> board += "g" + BETWEEN;
                case 3 -> board += "f" + BETWEEN;
                case 4 -> board += "e" + BETWEEN;
                case 5 -> board += "d" + BETWEEN;
                case 6 -> board += "c" + BETWEEN;
                case 7 -> board += "b" + BETWEEN;
                case 8 -> board += " a" + BETWEEN;
            }
        } else {
            switch (row) {
                case 1 -> board += " 1 ";
                case 2 -> board += " 2 ";
                case 3 -> board += " 3 ";
                case 4 -> board += " 4 ";
                case 5 -> board += " 5 ";
                case 6 -> board += " 6 ";
                case 7 -> board += " 7 ";
                case 8 -> board += " 8 ";
            }
        }
        return board;
    }

    public String pickBGBlack(String board, int row, int col) {
        board += SET_TEXT_COLOR_BLACK;
        if (row == 0 || row == 9) {
            switch (col) {
                case 0, 9 -> board += "   ";
                case 1 -> board += "a ";
                case 2 -> board += "b" + BETWEEN;
                case 3 -> board += "c" + BETWEEN;
                case 4 -> board += "d" + BETWEEN;
                case 5 -> board += "e" + BETWEEN;
                case 6 -> board += "f" + BETWEEN;
                case 7 -> board += "g" + BETWEEN;
                case 8 -> board += " h" + BETWEEN;
            }
        } else {
            switch (row) {
                case 1 -> board += " 8 ";
                case 2 -> board += " 7 ";
                case 3 -> board += " 6 ";
                case 4 -> board += " 5 ";
                case 5 -> board += " 4 ";
                case 6 -> board += " 3 ";
                case 7 -> board += " 2 ";
                case 8 -> board += " 1 ";
            }
        }
        return board;
    }
}
