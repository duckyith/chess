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
    Map<Integer, List<Integer>> whiteMap = new HashMap<>();
    Map<Integer, List<Integer>> blackMap = new HashMap<>();

    public HighlightDrawBoard(ChessGame game, ChessPosition piece) {
        this.piece = piece;
        this.game = game;
        highlighted.addAll(game.validMoves(piece));
        whiteMap.computeIfAbsent(piece.getRow(), k -> new ArrayList<>()).add(9-piece.getColumn());
        int row;
        int col;
        for (int i = 0; i < highlighted.size(); i++) {
            row = highlighted.get(i).getEndPosition().getRow(); //correct
            col = 9 - highlighted.get(i).getEndPosition().getColumn(); //correct
            whiteMap.computeIfAbsent(row, k -> new ArrayList<>()).add(col);
        }
        blackMap.computeIfAbsent(9-piece.getRow(), k -> new ArrayList<>()).add(piece.getColumn());
        for (int i = 0; i < highlighted.size(); i++) {
            row = 9-highlighted.get(i).getEndPosition().getRow();
            col = highlighted.get(i).getEndPosition().getColumn();
            blackMap.computeIfAbsent(row, k -> new ArrayList<>()).add(col);
        }
    }

    public String drawWhite() {
        StringBuilder board = new StringBuilder();
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j --) {
                if (i < 1 || j < 1 || i > 8 || j > 8) {
                    board.append(SET_BG_COLOR_WHITE);
                    pickBGWhite(board, i, j);
                } else {
                    if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)){
                        board.append(SET_BG_COLOR_LIGHT_BROWN);
                        checkSpecialLight(board,i,j);
                    } else {
                        board.append(SET_BG_COLOR_DARK_BROWN);
                        checkSpecialDark(board,i,j);
                    }
                    if (game.getBoard().getPiece(new ChessPosition(i, 9-j)) == null) {
                        board.append(EMPTY);
                    } else {
                        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(i, 9-j));
                        drawPiece(board, piece);
                    }
                }
            }
            board.append(RESET_BG_COLOR);
            board.append("\n");
        }
        return board.toString();
    }

    public void checkSpecialLight(StringBuilder board, int row, int col){
        if (whiteMap.containsKey(row) && whiteMap.get(row).contains(col)){
            board.append(SET_BG_COLOR_GREEN);
        }
        if (row == piece.getRow() && col == 9-piece.getColumn()){
            board.append(SET_BG_COLOR_RED);
        }
    }

    public void checkSpecialDark(StringBuilder board, int row, int col){
        if (whiteMap.containsKey(row) && whiteMap.get(row).contains(col)){
            board.append(SET_BG_COLOR_DARK_GREEN);
        }
        if (row == piece.getRow() && col == 9-piece.getColumn()){
            board.append(SET_BG_COLOR_RED);
        }
    }

    public String drawBlack() {
        StringBuilder board = new StringBuilder();
        for (int i = 9; i >= 0; i--) {
            for (int j = 9; j >= 0; j --) {
                if (i < 1 || j < 1 || i > 8 || j > 8) {
                    board.append(SET_BG_COLOR_WHITE);
                    pickBGBlack(board, i, j);
                } else {
                    if ((i % 2 == 0 && j % 2 == 0) || (i % 2 != 0 && j % 2 != 0)){
                        board.append(SET_BG_COLOR_LIGHT_BROWN);
                        checkSpecialLightB(board,i,j);
                    } else {
                        board.append(SET_BG_COLOR_DARK_BROWN);
                        checkSpecialDarkB(board,i,j);}
                    if (game.getBoard().getPiece(new ChessPosition(9-i, j)) == null) {
                        board.append(EMPTY);
                    } else {
                        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(9-i, j));
                        drawPiece(board, piece);
                    }
                }
            }
            board.append(RESET_BG_COLOR);
            board.append("\n");
        }
        return board.toString();
    }

    public void checkSpecialLightB(StringBuilder board, int row, int col){
        if (blackMap.containsKey(row) && blackMap.get(row).contains(col)){
            board.append(SET_BG_COLOR_GREEN);
        }
        if (row == 9-piece.getRow() && col == piece.getColumn()){
            board.append(SET_BG_COLOR_RED);
        }
    }

    public void checkSpecialDarkB(StringBuilder board, int row, int col){
        if (blackMap.containsKey(row) && blackMap.get(row).contains(col)){
            board.append(SET_BG_COLOR_DARK_GREEN);
        }
        if (row == 9-piece.getRow() && col == piece.getColumn()){
            board.append(SET_BG_COLOR_RED);
        }
    }

    public void drawPiece(StringBuilder board, ChessPiece piece){
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            board.append(SET_TEXT_COLOR_WHITE);
            switch (piece.getPieceType()) {
                case KING -> board.append(BLACK_KING);
                case QUEEN -> board.append(BLACK_QUEEN);
                case BISHOP -> board.append(BLACK_BISHOP);
                case KNIGHT -> board.append(BLACK_KNIGHT);
                case ROOK -> board.append(BLACK_ROOK);
                case PAWN -> board.append(BLACK_PAWN);
            }
        } else {
            board.append(SET_TEXT_COLOR_BLACK);
            switch (piece.getPieceType()) {
                case KING -> board.append(BLACK_KING);
                case QUEEN -> board.append(BLACK_QUEEN);
                case BISHOP -> board.append(BLACK_BISHOP);
                case KNIGHT -> board.append(BLACK_KNIGHT);
                case ROOK -> board.append(BLACK_ROOK);
                case PAWN -> board.append(BLACK_PAWN);
            }
        }
    }

    public void pickBGWhite(StringBuilder board, int row, int col) {
        board.append(SET_TEXT_COLOR_BLACK);
        if (row == 0 || row == 9) {
            switch (col) {
                case 0, 9 -> board.append("   ");
                case 1 -> board.append("h ");
                case 2 -> board.append("g" + BETWEEN);
                case 3 -> board.append("f" + BETWEEN);
                case 4 -> board.append("e" + BETWEEN);
                case 5 -> board.append("d" + BETWEEN);
                case 6 -> board.append("c" + BETWEEN);
                case 7 -> board.append("b" + BETWEEN);
                case 8 -> board.append(" a" + BETWEEN);
            }
        } else {
            switch (row) {
                case 1 -> board.append(" 1 ");
                case 2 -> board.append(" 2 ");
                case 3 -> board.append(" 3 ");
                case 4 -> board.append(" 4 ");
                case 5 -> board.append(" 5 ");
                case 6 -> board.append(" 6 ");
                case 7 -> board.append(" 7 ");
                case 8 -> board.append(" 8 ");
            }
        }
    }

    public void pickBGBlack(StringBuilder board, int row, int col) {
        board.append(SET_TEXT_COLOR_BLACK);
        if (row == 0 || row == 9) {
            switch (col) {
                case 0, 9 -> board.append("   ");
                case 1 -> board.append("a ");
                case 2 -> board.append("b" + BETWEEN);
                case 3 -> board.append("c" + BETWEEN);
                case 4 -> board.append("d" + BETWEEN);
                case 5 -> board.append("e" + BETWEEN);
                case 6 -> board.append("f" + BETWEEN);
                case 7 -> board.append("g" + BETWEEN);
                case 8 -> board.append(" h" + BETWEEN);
            }
        } else {
            switch (row) {
                case 1 -> board.append(" 8 ");
                case 2 -> board.append(" 7 ");
                case 3 -> board.append(" 6 ");
                case 4 -> board.append(" 5 ");
                case 5 -> board.append(" 4 ");
                case 6 -> board.append(" 3 ");
                case 7 -> board.append(" 2 ");
                case 8 -> board.append(" 1 ");
            }
        }
    }
}
