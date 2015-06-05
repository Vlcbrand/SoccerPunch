package app;

import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Een fysieke speler met een remote.
 */
class SoccerPlayer extends WiimoteAdapter
{
    public final Wiimote mote;

    private final Set<WiimoteButton> pressedButtons;

    public SoccerPlayer(Wiimote mote)
    {
        this.mote = mote;
        this.pressedButtons = new HashSet<>();
    }

    public Set<WiimoteButton> getPressedButtons()
    {
        return this.pressedButtons;
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
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
    }
}
