package app;

import app.entity.Player;
import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;

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

        this.isRunning = false;
        this.isPaused = false;
        this.getMotes();

        model.createNewFieldPlayers(view.getInnerField());

        if (motes == null)
            return;

        // Tweede poging tot verbinden.
        if (motes.length == 0)
            this.getMotes();

        this.addMotes();

        this.players[0].controlPlayer(model.getFieldPlayers(SoccerConstants.WEST).get(1));
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
            players[i] = new SoccerPlayer(motes[i], i % 2 == 0 ? SoccerConstants.WEST : SoccerConstants.EAST);
            motes[i].setLeds(i == 0, i == 1, i == 2, i == 3);
            motes[i].addWiiMoteEventListeners(this);
            motes[i].activateMotionSensing();
        }
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

    private Player getNearestFieldPlayer(SoccerPlayer p)
    {
        final Player player = p.getControlledPlayer();
        final List<Player> fieldPlayers = model.getFieldPlayers(p.getSide());
        final Set<WiimoteButton> pressedButtons = p.getPressedButtons();

        int candidates = 0;

        // Tel mogelijke keuzes.
        for (Player temp : fieldPlayers) {
            if (pressedButtons.contains(WiimoteButton.UP))
                if (temp.getY() < player.getY())
                    candidates++;
            else if (pressedButtons.contains(WiimoteButton.DOWN))
                if (temp.getY() > player.getY())
                    candidates++;
            else if (pressedButtons.contains(WiimoteButton.LEFT))
                if (temp.getX() < player.getX())
                    candidates++;
            else if (pressedButtons.contains(WiimoteButton.RIGHT))
                if (temp.getX() > player.getX())
                    candidates++;
        }

        Player nearest = null;
        int candidateXdiff, candidateYdiff;
        int nearestXdiff, nearestYdiff;

        for (Player candidate : fieldPlayers) {
            // Indien geen kandidaten, zelfde terug.
            if (candidates < 1)
                return player;

            if (candidate == player || candidate.isControlled())
                continue;

            if (nearest == null) {
                // Een startwaarde aannemen.
                nearest = candidate;
            } else {
                nearestXdiff = Math.abs(player.getX() - nearest.getX());
                nearestYdiff = Math.abs(player.getY() - nearest.getY());
                candidateXdiff = Math.abs(player.getX() - candidate.getX());
                candidateYdiff = Math.abs(player.getY() - candidate.getY());

                if (pressedButtons.contains(WiimoteButton.UP))
                    if (candidate.getY() < player.getY() && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                if (pressedButtons.contains(WiimoteButton.DOWN))
                    if (candidate.getY() > player.getY() && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                if (pressedButtons.contains(WiimoteButton.LEFT))
                    if (candidate.getX() < player.getX() && candidateXdiff < nearestXdiff)
                        nearest = candidate;
                if (pressedButtons.contains(WiimoteButton.RIGHT))
                    if (candidate.getX() > player.getX() && candidateXdiff < nearestXdiff)
                        nearest = candidate;
            }
        }

        return nearest;
    }

    private boolean isArrowKeyPressed(WiimoteButtonsEvent e)
    {
        return e.isButtonUpPressed() || e.isButtonDownPressed() || e.isButtonLeftPressed() || e.isButtonRightPressed();
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

            sleep(now + MILLIS_PER_LOOP - System.currentTimeMillis());
        }
    }

    /**
     * Geavanceerde lus, waarbij een fps- en update speed grens wordt aangehouden.
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
        final SoccerPlayer player = players[e.getWiimoteId() - 1];
        final Set<WiimoteButton> pressedButtons = player.getPressedButtons();

        // Indien op een pijltjestoets is gedrukt.
        if (this.isArrowKeyPressed(e)) {
            if (e.isButtonUpPressed())
                pressedButtons.add(WiimoteButton.UP);
            else if (e.isButtonUpJustReleased())
                pressedButtons.remove(WiimoteButton.UP);

            if (e.isButtonDownPressed())
                pressedButtons.add(WiimoteButton.DOWN);
            else if (e.isButtonDownJustReleased())
                pressedButtons.remove(WiimoteButton.DOWN);

            if (e.isButtonLeftPressed())
                pressedButtons.add(WiimoteButton.LEFT);
            else if (e.isButtonLeftJustReleased())
                pressedButtons.remove(WiimoteButton.LEFT);

            if (e.isButtonRightPressed())
                pressedButtons.add(WiimoteButton.RIGHT);
            else if (e.isButtonRightJustReleased())
                pressedButtons.remove(WiimoteButton.RIGHT);

            final Player nearest = this.getNearestFieldPlayer(player);
            player.controlPlayer(nearest);
        }
    }
}
