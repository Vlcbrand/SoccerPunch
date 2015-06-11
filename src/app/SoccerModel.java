package app;

import app.entity.Field;
import app.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Bezit én verwerkt spelers, spellogica en punten.
 */
class SoccerModel
{
    private Map<SoccerConstants, List<Player>> fieldPlayers;

    SoccerModel()
    {
        this.fieldPlayers = new TreeMap<>();
        this.fieldPlayers.put(SoccerConstants.EAST, new LinkedList<>());
        this.fieldPlayers.put(SoccerConstants.WEST, new LinkedList<>());
    }

    public void createNewFieldPlayers(Field field)
    {
        fieldPlayers.forEach((side, players) -> {
            for (int i = 0; i < Field.FIELD_PLAYERS_SUPPORTED/2; i++) {
                final int[] posXY = field.getDefaultPositions(side)[i];
                final Player player = new Player(side);
                player.setPosition(posXY[0], posXY[1]);

                players.add(player);
            }
        });
    }

    public List<Player> getFieldPlayers(SoccerConstants side)
    {
        if (fieldPlayers.size() == 0)
            return null;

        return this.fieldPlayers.get(side);
    }

    public List<Player> getFieldPlayers()
    {
        if (fieldPlayers.size() == 0)
            return null;

        List<Player> players = this.fieldPlayers.get(SoccerConstants.EAST);
        players.addAll(this.fieldPlayers.get(SoccerConstants.WEST));

        return players;
    }
}
