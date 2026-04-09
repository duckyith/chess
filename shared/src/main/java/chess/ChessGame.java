package chess;

import chess.calculators.SpecialMoves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {

    TeamColor teamTurn;
    ChessBoard game;
    ChessMove prevMove;
    Boolean gameActive = true;

    public ChessGame() {
        game = new ChessBoard();
        game.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    public void setGameInactive() {
        gameActive = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> initialMoveSet = new ArrayList<>(game.getPiece(startPosition).pieceMoves(game, startPosition));
        initialMoveSet.addAll(new SpecialMoves(game, startPosition).checkEnPassant(prevMove));
        ArrayList<ChessMove> afterCheck = new ArrayList<>();
        ChessPiece piece = game.getPiece(startPosition);
        ChessGame testGame = new ChessGame();
        for (ChessMove move : initialMoveSet) {
            testGame.setBoard(new ChessBoard(game));
            if (move.getPromotionPiece() != null){
                testGame.getBoard().addPiece(move.getEndPosition(), new ChessPiece (teamTurn, move.getPromotionPiece()));
                testGame.getBoard().addPiece(move.getStartPosition(), null);
            } else {
                testGame.getBoard().addPiece(move.getEndPosition(), piece);
                testGame.getBoard().addPiece(move.getStartPosition(), null);
            }
            if (testGame.isInCheck(piece.getTeamColor())){
                continue;
            }
            afterCheck.add(move);
        }
        afterCheck.addAll(new SpecialMoves(game, startPosition).checkCastle());
        return afterCheck;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        if (game.getPiece(startPos) == null) {
            throw new InvalidMoveException("No piece");
        }
        ChessPiece movingPiece = game.getPiece(startPos);
        if (movingPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Wrong turn");
        }
        if (validMoves(startPos).contains(move)){
            movingPiece.setHasMoved(true);
            if (move.getPromotionPiece() != null){
                game.addPiece(endPos, new ChessPiece (teamTurn, move.getPromotionPiece()));
                game.addPiece(startPos, null);
            } else {
                specialMoveEnPassant(movingPiece,startPos,endPos);
                game.addPiece(endPos, movingPiece);
                game.addPiece(startPos, null);
                specialMoveCastle(movingPiece,startPos,endPos);
            }
        } else {
            throw new InvalidMoveException("Illegal move");
        }
        if (teamTurn == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
        prevMove = move;
    }

    public void specialMoveCastle(ChessPiece movingPiece, ChessPosition startPos, ChessPosition endPos) {
        //if it's a king, is it moving more than 1, which way? move the rook too
        if (movingPiece.getPieceType() == ChessPiece.PieceType.KING){
            int moving = endPos.getColumn() - startPos.getColumn();
            if (Math.abs(moving) >1) {
                int row = startPos.getRow();
                ChessPiece rook;
                if (moving >0) {
                    rook = game.getPiece(new ChessPosition(row,8));
                    game.addPiece(new ChessPosition(row, endPos.getColumn()-1),rook);
                    game.addPiece(new ChessPosition(row,8), null);
                } else {
                    rook = game.getPiece(new ChessPosition(row,1));
                    game.addPiece(new ChessPosition(row, endPos.getColumn()+1),rook);
                    game.addPiece(new ChessPosition(row,1), null);
                }
            }
        }
    }

    public void specialMoveEnPassant(ChessPiece movingPiece, ChessPosition startPos, ChessPosition endPos) {
        //if you are changing columns as a pawn (capturing) but there is no piece there (must be EnPassant)
        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && endPos.getColumn() != startPos.getColumn() && game.getPiece(endPos) == null) {
            game.addPiece(prevMove.getEndPosition(),null);
        }
    }

    public boolean anyValidMoves(TeamColor teamColor) {
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition position = new ChessPosition(i,j);
                if (anyValidMovesHelper(position, teamColor)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean anyValidMovesHelper(ChessPosition position, TeamColor teamColor) {
        if (game.getPiece(position) != null) {
            if (game.getPiece(position).getTeamColor() == teamColor) {
                return !validMoves(position).isEmpty();
            }
        }
        return false;
    }

    public ChessPosition findKing(TeamColor teamColor) {
        int row = 0;
        int col = 0;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition position = new ChessPosition(i,j);
                if (game.getPiece(position) != null) {
                    if (game.getPiece(position).getPieceType() == ChessPiece.PieceType.KING && game.getPiece(position).getTeamColor() == teamColor) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }
        }
        return new ChessPosition(row, col);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingLocation = findKing(teamColor);
        ChessPosition checkingPosition;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                checkingPosition = new ChessPosition(i,j);
                if (isInCheckHelper(checkingPosition, teamColor, kingLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isInCheckHelper(ChessPosition checkingPosition, TeamColor teamColor, ChessPosition kingLocation){
        if (game.getPiece(checkingPosition) != null && game.getPiece(checkingPosition).getTeamColor() != teamColor) {
            Collection<ChessMove> options = game.getPiece(checkingPosition).pieceMoves(game, checkingPosition);
            for (ChessMove option : options) {
                ChessPosition target = option.getEndPosition();
                if (target.equals(kingLocation)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !anyValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !anyValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(game, chessGame.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, game);
    }
}
