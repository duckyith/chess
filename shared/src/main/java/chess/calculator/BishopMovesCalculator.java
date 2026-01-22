package chess.calculator;

import chess.*;
import chess.calculator.diagonal.ValidCalc;

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
        options.addAll(new ValidCalc(board, position, 1, 1, true).calc(position));
        options.addAll(new ValidCalc(board, position, -1, 1, true).calc(position));
        options.addAll(new ValidCalc(board, position, 1, -1, true).calc(position));
        options.addAll(new ValidCalc(board, position, -1, -1, true).calc(position));
        return options;
    }

}
