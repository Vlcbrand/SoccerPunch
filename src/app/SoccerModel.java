package app;

import app.entity.Ball;
import app.entity.Field;
import app.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bezit én verwerkt veldspelers, spellogica en punten.
 */
public class SoccerModel
{
    private List<SoccerRemote> remotes;

    private double fps;

    private volatile Map<SoccerConstants, List<Player>> players;
    private volatile Map<SoccerConstants, Integer> scores;

    SoccerModel()
    {
        this.remotes = new ArrayList<>(SoccerController.PLAYERS_SUPPORTED);
        this.players = new TreeMap<>();
        this.scores = new TreeMap<>();

        this.init();
    }

    /**
     * Initialisatie van de model.
     */
    private void init()
    {
        this.resetScores();
    }

    /**
     * Garandeert twee teams en maakt {@value Field#FIELD_PLAYERS_SUPPORTED} nieuwe spelers aan.
     */
    public void createPlayers(Field field)
    {
        this.players.put(SoccerConstants.EAST, new LinkedList<>());
        this.players.put(SoccerConstants.WEST, new LinkedList<>());

        this.players.forEach((side, players) -> {
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
        this.players.clear();
    }

    public boolean existPlayers()
    {
        return this.getPlayerCount() > 0;
    }

    public void update()
    {
        this.updatePlayers();
    }

    public void updatePlayers()
    {
        Random random = new Random();
        for (Player player : this.getPlayers()) {
            final int x = player.getX();
            final int y = player.getY();
            final double[] dxdy = player.getMovement();
            if (player.isControlled())
                player.setPosition((int)(x + dxdy[0]), (int)(y + dxdy[1]));
            else if(random.nextBoolean()){
                player.setPosition((int)(x+Math.random()*3), (int)(y+Math.random()*3));
            }
            else
                player.setPosition((int)(x-Math.random()/2),(int)(y-Math.random()/2));

        }
    }

    public List<Player> getPlayers()
    {
        if (players.size() == 0)
            return null;

        List<Player> allFieldPlayers = new ArrayList<>(Field.FIELD_PLAYERS_SUPPORTED);
        this.players.forEach((side, players) -> allFieldPlayers.addAll(players.stream().collect(Collectors.toList())));

        return allFieldPlayers;
    }

    public List<Player> getPlayers(SoccerConstants side)
    {
        if (players.size() == 0)
            return null;

        return this.players.get(side);
    }

    public int getPlayerCount()
    {
        return this.players.size();
    }

    public void resetScores()
    {
        this.scores.clear();
        this.scores.put(SoccerConstants.WEST, 0);
        this.scores.put(SoccerConstants.EAST, 0);
    }

    public void appendScore(SoccerConstants side, int score)
    {
        this.scores.put(side, this.getScore(side) + score);
    }

    public int getScore(SoccerConstants side)
    {
        return this.scores.get(side);
    }

    public void updateFramesPerSecond(double fps)
    {
        this.fps = fps;
    }

    public int getFramesPerSecond()
    {
        return (int)this.fps;
    }

    public void addRemote(int index, SoccerRemote remote)
    {
        this.remotes.add(index, remote);
    }

    /**
     * Verkrijg een speler door zijn Wiimote-ID.
     */
    public SoccerRemote getRemote(int id)
    {
        if (id < 1 || id > 4)
            return null;

        return this.remotes.get(id - 1);
    }

    public List<SoccerRemote> getRemotes()
    {
        return this.remotes;
    }
}
