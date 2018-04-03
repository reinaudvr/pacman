package ReinaudLovesPacman.resource;

import pacman.game.Constants;

public class NextStep {
    Integer goalIndex;
    Constants.MOVE firstMove;
    Integer neighbourIndex;
    Double score;

    public NextStep(Integer goalIndex, Constants.MOVE firstMove, Integer neighbourIndex, Double score) {
        this.goalIndex = goalIndex;
        this.firstMove = firstMove;
        this.neighbourIndex = neighbourIndex;
        this.score = score;
    }

    public Integer getGoalIndex() {
        return goalIndex;
    }

    public Constants.MOVE getFirstMove() {
        return firstMove;
    }

    public Integer getNeighbourIndex() {
        return neighbourIndex;
    }

    public Double getScore() {
        return score;
    }
}
