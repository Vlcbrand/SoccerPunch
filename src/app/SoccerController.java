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

    private Wiimote[] wiimotes;

    private volatile Thread runner;
    private boolean isRunning, isPaused;

    private static double xm, ym;
    private double joystickAngle;

    static {
        FPS = Resource.getInteger("int.frames_per_second");
        UPS = Resource.getInteger("int.updates_per_second");
        SHOW_FPS = Resource.getBoolean("bool.show_fps");
    }

    SoccerController(SoccerPanel view, SoccerModel model)
    {
        this.view = view;
        this.model = model;

        this.runner = null;
        this.isRunning = false;
        this.isPaused = false;

        this.getWiimotes();

        if (wiimotes == null)
            return;

        // Tweede poging tot verbinden.
        if (wiimotes.length == 0)
            this.getWiimotes();

        this.addAvailableWiimotes();
    }

    /**
     * Verkrijg alle Wiimotes in de directe omgeving.
     */
    private void getWiimotes()
    {
        this.wiimotes = WiiUseApiManager.getWiimotes(PLAYERS_SUPPORTED, false);
    }

    /**
     * Voeg de gevonden Wiimotes toe voor gebruik.
     */
    private void addAvailableWiimotes()
    {
        final int connectedWiimotes = this.wiimotes.length;

        if (connectedWiimotes < 1)
            return;

        // Wiimotes en SoccerRemote op gelijke index plaatsen.
        for (int i = 0; i < connectedWiimotes; i++) {
            // Wiimote voorbereiden.
            final Wiimote wiimote = wiimotes[i];
            wiimote.addWiiMoteEventListeners(this);
            wiimote.activateMotionSensing();
            wiimote.setLeds(i == 0, i == 1, i == 2, i == 3);

            // Wiimote koppelen aan SoccerRemote.
            this.model.addRemote(i, new SoccerRemote(wiimote, i%2 == 0 ? SoccerConstants.WEST : SoccerConstants.EAST));
        }
    }

    public void togglePause()
    {
        this.isPaused = !this.isPaused;
    }

    /**
     * Alle nodige operaties voor het starten van het spel.
     */
    private void prepareForStart()
    {
        final List<SoccerRemote> remotes = this.model.getRemotes();

        // Alle veldspelers aanmaken.
        this.model.createPlayers(view.getInnerField());

        // Elke controller één veldspeler laten besturen.
        for (int i = 0; i < remotes.size(); i++) {
            final SoccerRemote remote = remotes.get(i);
            final List<Player> team = this.model.getPlayers(remote.getSide());
            final Player ctrlPlayer = team.get(i < 1 ? 1 : 2);

            remote.controlPlayer(ctrlPlayer);
        }

        // Startanimatie stoppen.
        this.view.getStartSequence().deactivate();

        // Achtergrondmuziek afspelen.
        SoccerSound.getInstance().addFile(SoccerSound.MUSIC_MAIN).setVolume(-15).loop();
    }

    /**
     * Clean-up operaties vóór het stoppen van het spel.
     */
    private void prepareForStop()
    {
        this.model.removePlayers();

        // Score herstellen.
        this.model.resetScores();

        // Bal op middelpunt plaatsen - tijdelijke oplossing.
        this.view.centerBall();

        // Startanimatie hervatten.
        this.view.getStartSequence().activate();

        // Achtergrondmuziek stoppen.
        SoccerSound.getInstance().addFile(SoccerSound.MUSIC_MAIN).stop();
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

    private void processInput()
    {
        for (SoccerRemote remote : this.model.getRemotes()) {
            final Set<WiimoteButton> pressedButtons = remote.getPressedButtons();

            // Indien geen knoppen ingedrukt, volgende remote nagaan.
            if (pressedButtons.isEmpty())
                continue;

            // Wisselen van speler - indien nodig.
            remote.controlPlayer(this.model.getNearestPlayer(remote));

            // Knoppen afgehandeld, laat los.
            remote.releaseButtons();
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
            final long currentLoopTime = System.nanoTime();
            updateTimeDelta += (currentLoopTime - initialTime)/updateTime;
            renderTimeDelta += (currentLoopTime - initialTime)/renderTime;

            initialTime = currentLoopTime;

            while (!isPaused) {
                if (updateTimeDelta >= 1) {
                    model.update();

                    ticks++;
                    updateTimeDelta--;
                }

                if (renderTimeDelta >= 1) {
                    view.repaint();

                    frames++;
                    renderTimeDelta--;
                }

                if (System.currentTimeMillis() - timer > 1000) {
                    if (SHOW_FPS) {
                        //final double ups = ticks/updateTimeDelta;
                        final double fps = frames/renderTimeDelta;
                        this.model.updateFramesPerSecond(fps);
                    }

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

            this.processInput();
            this.model.update();
            this.view.update();
            this.view.repaint();

            sleep(now - System.currentTimeMillis() + MILLIS_PER_LOOP);
        }
    }

    /**
     * Verkrijg de x- en y-waarde van een joystick.
     */
    public static double[] toPoints(JoystickEvent e)
    {
        if (e == null)
            return new double[] {0, 0};

        xm = Math.sin(e.getAngle()*Math.PI/180d)*e.getMagnitude();
        ym = -Math.cos(e.getAngle()*Math.PI/180d)*e.getMagnitude();

        return new double[] {xm, ym};
    }

    public double getJoystickAngle(JoystickEvent e)
    {
        if (e == null)
            return 0;

        this.joystickAngle = e.getAngle();
        return Math.toDegrees(Math.atan(ym/xm));
    }

    private static void sleep(long millis)
    {
        if (millis < 0)
            // Kan niet terug in de tijd, stoppen.
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
        final SoccerRemote remote = this.model.getRemote(e.getWiimoteId());

        if (isRunning) {
            // Indien spel draait, knoppen doorgeven aan SoccerRemotes.

            if (e.isButtonAJustPressed() && e.isButtonBHeld()) {
                this.stop();
                return;
            }

            if (e.isButtonUpJustPressed())
                remote.pressButton(WiimoteButton.UP);
            else if (e.isButtonUpJustReleased())
                remote.releaseButton(WiimoteButton.UP);

            if (e.isButtonDownJustPressed())
                remote.pressButton(WiimoteButton.DOWN);
            else if (e.isButtonDownJustReleased())
                remote.releaseButton(WiimoteButton.DOWN);

            if (e.isButtonLeftJustPressed())
                remote.pressButton(WiimoteButton.LEFT);
            else if (e.isButtonLeftJustReleased())
                remote.releaseButton(WiimoteButton.LEFT);

            if (e.isButtonRightJustPressed())
                remote.pressButton(WiimoteButton.RIGHT);
            else if (e.isButtonRightJustReleased())
                remote.releaseButton(WiimoteButton.RIGHT);
        } else {
            // Spel starten indien B wordt vastgehouden en A wordt ingedrukt.
            if (e.isButtonAJustPressed() && !e.isButtonBPressed())
                this.start();
        }
    }

    @Override public void onExpansionEvent(ExpansionEvent e)
    {
        if (!NunchukEvent.class.isInstance(e))
            return;

        final SoccerRemote remote = this.model.getRemote(e.getWiimoteId());

        if (remote == null)
            // Indien speler niet bestaat, stoppen.
            return;

        final NunchukEvent ne = (NunchukEvent)e;
        final JoystickEvent je = ne.getNunchukJoystickEvent();
        final Player controlledFieldPlayer = remote.getControlledPlayer();

        // Stoppen, indien nog geen veldspelers zijn toegewezen.
        if (!this.model.existPlayers())
            return;

        if (ne.isThereNunchukJoystickEvent()) {
            controlledFieldPlayer.setMovement(toPoints(je));
            controlledFieldPlayer.setAngle(getJoystickAngle(je));

            // Bal pasen met de c-knop van de nunchuk.
            if (ne.getButtonsEvent().isButtonCJustPressed() && controlledFieldPlayer.getEllipse().contains(view.getBall().getBall()))
                view.getBall().accelerate(45, this.joystickAngle);

            // Bal pasen met nunchukbeweging.
            if (ne.getNunchukMotionSensingEvent().getGforce().getY()*100 > 20 && controlledFieldPlayer.getEllipse().contains(view.getBall().getBall()))
                view.getBall().accelerate((int)(ne.getNunchukMotionSensingEvent().getGforce().getY()*100), this.joystickAngle);
        }
    }
}
