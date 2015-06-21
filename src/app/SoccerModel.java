package app;

import app.entity.Field;
import app.entity.Player;
import app.wii.WiimoteButton;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bezit én verwerkt veldspelers, spellogica en punten.
 */
public class SoccerModel
{
    private List<SoccerRemote> remotes;

    private double framesPerSecond;

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
     * Wordt periodiek aangeroepen door de controller.
     */
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

            if (player.isControlled()) {
                // Verplaats speler met gegeven afstand.
                player.setPosition((int)(x + dxdy[0]), (int)(y + dxdy[1]));
            } else if (random.nextBoolean()) {
                // Verplaats CPU willekeurig met x- en y-waarden.
                player.setPosition((int)(x + Math.random()*3), (int)(y + Math.random()*3));
            } else {
                // Math.random() geeft snelheidsproblemen, zie javadoc.
                player.setPosition((int)(x - Math.random()/2), (int)(y - Math.random()/2));
            }
        }
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

    public Player getNearestPlayer(SoccerRemote remote)
    {
        final Player current = remote.getControlledPlayer();
        final Set<WiimoteButton> pressed = remote.getPressedButtons();
        final List<Player> fieldPlayers = this.getPlayers(remote.getSide());
        final List<Player> candidatePlayers = new ArrayList<>();

        for (Player temp : fieldPlayers) {
            if (temp == current || temp.isControlled())
                // Sla zelfde- of bezette speler over.
                continue;

            if (pressed.contains(WiimoteButton.UP)) {
                if (temp.getY() < current.getY())
                    candidatePlayers.add(temp);
            } else if (pressed.contains(WiimoteButton.DOWN)) {
                if (temp.getY() > current.getY())
                    candidatePlayers.add(temp);
            } else if (pressed.contains(WiimoteButton.LEFT)) {
                if (temp.getX() < current.getX())
                    candidatePlayers.add(temp);
            } else if (pressed.contains(WiimoteButton.RIGHT)) {
                if (temp.getX() > current.getX())
                    candidatePlayers.add(temp);
            }
        }

        // Stoppen, indien geen keuzemogelijkheden.
        if (candidatePlayers.size() < 1)
            return current;

        Player nearest = null;

        final int thershold = current.getHeight()*2;

        for (Player candidate : candidatePlayers) {
            if (nearest == null) {
                // Startwaarde aannemen.
                nearest = candidate;
            } else {
                // Deltawaarden van de huidige dichstbijzijnde veldspeler.
                final int nearestXdiff = Math.abs(current.getX() - nearest.getX());
                final int nearestYdiff = Math.abs(current.getY() - nearest.getY());
                // Deltawaarden van de huidige te testen veldspeler.
                final int candidateXdiff = Math.abs(current.getX() - candidate.getX());
                final int candidateYdiff = Math.abs(current.getY() - candidate.getY());

                if (pressed.contains(WiimoteButton.UP)) {
                    if (candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.DOWN)) {
                    if (candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.LEFT)) {
                    if (candidateYdiff + thershold < nearestYdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.RIGHT)) {
                    if (candidateYdiff + thershold < nearestYdiff)
                        nearest = candidate;
                }
            }
        }

        // Geluid afspelen na het kiezen van speler.
        SoccerSound.getInstance().addFile(SoccerSound.SOUND_COIN).play();

        return nearest;
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
        this.framesPerSecond = fps;
    }

    public int getFramesPerSecond()
    {
        return (int)this.framesPerSecond;
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
