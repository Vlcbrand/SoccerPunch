package app;

import app.entity.Field;
import app.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bezit én verwerkt veldspelers, spellogica en punten.
 */
class SoccerModel
{
    private volatile Map<SoccerConstants, List<Player>> fieldPlayers;

    SoccerModel()
    {
        this.fieldPlayers = new TreeMap<>();
    }

    public void createFieldPlayers(Field field)
    {
        this.fieldPlayers.put(SoccerConstants.EAST, new LinkedList<>());
        this.fieldPlayers.put(SoccerConstants.WEST, new LinkedList<>());

        fieldPlayers.forEach((side, players) -> {
            for (int i = 0; i < Field.FIELD_PLAYERS_SUPPORTED/2; i++) {
                final int[] posXY = field.getDefaultPositions(side)[i];
                final Player player = new Player(side);
                player.setPosition(posXY[0], posXY[1]);

                players.add(player);
            }
        });
    }

    public void removeFieldPlayers()
    {
        fieldPlayers.clear();
    }

    public void updateFieldPlayers()
    {
        for (Player player : this.getFieldPlayers()) {
            final int x = player.getX();
            final int y = player.getY();
            final double[] dxdy = player.getMovement();

            player.setPosition((int)(x + dxdy[0]), (int)(y + dxdy[1]));
        }
    }

    public void update()
    {
        this.updateFieldPlayers();
    }

    public List<Player> getFieldPlayers()
    {
        if (fieldPlayers.size() == 0)
            return null;

        List<Player> allFieldPlayers = new ArrayList<>(8);
        fieldPlayers.forEach((side, players) -> allFieldPlayers.addAll(players.stream().collect(Collectors.toList())));

        return allFieldPlayers;
    }

    public List<Player> getFieldPlayers(SoccerConstants side)
    {
        if (fieldPlayers.size() == 0)
            return null;

        return this.fieldPlayers.get(side);
    }
}
