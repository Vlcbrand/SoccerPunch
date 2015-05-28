package app;

import util.WiimoteAdapter;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.NunchukEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukInsertedEvent;
import wiiusej.wiiusejevents.wiiuseapievents.NunchukRemovedEvent;

/**
 * Bezit over een {@link SoccerFrame} en een {@link SoccerModel}.
 * Deze controller vangt events op van de Wiimotes en verwerkt deze.
 */
class SoccerController extends WiimoteAdapter implements Runnable
{
    protected static final int PLAYERS = 1;

    protected SoccerPanel view;
    protected SoccerModel model;

    private Wiimote[] motes;
    private boolean isRunning;

    SoccerController(SoccerPanel view, SoccerModel model)
    {
        this.view = view;
        this.model = model;

        this.isRunning = false;

        this.getMotes(PLAYERS);

        if (motes == null)
            return;

        // Tweede poging tot verbinden.
        if (motes.length == 0)
            this.getMotes(PLAYERS);
    }

    public void start()
    {
        this.isRunning = true;

    }

    /**
     * Verkrijg alle Wiimotes in de directe omgeving.
     * @param amount hoeveelheid
     */
    private void getMotes(int amount)
    {
        this.motes = WiiUseApiManager.getWiimotes(amount, false);
    }

    /**
     * Voeg een Wiimote toe voor gebruik.
     * @return de toegevoegde Wiimote
     */
    private Wiimote addMote(int index)
    {
        if (motes.length < 0 || index > motes.length)
            return null;

        for (int i = 0; i < motes.length; i++) {
            motes[i].setLeds(i == 0, i == 1, i == 2, i == 3);
            motes[i].addWiiMoteEventListeners(this);
            motes[i].activateMotionSensing();
        }

        return motes[index];
    }

    @Override public void run()
    {
        while (isRunning) {
            // TODO: renderen.
        }
    }

    @Override public void onMotionSensingEvent(MotionSensingEvent e)
    {
        model.update(e);
    }

    @Override public void onNunchukInsertedEvent(NunchukInsertedEvent e)
    {
        System.out.println("Nunchuk inserted...");
    }

    @Override public void onNunchukRemovedEvent(NunchukRemovedEvent e)
    {
        System.out.println("Nunchuck removed...");
    }

    @Override public void onExpansionEvent(ExpansionEvent e)
    {
        if (!NunchukEvent.class.isInstance(e))
            return;

        model.expansionUpdate(e);
    }
}
