package chess.calculator;

import chess.*;

import java.util.ArrayList;

public class PawnMovesCalculator {

    ChessBoard board;
    ChessPosition position;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            options.addAll(new ValidCalc(board, position, 1, 0, false).calc(position));
            if (position.getRow() == 2 && !options.isEmpty()){
                options.addAll(new ValidCalc(board, position, 2, 0, false).calc(position));
            }
            if (position.getRow() == 7 && !options.isEmpty()){
                ChessMove fake = options.getFirst();
                options.clear();
                ChessPosition target = new ChessPosition(position.getRow()+1,position.getColumn());
                options.add(new ChessMove(position,target, ChessPiece.PieceType.BISHOP));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.ROOK));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.QUEEN));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.KNIGHT));
            }
        } else {
            options.addAll(new ValidCalc(board, position, -1, 0, false).calc(position));
            if (position.getRow() == 7 && !options.isEmpty()){
                options.addAll(new ValidCalc(board, position, -2, 0, false).calc(position));
            }
            if (position.getRow() == 2 && !options.isEmpty()){
                ChessMove fake = options.getFirst();
                options.clear();
                ChessPosition target = new ChessPosition(position.getRow()-1,position.getColumn());
                options.add(new ChessMove(position,target, ChessPiece.PieceType.BISHOP));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.ROOK));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.QUEEN));
                options.add(new ChessMove(position,target, ChessPiece.PieceType.KNIGHT));
            }
        }
        return options;
    }

}
