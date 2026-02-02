package chess.calculators;

import chess.*;

import java.util.ArrayList;

public class Valid {
    ChessBoard board;
    ChessPosition startPos;
    ChessGame.TeamColor color;
    int rowChange;
    int colChange;

    public Valid(ChessBoard board, ChessPosition startPos, ChessGame.TeamColor color, int rowChange, int colChange){
        this.board = board;
        this.startPos = startPos;
        this.color = color;
        this.rowChange = rowChange;
        this.colChange = colChange;
    }

    public ArrayList<ChessMove> calc(ChessPosition fromPos, boolean recursive){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessPosition target = new ChessPosition(fromPos.getRow()+rowChange,fromPos.getColumn()+colChange);
        if (target.getRow() >= 9 || target.getColumn() >= 9 || target.getRow() <= 0 || target.getColumn() <= 0){
            return options;
        }
        if (board.getPiece(target) == null){
            options.add(new ChessMove(startPos,target,null));
            if (recursive){
                options.addAll(calc(target,true));
            }
        } else {
            if (board.getPiece(target).getTeamColor() != color){
                options.add(new ChessMove(startPos,target,null));
            } else {
                return options;
            }
        }
        return options;
    }

    public ArrayList<ChessMove> pawnMoveCalc(boolean promo){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessPosition target = new ChessPosition(startPos.getRow()+rowChange,startPos.getColumn()+colChange);
        boolean out = target.getRow() >= 9 || target.getColumn() >= 9 || target.getRow() <= 0 || target.getColumn() <= 0;
        if (!out && board.getPiece(target) == null) {
            if (promo) {
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.BISHOP));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.ROOK));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.QUEEN));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.KNIGHT));
            } else {
                options.add(new ChessMove(startPos, target, null));
            }
        }
        return options;
    }

    public ArrayList<ChessMove> pawnCapCalc(boolean promo){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessPosition target = new ChessPosition(startPos.getRow()+rowChange,startPos.getColumn()+colChange);
        boolean out = target.getRow() >= 9 || target.getColumn() >= 9 || target.getRow() <= 0 || target.getColumn() <= 0;
        if (!out && board.getPiece(target) != null && board.getPiece(target).getTeamColor() != color) {
            if (promo) {
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.BISHOP));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.ROOK));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.QUEEN));
                options.add(new ChessMove(startPos, target, ChessPiece.PieceType.KNIGHT));
            } else {
                options.add(new ChessMove(startPos, target, null));
            }
        }
        return options;
    }
}
