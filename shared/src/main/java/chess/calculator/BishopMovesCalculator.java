package chess.calculator;

import chess.*;

import java.util.ArrayList;

public class BishopMovesCalculator {

    ChessBoard board;
    ChessPosition position;

    public BishopMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        options.addAll(checkTRDiagonal(board, position));
        options.addAll(checkBRDiagonal(board, position));
        options.addAll(checkTLDiagonal(board, position));
        options.addAll(checkBLDiagonal(board, position));
        return options;
    }

    private ArrayList<ChessMove> checkTRDiagonal(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();

        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor team = piece.getTeamColor();
        ChessPosition target = new ChessPosition(position.getRow()+1, position.getColumn()+1);
        ChessPiece targetPiece = board.getPiece(target);

        if (target.getColumn() > 8 || target.getColumn() < 1 || target.getRow() > 8 || target.getRow() < 1) {
            return options;
        }
        if (targetPiece.getTeamColor() !=team){
            options.add(new ChessMove(this.position,target,null));
            options.addAll(checkTRDiagonal(board, target));
        }
    }

    private ArrayList<ChessMove> checkBRDiagonal(ChessBoard board, ChessPosition position){

    }

    private ArrayList<ChessMove> checkTLDiagonal(ChessBoard board, ChessPosition position){

    }

    private ArrayList<ChessMove> checkBLDiagonal(ChessBoard board, ChessPosition position){

    }

}
