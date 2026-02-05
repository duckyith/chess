package chess.calculators;

import chess.*;

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
                if (rightCastle()) {
                    ChessPosition target = new ChessPosition(position.getRow(), position.getColumn()+2);
                    options.add(new ChessMove(position, target, null));
                }
                if (leftCastle()) {
                    ChessPosition target = new ChessPosition(position.getRow(), position.getColumn()-2);
                    options.add(new ChessMove(position, target, null));
                }
            }
        }
        return options;
    }

    public boolean rightCastle() {
        ChessPosition rookPos = new ChessPosition(position.getRow(), 8);
        ChessPiece rook = board.getPiece(rookPos);
        ChessPiece king = board.getPiece(position);
        if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.getTeamColor() == board.getPiece(position).getTeamColor() && !rook.getHasMoved()) {
            ChessPiece null1 = board.getPiece(new ChessPosition(position.getRow(), position.getColumn() + 1));
            ChessPiece null2 = board.getPiece(new ChessPosition(position.getRow(), position.getColumn() + 2));
            if (null1 == null && null2 == null) {
                ChessGame testGame = new ChessGame();
                testGame.setBoard(new ChessBoard(board));
                if (!testGame.isInCheck(king.getTeamColor())) {
                    testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() + 1), king);
                    testGame.getBoard().addPiece(position, null);
                    if (!testGame.isInCheck(king.getTeamColor())) {
                        testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() + 2), king);
                        testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() + 1), null);
                        return !testGame.isInCheck(king.getTeamColor());
                    }
                }
            }
        }
        return false;
    }

    public boolean leftCastle() {
        ChessPosition rookPos = new ChessPosition(position.getRow(), 1);
        ChessPiece rook = board.getPiece(rookPos);
        ChessPiece king = board.getPiece(position);
        if (rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.getTeamColor() == board.getPiece(position).getTeamColor() && !rook.getHasMoved()) {
            ChessPiece null1 = board.getPiece(new ChessPosition(position.getRow(), position.getColumn() - 1));
            ChessPiece null2 = board.getPiece(new ChessPosition(position.getRow(), position.getColumn() - 2));
            if (null1 == null && null2 == null) {
                ChessGame testGame = new ChessGame();
                testGame.setBoard(new ChessBoard(board));
                if (!testGame.isInCheck(king.getTeamColor())) {
                    testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() - 1), king);
                    testGame.getBoard().addPiece(position, null);
                    if (!testGame.isInCheck(king.getTeamColor())) {
                        testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() - 2), king);
                        testGame.getBoard().addPiece(new ChessPosition(position.getRow(), position.getColumn() - 1), null);
                        return !testGame.isInCheck(king.getTeamColor());
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<ChessMove> checkEnPassant(ChessMove prevMove) {
        ArrayList<ChessMove> options = new ArrayList<>();
        if (board.getPiece(position) != null && board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN) {
            if (prevMove != null) {
                ChessPosition targetPos = prevMove.getEndPosition();
                ChessPiece targetPiece = board.getPiece(targetPos);
                ChessPiece.PieceType targetType = targetPiece.getPieceType();
                int colDist = Math.abs(targetPos.getColumn() - position.getColumn());
                int moveDist = Math.abs(prevMove.getStartPosition().getRow() - prevMove.getEndPosition().getRow());
                //1 column away, it's a pawn, it moved more than 1 last turn
                if (colDist == 1 && targetType == ChessPiece.PieceType.PAWN && moveDist > 1) {
                    ChessPiece pawn = board.getPiece(position);
                    ChessGame.TeamColor color = pawn.getTeamColor();
                    if (color == ChessGame.TeamColor.WHITE && position.getRow() == 5) {
                        options.add(new ChessMove(position, new ChessPosition (targetPos.getRow()+1,targetPos.getColumn()), null));
                    } else if (color == ChessGame.TeamColor.BLACK && position.getRow() == 4) {
                        options.add(new ChessMove(position, new ChessPosition (targetPos.getRow()-1,targetPos.getColumn()), null));
                    }
                }
            }
        }
        return options;
    }
}
