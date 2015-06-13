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

        this.runner = null;
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
            final Wiimote mote = motes[i];
            players[i] = new SoccerPlayer(mote, i % 2 == 0 ? SoccerConstants.WEST : SoccerConstants.EAST);
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

    /**
     * Alle nodige operaties voor het starten van het spel.
     */
    private void prepareForStart()
    {
        // Alle veldspelers aanmaken.
        this.model.createFieldPlayers(view.getInnerField());

        // Elke controller één veldspeler laten besturen.
        for (int i = 0; i < players.length; i++) {
            final SoccerConstants side = this.players[i].getSide();
            final List<Player> team = this.model.getFieldPlayers(side);
            final Player ctrlPlayer = team.get(i < 1 ? 1 : 2);

            this.players[i].controlPlayer(ctrlPlayer);
        }
    }

    /**
     * Clean-up operaties voor het stoppen van het spel.
     */
    private void prepareForStop()
    {
        this.model.removeFieldPlayers();
    }

    public void start()
    {
        if (this.runner == null) {
            this.isRunning = true;
            this.runner = new Thread(this);

            this.prepareForStart();

            this.view.update();
            this.view.repaint();

            this.runner.start();
        }
    }

    public void stop()
    {
        if (this.runner != null) {
            this.isRunning = false;

            this.prepareForStop();

            try {
                this.runner.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            this.runner = null;

            this.view.update();
            this.view.repaint();
        }
    }

    public void togglePause()
    {
        this.isPaused = !this.isPaused;
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
                        System.out.printf("\nUPDATES: %f, FRAMES: %f", ticks / updateTimeDelta, frames / renderTimeDelta);

                    ticks = 0;
                    frames = 0;
                    timer += 1000l;
                }
            }
        }
    }

    private void loopSimple()
    {
        final int MILLIS_PER_LOOP = 15;

        while (isRunning) {
            long now = System.currentTimeMillis();

            model.update();
            view.repaint();

            sleep(now - System.currentTimeMillis() + MILLIS_PER_LOOP);
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
        // Kan niet terug in de tijd.
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

        if (isRunning) {
            if (e.isButtonAJustPressed() && e.isButtonBHeld()) {
                this.stop();
                return;
            }

            if (e.isButtonHomePressed())
                // Indien op HOME is gedrukt.
                this.togglePause();

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
        } else {
            if (e.isButtonAJustPressed() && !e.isButtonBPressed())
                this.start();
        }
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

        final Player controlledFieldPlayer = player.getControlledPlayer();

        // Stoppen, indien nog geen veldspelers zijn toegewezen.
        if (controlledFieldPlayer == null)
            return;

        if (ne.isThereNunchukJoystickEvent())
            controlledFieldPlayer.setMovement(toPoints(je));
    }
}
