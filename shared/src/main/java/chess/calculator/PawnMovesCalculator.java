package chess.calculator;

import chess.*;

import java.util.ArrayList;

public class PawnMovesCalculator {

    ChessBoard board;
    ChessPosition position;

    public PawnMovesCalculator(ChessBoard board, ChessPosition position){
        this.board = board;
        this.position = position;
    }

    public ArrayList<ChessMove> calc(){
        ArrayList<ChessMove> options = new ArrayList<ChessMove>();
        //white pawn moves, check double
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition noCaptureTarget = new ChessPosition(position.getRow()+1,position.getColumn());
            if (board.getPiece(noCaptureTarget) == null) {
                options.add(new ChessMove(position, noCaptureTarget, null));
                if (position.getRow() == 2){
                    ChessPosition noCaptureTarget2 = new ChessPosition(position.getRow()+2,position.getColumn());
                    if (board.getPiece(noCaptureTarget2) == null) {
                        options.add(new ChessMove(position, noCaptureTarget2, null));
                    }
                }
            }
            //capture? (pos column, neg column)
            ChessPosition capture = new ChessPosition(position.getRow()+1,position.getColumn()+1);
            if (capture.getColumn() <= 8 && capture.getColumn() >= 1 && board.getPiece(capture) != null && board.getPiece(capture).getTeamColor() != board.getPiece(position).getTeamColor()){
                options.addAll(new ValidCalc(board, position, 1, 1, false).calc(position));
            }
            capture = new ChessPosition(position.getRow()+1,position.getColumn()-1);
            if (capture.getColumn() <= 8 && capture.getColumn() >= 1 && board.getPiece(capture) != null && board.getPiece(capture).getTeamColor() != board.getPiece(position).getTeamColor()){
                options.addAll(new ValidCalc(board, position, 1, -1, false).calc(position));
            }

            if (position.getRow() == 7 && !options.isEmpty()){
                ArrayList<ChessMove> optionsCopy = new ArrayList<ChessMove>(options);
                options.clear();
                for (int i = 0; !optionsCopy.isEmpty(); i++) {
                    ChessPosition target = optionsCopy.get(0).getEndPosition();
                    optionsCopy.remove(0);
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                }
            }
        } else {
            ChessPosition noCaptureTarget = new ChessPosition(position.getRow()-1,position.getColumn());
            if (board.getPiece(noCaptureTarget) == null) {
                options.add(new ChessMove(position, noCaptureTarget, null));
                if (position.getRow() == 7){
                    ChessPosition noCaptureTarget2 = new ChessPosition(position.getRow()-2,position.getColumn());
                    if (board.getPiece(noCaptureTarget2) == null) {
                        options.add(new ChessMove(position, noCaptureTarget2, null));
                    }
                }
            }
            //capture? (pos column, neg column)
            ChessPosition capture = new ChessPosition(position.getRow()-1,position.getColumn()+1);
            if (capture.getColumn() <= 8 && capture.getColumn() >= 1 && board.getPiece(capture) != null && board.getPiece(capture).getTeamColor() != board.getPiece(position).getTeamColor()){
                options.addAll(new ValidCalc(board, position, -1, 1, false).calc(position));
            }
            capture = new ChessPosition(position.getRow()-1,position.getColumn()-1);
            if (capture.getColumn() <= 8 && capture.getColumn() >= 1 && board.getPiece(capture) != null && board.getPiece(capture).getTeamColor() != board.getPiece(position).getTeamColor()){
                options.addAll(new ValidCalc(board, position, -1, -1, false).calc(position));
            }

            if (position.getRow() == 2 && !options.isEmpty()){
                ArrayList<ChessMove> optionsCopy = new ArrayList<ChessMove>(options);
                options.clear();
                for (int i = 0; 1 <= optionsCopy.size(); i++) {
                    ChessPosition target = optionsCopy.get(0).getEndPosition();
                    optionsCopy.remove(0);
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                    options.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                }
            }
        }
        return options;
    }

}
