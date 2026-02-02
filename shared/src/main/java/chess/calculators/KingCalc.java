package chess.calculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingCalc {
    ChessBoard board;
    ChessPosition startPos;

    public KingCalc(ChessBoard board, ChessPosition startPos){
        this.board = board;
        this.startPos = startPos;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(startPos).getTeamColor();
        options.addAll(new Valid(board,startPos,color,1,1).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,0,1).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,-1,1).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,1,0).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,-1,0).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,1,-1).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,0,-1).calc(startPos,false));
        options.addAll(new Valid(board,startPos,color,-1,-1).calc(startPos,false));
        return options;
    }
}
