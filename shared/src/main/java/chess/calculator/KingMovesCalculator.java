package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;

public class KingMovesCalculator {

    ChessBoard board;
    ChessPosition position;

    public KingMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        options.addAll(new ValidCalc(board, position, 1, 1, false).calc(position));
        options.addAll(new ValidCalc(board, position, -1, 1, false).calc(position));
        options.addAll(new ValidCalc(board, position, 1, -1, false).calc(position));
        options.addAll(new ValidCalc(board, position, -1, -1, false).calc(position));
        options.addAll(new ValidCalc(board, position, 1, 0, false).calc(position));
        options.addAll(new ValidCalc(board, position, 0, 1, false).calc(position));
        options.addAll(new ValidCalc(board, position, -1, 0, false).calc(position));
        options.addAll(new ValidCalc(board, position, 0, -1, false).calc(position));
        return options;
    }

}
