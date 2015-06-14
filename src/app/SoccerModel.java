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
    private List<SoccerRemote> remotes;
    private volatile Map<SoccerConstants, List<Player>> players;

    SoccerModel()
    {
        this.remotes = new ArrayList<>(SoccerController.PLAYERS_SUPPORTED);
        this.players = new TreeMap<>();
    }

    /**
     * Garandeert twee teams en maakt {@value Field#FIELD_PLAYERS_SUPPORTED} nieuwe spelers aan.
     */
    public void createPlayers(Field field)
    {
        this.players.put(SoccerConstants.EAST, new LinkedList<>());
        this.players.put(SoccerConstants.WEST, new LinkedList<>());

        players.forEach((side, players) -> {
            for (int i = 0; i < Field.FIELD_PLAYERS_SUPPORTED/2; i++) {
                final int[] posXY = field.getDefaultPositions(side)[i];
                final Player player = new Player(side);
                player.setPosition(posXY[0], posXY[1]);

                players.add(player);
            }
        });
    }

    /**
     * Leegt de collectie bestemd voor {@link Player spelers}.
     */
    public void removePlayers()
    {
        players.clear();
    }

    public void update()
    {
        this.updatePlayers();
    }

    public void updatePlayers()
    {
        for (Player player : this.getPlayers()) {
            final int x = player.getX();
            final int y = player.getY();
            final double[] dxdy = player.getMovement();

            player.setPosition((int)(x + dxdy[0]), (int)(y + dxdy[1]));
        }
    }

    public List<Player> getPlayers()
    {
        if (players.size() == 0)
            return null;

        List<Player> allFieldPlayers = new ArrayList<>(Field.FIELD_PLAYERS_SUPPORTED);
        players.forEach((side, players) -> allFieldPlayers.addAll(players.stream().collect(Collectors.toList())));

        return allFieldPlayers;
    }

    public List<Player> getPlayers(SoccerConstants side)
    {
        if (players.size() == 0)
            return null;

        return this.players.get(side);
    }

    /**
     * Verkrijg een speler door zijn Wiimote-ID.
     */
    public SoccerRemote getRemote(int id)
    {
        if (id < 1 || id > 4)
            return null;

        return this.remotes.get(id -1);
    }

    public void addRemote(int index, SoccerRemote remote)
    {
        this.remotes.add(index, remote);
    }

    public List<SoccerRemote> getRemotes()
    {
        return this.remotes;
    }
}
