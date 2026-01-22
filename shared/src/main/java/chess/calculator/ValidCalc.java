package chess.calculator;

import chess.*;

import java.util.ArrayList;

public class ValidCalc {

    ChessBoard board;
    ChessPosition position;
    int rowChange;
    int columnChange;
    boolean recursive;

    public ValidCalc(ChessBoard board, ChessPosition position, int rowChange, int columnChange, boolean recursive){
        this.board = board;
        this.position = position;
        this.rowChange = rowChange;
        this.columnChange = columnChange;
        this.recursive = recursive;
    }

    public ArrayList<ChessMove> calc(ChessPosition position){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();

        ChessPiece piece = board.getPiece(this.position);
        ChessGame.TeamColor team = piece.getTeamColor();
        ChessPosition target = new ChessPosition(position.getRow()+rowChange, position.getColumn()+columnChange);
        ChessPiece targetPiece;

        if (target.getColumn() > 8 || target.getColumn() < 1 || target.getRow() > 8 || target.getRow() < 1) {
            return options;
        } else {
            targetPiece = board.getPiece(target);
        }
        if (targetPiece == null){
            options.add(new ChessMove(this.position,target,null));
            if (recursive) {
                options.addAll(calc(target));
            }
        } else {
            if (targetPiece.getTeamColor() == team) {
                return options;
            }
            if (targetPiece.getTeamColor() != team) {
                options.add(new ChessMove(this.position, target, null));
                return options;
            }
        }
        //System.out.println(options);
        return options;
    }
}
