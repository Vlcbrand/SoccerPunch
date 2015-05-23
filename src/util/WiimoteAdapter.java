package util;

import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.*;

public class WiimoteAdapter implements WiimoteListener
{
    @Override public void onButtonsEvent(WiimoteButtonsEvent wiimoteButtonsEvent)
    {
    }

    @Override public void onIrEvent(IREvent irEvent)
    {
    }

    @Override public void onMotionSensingEvent(MotionSensingEvent motionSensingEvent)
    {
    }

    @Override public void onExpansionEvent(ExpansionEvent expansionEvent)
    {
    }

    @Override public void onStatusEvent(StatusEvent statusEvent)
    {
    }

    @Override public void onDisconnectionEvent(DisconnectionEvent disconnectionEvent)
    {
    }

    @Override public void onNunchukInsertedEvent(NunchukInsertedEvent nunchukInsertedEvent)
    {
    }

    @Override public void onNunchukRemovedEvent(NunchukRemovedEvent nunchukRemovedEvent)
    {
    }

    @Override public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent guitarHeroInsertedEvent)
    {
    }

    @Override public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent guitarHeroRemovedEvent)
    {
    }

    @Override public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent classicControllerInsertedEvent)
    {
    }

    @Override public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent classicControllerRemovedEvent)
    {
    }
}
