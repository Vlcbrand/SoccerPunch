package app;

import app.entity.Player;
import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import com.sun.istack.internal.NotNull;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.JoystickEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Bezit over een {@link SoccerFrame} en een {@link SoccerModel}.
 */
class SoccerController extends WiimoteAdapter implements Runnable
{
    static final int SECOND_IN_NANOS = 1000000000;
    static final int PLAYERS_SUPPORTED = 2;

    private SoccerPanel view;
    private SoccerModel model;

    private Wiimote[] motes;
    private SoccerPlayer[] players;

    private Thread runner;
    private boolean isRunning, isPaused;
    private int frames, FPS;

    SoccerController(SoccerPanel view, SoccerModel model)
    {
        this.view = view;
        this.model = model;
        model.createNewFieldPlayers(view.getInnerField());

        this.isRunning = false;
        this.isPaused = false;
        this.getMotes();

        if (motes == null)
            return;

        // Tweede poging tot verbinden.
        if (motes.length == 0)
            this.getMotes();

        this.addMotes();

        this.start();
    }

    /**
     * Verkrijg alle Wiimotes in de directe omgeving.
     */
    private void getMotes()
    {
        this.motes = WiiUseApiManager.getWiimotes(PLAYERS_SUPPORTED, false);
    }

    /**
     * Voeg de gevonden Wiimotes toe voor gebruik.
     */
    private void addMotes()
    {
        final int connectedMotes = motes.length;

        if (connectedMotes <= 0)
            return;

        // Voor elke veronden Wiimote een speler.
        players = new SoccerPlayer[connectedMotes];

        // Wiimote bruikbaar maken en koppelen aan speler.
        for (int i = 0; i < connectedMotes; i++) {
            // Speler begint met het besturen van de spits, index = 1.
            players[i] = new SoccerPlayer(motes[i], i % 2 == 0 ? SoccerConstants.WEST : SoccerConstants.EAST);
            players[i].controlPlayer(this.model.getFieldPlayers(players[i].getSide()).get(1));

            final Wiimote mote = motes[i];
            mote.setLeds(i == 0, i == 1, i == 2, i == 3);
            mote.addWiiMoteEventListeners(this);
            mote.activateMotionSensing();
        }
    }

    /**
     * Verkrijg een speler door zijn Wiimote-ID.
     */
    private SoccerPlayer getPlayer(int id)
    {
        if (id < 1 || id > 4)
            return null;

        return this.players[id - 1];
    }

    public void start()
    {
        if (runner == null && !isRunning) {
            this.runner = new Thread(this);
            this.runner.start();
            this.isRunning = true;
        }
    }

    public void stop()
    {
        this.isRunning = false;
        this.runner = null;
    }

    public void pause()
    {
        this.isPaused = true;
    }

    public void unpause()
    {
        this.isPaused = false;
    }

    /**
     * Eenvoudige lus, waarbij enkel wordt voorkomen dat het spel te snel loopt.
     */
    private void simpleGameLoop()
    {
        final int MILLIS_PER_LOOP = 15;

        model.createNewFieldPlayers(view.getInnerField());

        while (isRunning) {
            long now = System.currentTimeMillis();

            view.repaint();

            final long sleepTime = now + MILLIS_PER_LOOP - System.currentTimeMillis();
            sleep(sleepTime < 0 ? 0 : sleepTime);
        }
    }

    /**
     * Geavanceerde lus, waarbij een fps- en een update speed grens wordt aangehouden.
     */
    private void advancedGameLoop()
    {
        final double HERTZ = 30;
        final double MAX_FPS = 30;
        final double UPDATETIME_IN_NANOS = SECOND_IN_NANOS/HERTZ;
        final double RENDERTIME_IN_NANOS = SECOND_IN_NANOS/MAX_FPS;
        final int MAX_UPDATES = 1;

        double lastRenderTime;
        double lastUpdateTime = System.nanoTime();
        int lastUpdateTimeInSeconds = (int)(lastUpdateTime/SECOND_IN_NANOS);

        while (isRunning) {
            long now = System.nanoTime();
            int updates = 0;

            if (!isPaused) {
                // Updaten zolang nodig is.
                while (now - lastUpdateTime > UPDATETIME_IN_NANOS && updates < MAX_UPDATES) {
                    // TODO: berekeningen zullen hier plaatsvinden.
                    lastUpdateTime += UPDATETIME_IN_NANOS;
                    updates++;
                }

                // Indien een motionUpdate te lang heeft geduurd, deze niet volledig bijhouden.
                if (now - lastUpdateTime > UPDATETIME_IN_NANOS)
                    lastUpdateTime = now - UPDATETIME_IN_NANOS;

                // Bereken interpolation.
                final float interp = Math.min(1f, (float)((now - lastUpdateTime)/UPDATETIME_IN_NANOS));

                // Teken de view.
                view.repaint();
                lastRenderTime = now;

                // Bereken de frames.
                final int nowInSeconds = (int)(lastUpdateTime/SECOND_IN_NANOS);
                if (nowInSeconds > lastUpdateTimeInSeconds) {
                    System.out.printf("FRAMES %1$d, FPS %2$d\n", frames, FPS);

                    FPS = frames;
                    frames = 0;

                    lastUpdateTimeInSeconds = nowInSeconds;
                }

                // Maximum motionUpdate- en rendertijden hanteren.
                while (now - lastRenderTime < RENDERTIME_IN_NANOS && now - lastUpdateTime < UPDATETIME_IN_NANOS) {
                    // Andere processen de kans geven om CPU tijd te nemen.
                    Thread.yield();

                    // Stopt de applicatie van het overnemen van de CPU.
                    sleep(1);

                    now = System.nanoTime();
                }
            }
        }
    }

    private Player getNearestFieldPlayer(SoccerPlayer p)
    {
        final Player current = p.getControlledPlayer();
        final Set<WiimoteButton> pressed = p.getPressedButtons();
        final List<Player> fieldPlayers = model.getFieldPlayers(p.getSide());
        final List<Player> candidatePlayers = new ArrayList<>();

        // Tel mogelijke keuzes.
        for (Player temp : fieldPlayers) {
            if (temp == current || temp.isControlled())
                // Skip zelfde speler of bezette spelers.
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
        int candidateXdiff, candidateYdiff;
        int nearestXdiff, nearestYdiff;

        for (Player candidate : candidatePlayers) {
            if (nearest == null) {
                // Een startwaarde aannemen.
                nearest = candidate;
            } else {
                // Deltawaarden van de huidige dichstbijzijnde veldspeler.
                nearestXdiff = Math.abs(current.getX() - nearest.getX());
                nearestYdiff = Math.abs(current.getY() - nearest.getY());
                // Deltawaarden van de huidige veldspeler in de loop.
                candidateXdiff = Math.abs(current.getX() - candidate.getX());
                candidateYdiff = Math.abs(current.getY() - candidate.getY());

                if (pressed.contains(WiimoteButton.UP)) {
                    if (candidate.getY() < current.getY() && candidateYdiff < nearestYdiff && candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.DOWN)) {
                    if (candidate.getY() > current.getY() && candidateYdiff < nearestYdiff && candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.LEFT)) {
                    if (candidate.getX() < current.getX() && candidateXdiff < nearestXdiff && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.RIGHT)) {
                    if (candidate.getX() > current.getX() && candidateXdiff < nearestXdiff && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                }
            }
        }

        return nearest;
    }

    /**
     * Verkrijg de x- en y-waarde van een joystick.
     */
    public static double[] toPoints(JoystickEvent e)
    {
        if (e == null)
            return new double[] {0d, 0d};

        return new double[] {
            Math.sin(e.getAngle() * Math.PI/180d) * e.getMagnitude(), // x-waarde.
            -Math.cos(e.getAngle() * Math.PI/180d) * e.getMagnitude() // y-waarde.
        };
    }

    private static void sleep(long millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override public void run()
    {
        this.simpleGameLoop();
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
        final SoccerPlayer player = this.getPlayer(e.getWiimoteId());

        if (player == null)
            return;

        if (e.isButtonUpJustPressed())
            player.pressButton(WiimoteButton.UP);
        else if (e.isButtonUpJustReleased())
            player.releaseButton(WiimoteButton.UP);

        if (e.isButtonDownJustPressed())
            player.pressButton(WiimoteButton.DOWN);
        else if (e.isButtonDownJustReleased())
            player.releaseButton(WiimoteButton.DOWN);

        if (e.isButtonLeftPressed())
            player.pressButton(WiimoteButton.LEFT);
        else if (e.isButtonLeftJustReleased())
            player.releaseButton(WiimoteButton.LEFT);

        if (e.isButtonRightPressed())
            player.pressButton(WiimoteButton.RIGHT);
        else if (e.isButtonRightJustReleased())
            player.releaseButton(WiimoteButton.RIGHT);

        // Dichstbijzijnde speler selecteren.
        player.controlPlayer(this.getNearestFieldPlayer(player));
    }

    @Override public void onExpansionEvent(ExpansionEvent e)
    {
        if (!NunchukEvent.class.isInstance(e))
            return;

        final NunchukEvent ne = (NunchukEvent)e;
        final JoystickEvent je = ((NunchukEvent)e).getNunchukJoystickEvent();
        final SoccerPlayer player = this.getPlayer(e.getWiimoteId());

        if (player == null)
            return;

        final Player fieldPlayer = player.getControlledPlayer();

        if (ne.isThereNunchukJoystickEvent()) {
            final double[] points = toPoints(je);
            fieldPlayer.setMovement(points);
        }
    }
}
