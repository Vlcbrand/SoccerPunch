package app;

import app.entity.Player;
import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import util.Resource;
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
    static final int FPS, UPS;
    static final boolean SHOW_FPS;

    private SoccerPanel view;
    private SoccerModel model;

    private Wiimote[] motes;
    private SoccerPlayer[] players;

    private volatile Thread runner;
    private boolean isRunning, isPaused;

    static {
        FPS = Resource.getInteger("app.frames_per_second");
        UPS = Resource.getInteger("app.updates_per_second");
        SHOW_FPS = Resource.getBoolean("app.show_fps");
    }

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
            this.view.update();
            this.isRunning = true;
        }
    }

    public void stop()
    {
        this.isRunning = false;

        try {
            this.runner.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

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

    private void loopSimple()
    {
        final int MILLIS_PER_LOOP = 15;

        while (isRunning) {
            long now = System.currentTimeMillis();

            view.repaint();

            sleep(now + MILLIS_PER_LOOP - System.currentTimeMillis());
        }
    }

    private void loop()
    {
        long initialTime = System.nanoTime();
        final double updateTime = SECOND_IN_NANOS/UPS;
        final double renderTime = SECOND_IN_NANOS/FPS;
        double updateTimeDelta = 0, renderTimeDelta = 0;
        int frames = 0, ticks = 0;

        long timer = System.currentTimeMillis();

        while (isRunning) {
            long currentLoopTime = System.nanoTime();
            updateTimeDelta += (currentLoopTime - initialTime)/updateTime;
            renderTimeDelta += (currentLoopTime - initialTime)/renderTime;

            initialTime = currentLoopTime;

            while (!isPaused) {
                if (updateTimeDelta >= 1d) {
                    model.update();

                    ticks++;
                    updateTimeDelta--;
                }

                if (renderTimeDelta >= 1d) {
                    view.repaint();

                    frames++;
                    renderTimeDelta--;
                }

                if (System.currentTimeMillis() - timer > 1000l) {
                    if (SHOW_FPS)
                        System.out.printf("UPDATES: %d, FRAMES: %d", ticks, frames);

                    ticks = 0;
                    frames = 0;
                    timer += 1000l;
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
        if (millis < 1)
            return;

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override public void run()
    {
        this.loopSimple();
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
        final SoccerPlayer player = this.getPlayer(e.getWiimoteId());

        if (player == null)
            return;

        if (e.isButtonAPressed() && !isRunning)
            this.start();

        if (e.isButtonAPressed() && e.isButtonBPressed() && isRunning)
            this.stop();

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

        final SoccerPlayer player = this.getPlayer(e.getWiimoteId());

        // Stoppen, indien speler niet bestaat.
        if (player == null)
            return;

        final NunchukEvent ne = (NunchukEvent)e;
        final JoystickEvent je = ne.getNunchukJoystickEvent();

        final Player fieldPlayer = player.getControlledPlayer();

        if (ne.isThereNunchukJoystickEvent())
            fieldPlayer.setMovement(toPoints(je));
    }
}
