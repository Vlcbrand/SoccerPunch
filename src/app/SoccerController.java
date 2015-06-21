package app;

import app.entity.Player;
import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import util.Resource;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.*;
import wiiusej.wiiusejevents.wiiuseapievents.WiimoteEvent;

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
            final SoccerConstants side = remote.getSide();
            final List<Player> team = this.model.getPlayers(side);
            final Player ctrlPlayer = team.get(i < 1 ? 1 : 2);

            remote.controlPlayer(ctrlPlayer);
        }

        // Startanimatie stoppen.
        this.view.getStartSequence().deactivate();

        // Achtergrondmuziek afspelen.
        SoccerSound.getInstance().addFile(SoccerSound.MUSIC_MAIN).setVolume(-15).loop();
    }

    /**
     * Clean-up operaties voor het stoppen van het spel.
     */
    private void prepareForStop()
    {
        this.model.removePlayers();

        // Startanimatie hervatten.
        this.view.getStartSequence().activate();

        // Bal op middelpunt plaatsen - tijdelijke oplossing.
        this.view.centerBall();

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

            model.update();
            view.update();
            view.repaint();

            sleep(now - System.currentTimeMillis() + MILLIS_PER_LOOP);
        }
    }

    private Player getNearestFieldPlayer(SoccerRemote remote)
    {
        final Player current = remote.getControlledPlayer();
        final Set<WiimoteButton> pressed = remote.getPressedButtons();
        final List<Player> fieldPlayers = model.getPlayers(remote.getSide());
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

        for (Player candidate : candidatePlayers) {
            if (nearest == null) {
                // Een startwaarde aannemen.
                nearest = candidate;
            } else {
                // Deltawaarden van de huidige dichstbijzijnde veldspeler.
                final int nearestXdiff = Math.abs(current.getX() - nearest.getX());
                final int nearestYdiff = Math.abs(current.getY() - nearest.getY());
                // Deltawaarden van de huidige veldspeler in de loop.
                final int candidateXdiff = Math.abs(current.getX() - candidate.getX());
                final int candidateYdiff = Math.abs(current.getY() - candidate.getY());

                if (pressed.contains(WiimoteButton.UP)) {
                    if (candidateYdiff <= nearestYdiff && candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.DOWN)) {
                    if (candidateYdiff <= nearestYdiff && candidateXdiff < nearestXdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.LEFT)) {
                    if (candidateXdiff <= nearestXdiff && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                } else if (pressed.contains(WiimoteButton.RIGHT)) {
                    if (candidateXdiff <= nearestXdiff && candidateYdiff < nearestYdiff)
                        nearest = candidate;
                }
            }
        }

        // Geluid afspelen.
        SoccerSound.getInstance().addFile(SoccerSound.SOUND_COIN).play();

        System.out.println("Switched to: " + nearest.getX() + ", " + nearest.getY());

        return nearest;
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

    private static void sleep(long millis)
    {
        // Kan niet terug in de tijd.
        if (millis < 0)
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

            // Dichstbijzijnde speler selecteren.
            remote.controlPlayer(this.getNearestFieldPlayer(remote));
        } else {
            if (e.isButtonAJustPressed() && !e.isButtonBPressed())
                this.start();
        }
    }

    public double getJoystickAngle(JoystickEvent e)
    {
        if (e == null)
            return 0;

        //hoek in graden
        this.joystickAngle = e.getAngle();
        return Math.toDegrees(Math.atan(ym/xm));
    }

    @Override public void onExpansionEvent(ExpansionEvent e)
    {
        if (!NunchukEvent.class.isInstance(e))
            return;

        final SoccerRemote remote = this.model.getRemote(e.getWiimoteId());

        // Stoppen, indien speler niet bestaat.
        if (remote == null)
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

            //ball passen
            if (ne.getButtonsEvent().isButtonCJustPressed() && controlledFieldPlayer.getEllipse().contains(view.getBall().getBall()))
                view.getBall().accelerate(45, joystickAngle);

            if (ne.getNunchukMotionSensingEvent().getGforce().getY()*100 > 20 && controlledFieldPlayer.getEllipse().contains(view.getBall().getBall()))
                view.getBall().accelerate((int)(ne.getNunchukMotionSensingEvent().getGforce().getY()*100), joystickAngle);
        }
    }
}
