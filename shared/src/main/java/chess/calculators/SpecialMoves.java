package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;

public class SpecialMoves {

    ChessBoard board;
    ChessPosition position;

    public SpecialMoves(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> checkCastle() {
        ArrayList<ChessMove> options = new ArrayList<>();
        if (board.getPiece(position) != null) {
            ChessPiece piece = board.getPiece(position);
            if (piece.getPieceType() == ChessPiece.PieceType.KING && !piece.getHasMoved()) {
                if (rightCastle() == true) {
                    options.add
                }
            }
        }
        return options;
    }

    public
}
