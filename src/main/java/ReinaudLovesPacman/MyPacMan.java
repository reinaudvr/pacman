package ReinaudLovesPacman;

import pacman.controllers.MASController;
import pacman.controllers.PacmanController;
import pacman.controllers.examples.po.POCommGhosts;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.info.GameInfo;
import pacman.game.internal.Ghost;
import pacman.game.internal.Maze;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Reinaud van Rumpt
 */
public class MyPacMan extends PacmanController {

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        Game coGame;
        GameInfo info = game.getPopulatedGameInfo();
        info.fixGhosts((ghost) -> new Ghost(
                ghost,
                game.getCurrentMaze().lairNodeIndex,
                -1,
                -1,
                Constants.MOVE.NEUTRAL
        ));
        coGame = game.getGameFromInfo(info);

        List<Integer> eatThem = Arrays.stream(Constants.GHOST.values())
                .filter(ghost -> game.getGhostLairTime(ghost) >= 0)
                .filter(ghost -> game.isGhostEdible(ghost))
                .map(ghost -> game.getGhostCurrentNodeIndex(ghost))
                .collect(toList());

        return getBestOptionToIndexies(game, eatThem.size() > 0 ? eatThem.stream().mapToInt(i->i).toArray() : game.getActivePillsIndices());
    }

    private Constants.MOVE getBestOptionToIndexies(Game game, int[] indexes) {
        int pacManIndex = game.getPacmanCurrentNodeIndex();
        Map<Integer, Integer> closestPill = new HashMap<>();

        for(Integer pillIndex : indexes) {
            int distance = game.getShortestPathDistance(pacManIndex, pillIndex, game.getPacmanLastMoveMade());
            closestPill.put(pillIndex, distance);
        }

        Integer pillIndex = closestPill.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(integerIntegerEntry -> integerIntegerEntry.getKey())
                .findFirst().orElse(0);

        return game.getNextMoveTowardsTarget(pacManIndex, pillIndex, game.getPacmanLastMoveMade(), Constants.DM.PATH);
    }

}