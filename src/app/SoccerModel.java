package app;

import app.entity.Field;
import app.entity.Player;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Bezit én verwerkt spellogica.
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
                final Player player = new Player(side);
                final int[] posXY = field.getDefaultPositions(side)[i];
                player.setPosition(posXY[0], posXY[1]);
                players.add(player);
            }
        });
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
