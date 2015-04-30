package irpad;

import wiiusej.test.Tests;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.*;

public class WiiMoteController implements WiimoteListener
{
    private WiiMoteView view = null;
    private WiiMoteModel model = null;

    public WiiMoteController(WiiMoteView view, WiiMoteModel model)
    {
        this.view = view;
        this.model = model;

        Tests.main(null);

        /*
        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, true);

        if (wiimotes != null) {
            Wiimote wiimote = wiimotes[0];
            wiimote.setLeds(true, false, false, false);
            wiimote.activateIRTRacking();
            wiimote.addWiiMoteEventListeners(this);

            // Set IR sensivity.
            wiimote.setIrSensitivity(0);
            wiimote.setIrSensitivity(3);
        }*/

        System.exit(0);
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
    }

    @Override public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent e)
    {
    }

    @Override public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent e)
    {
    }

    @Override public void onDisconnectionEvent(DisconnectionEvent e)
    {
    }

    @Override public void onExpansionEvent(ExpansionEvent e)
    {
    }

    @Override public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent e)
    {
    }

    @Override public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent e)
    {
    }

    @Override public void onIrEvent(IREvent e)
    {
        view.update(e);
    }

    @Override public void onMotionSensingEvent(MotionSensingEvent e)
    {
    }

    @Override public void onNunchukInsertedEvent(NunchukInsertedEvent e)
    {
    }

    @Override public void onNunchukRemovedEvent(NunchukRemovedEvent e)
    {
    }

    @Override public void onStatusEvent(StatusEvent e)
    {
    }
}
