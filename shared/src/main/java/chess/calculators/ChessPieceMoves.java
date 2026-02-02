package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class ChessPieceMoves {

    ChessBoard board;
    ChessPosition fromPos;

    public ChessPieceMoves(ChessBoard board, ChessPosition fromPos){
        this.board = board;
        this.fromPos = fromPos;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessPiece piece = board.getPiece(fromPos);
        ChessPiece.PieceType type = piece.getPieceType();
        if (type == ChessPiece.PieceType.BISHOP){
            options.addAll(new BishopCalc(board, fromPos).calc());
        }
        if (type == ChessPiece.PieceType.ROOK){
            options.addAll(new RookCalc(board, fromPos).calc());
        }
        if (type == ChessPiece.PieceType.QUEEN){
            options.addAll(new BishopCalc(board, fromPos).calc());
            options.addAll(new RookCalc(board, fromPos).calc());
        }
        if (type == ChessPiece.PieceType.KING){
            options.addAll(new KingCalc(board, fromPos).calc());
        }
        if (type == ChessPiece.PieceType.KNIGHT){
            options.addAll(new KnightCalc(board, fromPos).calc());
        }
        if (type == ChessPiece.PieceType.PAWN){
            options.addAll(new PawnCalc(board, fromPos).calc());
        }
        return options;
    }
}
