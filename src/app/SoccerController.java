package app;

import app.wii.WiimoteAdapter;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;

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
        //this.getMotes();

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
     * Voeg een Wiimote toe voor gebruik.
     * @return de toegevoegde Wiimote
     */
    private void addMotes()
    {
        final int connectedMotes = motes.length;

        if (connectedMotes <= 0)
            return;

        // Voor elke veronden Wiimote een speler.
        players = new SoccerPlayer[connectedMotes];

        // Speler 1 kan het spel starten.
        motes[0].addWiiMoteEventListeners(this);

        // Wiimote bruikbaar maken en koppelen aan speler.
        for (int i = 0; i < connectedMotes; i++) {
            players[i] = new SoccerPlayer(motes[i]);
            motes[i].setLeds(i == 0, i == 1, i == 2, i == 3);
            motes[i].addWiiMoteEventListeners(players[i]);
            motes[i].activateMotionSensing();
        }
    }

    public void start()
    {
        if (runner == null) {
            this.runner = new Thread(this);
            this.runner.start();
            this.isRunning = true;
        }
    }

    public void stop()
    {
        this.isRunning = false;
        this.runner.interrupt();
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
        if (!isRunning && e.isButtonAPressed())
            this.start();
        else if (isRunning && e.isButtonAPressed() && e.isButtonBPressed())
            this.stop();
    }
}
