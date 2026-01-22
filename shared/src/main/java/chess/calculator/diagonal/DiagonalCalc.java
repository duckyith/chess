package chess.calculator.diagonal;

import chess.*;

public class DiagonalCalc {

    ChessGame.TeamColor team;
    ChessPiece post;

    public DiagonalCalc(ChessPiece post, ChessGame.TeamColor team){
        this.team = team;
        this.post = post;
    }

    public boolean calc(){
        return post.getTeamColor() != team;
    }
}
