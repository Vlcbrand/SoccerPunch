package app;

import app.entity.Field;
import app.entity.Player;

import javax.swing.*;
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
    }

    public void createNewFieldPlayers(Field field)
    {
        fieldPlayers.clear();

        fieldPlayers.forEach((side, players) -> {
            for (int i = 0; i < Field.FIELD_PLAYERS_SUPPORTED/2; i++) {
                final Player player = new Player(side);
                final int[] posXY = field.getDefaultPositions(side)[i];
                player.setPosition(posXY[0], posXY[1]);
                players.add(player);
            }
        });
    }

    public Player[] getFieldPlayers()
    {
        if (fieldPlayers.size() == 0)
            return null;

        final ArrayList<Player> list = new ArrayList<>(this.fieldPlayers.get(SoccerConstants.WEST));
        list.addAll(this.fieldPlayers.get(SoccerConstants.EAST));
        return (Player[])list.toArray();
    }

    public Player[] getFieldPlayers(WindowConstants side)
    {
        return (Player[])this.fieldPlayers.get(side).toArray();
    }
}
