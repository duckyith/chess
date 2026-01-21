package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {
    ChessBoard board;
    ChessPosition position;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> pieceMoves(){
        return List.of();
    }
}
