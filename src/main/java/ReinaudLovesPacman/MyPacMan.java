package ReinaudLovesPacman;

import ReinaudLovesPacman.resource.NextStep;
import pacman.controllers.PacmanController;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import sun.security.krb5.internal.crypto.CksumType;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Reinaud van Rumpt
 */
public class MyPacMan extends PacmanController {

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        List<Integer> eatThem = Arrays.stream(Constants.GHOST.values())
                .filter(ghost -> Optional.ofNullable(game.isGhostEdible(ghost)).orElse(false))
                .map(ghost -> game.getGhostCurrentNodeIndex(ghost))
                .collect(toList());

        return getBestOptionToIndexies(game, ghostsOrPill(game, eatThem));
    }

    private int[] ghostsOrPill(Game game, List<Integer> eatThem) {
        if(eatThem.size() >= 4) {
            System.out.println("Ghosts --> ");
            return eatThem.stream().mapToInt(i->i).toArray();
        } else {
            System.out.println("Pill --> ");
            return game.getActivePillsIndices();
        }
    }

    private Constants.MOVE getBestOptionToIndexies(Game game, int[] indexes) {
        int pacManIndex = game.getPacmanCurrentNodeIndex();
        List<NextStep> closestIndex = new ArrayList<>();

        for(Integer index : indexes) {
            int distance = game.getShortestPathDistance(pacManIndex, index);
            Constants.MOVE move = game.getNextMoveTowardsTarget(pacManIndex, index, Constants.DM.PATH);
            int neighbourIndex = game.getNeighbour(pacManIndex, move);
            double score = distance + ghostScore(game, neighbourIndex);
            if(score > 100) {
                move = game.getNextMoveAwayFromTarget(pacManIndex, neighbourIndex, Constants.DM.PATH);
            }
            closestIndex.add(new NextStep(index, move, neighbourIndex, score));
        }

       return closestIndex.stream()
                .sorted(Comparator.comparing(NextStep::getScore))
                .findFirst()
                .map(NextStep::getFirstMove)
                .orElseGet(() -> getClosetsJunction(game, pacManIndex));
    }

    private Constants.MOVE getClosetsJunction(Game game, int pacManIndex) {

        List<NextStep> closestIndex = new ArrayList<>();

        for(Integer index : game.getJunctionIndices()) {
            if(pacManIndex != index) {
                double distance = game.getShortestPathDistance(pacManIndex, index, game.getPacmanLastMoveMade());
                Constants.MOVE move = game.getNextMoveTowardsTarget(pacManIndex, index, Constants.DM.PATH);
                int neighbourIndex = game.getNeighbour(pacManIndex, move);
                closestIndex.add(new NextStep(index, move, neighbourIndex, distance + ghostScore(game, neighbourIndex)));
            }
        }

        return closestIndex.stream()
                .sorted(Comparator.comparing(NextStep::getScore))
                .map(NextStep::getFirstMove)
                .findFirst().orElse(Constants.MOVE.NEUTRAL);

    }

    private Double ghostScore(Game game, int index) {
        Set<Double> ghostScores = Arrays.stream(Constants.GHOST.values())
                .filter(ghost -> !Optional.ofNullable(game.isGhostEdible(ghost)).orElse(false))
                .map(ghost -> game.getGhostCurrentNodeIndex(ghost))
                .filter(ghostIndex -> ghostIndex > 0)
                .map(ghostIndex -> game.getDistance(index, ghostIndex, Constants.DM.MANHATTAN))
                .collect(Collectors.toSet());

        double score = 0;
        for (double ghostScore: ghostScores) {
            if(ghostScore < 30) {
                score += 100;
            }
            score -= ghostScore;
        }

        return score;
    }

}