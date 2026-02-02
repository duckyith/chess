package chess.calculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class BishopCalc {
    ChessBoard board;
    ChessPosition startPos;

    public BishopCalc(ChessBoard board, ChessPosition startPos){
        this.board = board;
        this.startPos = startPos;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(startPos).getTeamColor();
        options.addAll(new Valid(board,startPos,color,1,1).calc(startPos,true));
        options.addAll(new Valid(board,startPos,color,-1,1).calc(startPos,true));
        options.addAll(new Valid(board,startPos,color,1,-1).calc(startPos,true));
        options.addAll(new Valid(board,startPos,color,-1,-1).calc(startPos,true));
        return options;
    }
}
