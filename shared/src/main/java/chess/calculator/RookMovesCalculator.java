package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class RookMovesCalculator {

    ChessBoard board;
    ChessPosition position;

    public RookMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        options.addAll(new ValidCalc(board, position, 1, 0, true).calc(position));
        options.addAll(new ValidCalc(board, position, 0, 1, true).calc(position));
        options.addAll(new ValidCalc(board, position, -1, 0, true).calc(position));
        options.addAll(new ValidCalc(board, position, 0, -1, true).calc(position));
        return options;
    }

}
