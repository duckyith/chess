package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {
    ChessBoard board;
    ChessPosition position;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> pieceMoves(){
        ChessPiece piece = board.getPiece(position);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            return new BishopMovesCalculator(board, position).calc();
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
            return new RookMovesCalculator(board, position).calc;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
            return new QueenMovesCalculator(board, position).calc;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            return new KingMovesCalculator(board, position).calc;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
            return new KnightMovesCalculator(board, position).calc;
        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
            return new PawnMovesCalculator(board, position).calc;
        }
        return new ArrayList<ChessMove>();
    }
}
