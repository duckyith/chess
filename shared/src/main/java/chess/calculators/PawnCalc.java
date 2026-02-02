package chess.calculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class PawnCalc {
    ChessBoard board;
    ChessPosition startPos;

    public PawnCalc(ChessBoard board, ChessPosition startPos){
        this.board = board;
        this.startPos = startPos;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(startPos).getTeamColor();
        if (color == ChessGame.TeamColor.WHITE) {
            if (startPos.getRow() == 7){
                options.addAll(new Valid(board, startPos, color, 1, 0).pawnMoveCalc(true));
                options.addAll(new Valid(board, startPos, color, 1, 1).pawnCapCalc(true));
                options.addAll(new Valid(board, startPos, color, 1, -1).pawnCapCalc(true));
            } else {
                options.addAll(new Valid(board, startPos, color, 1, 0).pawnMoveCalc(false));
                if (startPos.getRow() == 2 && !options.isEmpty())
                    options.addAll(new Valid(board, startPos, color, 2, 0).pawnMoveCalc(false));
                options.addAll(new Valid(board, startPos, color, 1, 1).pawnCapCalc(false));
                options.addAll(new Valid(board, startPos, color, 1, -1).pawnCapCalc(false));
            }
        } else {
            if (startPos.getRow() == 2){
                options.addAll(new Valid(board, startPos, color, -1, 0).pawnMoveCalc(true));
                options.addAll(new Valid(board, startPos, color, -1, 1).pawnCapCalc(true));
                options.addAll(new Valid(board, startPos, color, -1, -1).pawnCapCalc(true));
            } else {
                options.addAll(new Valid(board, startPos, color, -1, 0).pawnMoveCalc(false));
                if (startPos.getRow() == 7 && !options.isEmpty())
                    options.addAll(new Valid(board, startPos, color, -2, 0).pawnMoveCalc(false));
                options.addAll(new Valid(board, startPos, color, -1, 1).pawnCapCalc(false));
                options.addAll(new Valid(board, startPos, color, -1, -1).pawnCapCalc(false));
            }
        }
        return options;
    }
}
