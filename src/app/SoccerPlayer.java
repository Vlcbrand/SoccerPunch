package app;

import app.entity.Player;
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

    private final SoccerConstants side;
    private final Set<WiimoteButton> pressedButtons;

    private Player controlledPlayer;

    public SoccerPlayer(Wiimote mote, SoccerConstants side)
    {
        this.mote = mote;
        this.side = side;
        this.pressedButtons = new HashSet<>();
    }

    public void controlPlayer(Player player)
    {
        // Reset de speler.
        if (this.controlledPlayer != null) {
            this.controlledPlayer.setState(false);
            this.controlledPlayer.setTitle("CPU");
        }

        this.controlledPlayer = player;
        this.controlledPlayer.setState(true);
        this.controlledPlayer.setTitle("P" + mote.getId());
    }

    public Player getControlledPlayer()
    {
        return this.controlledPlayer;
    }

    public SoccerConstants getSide()
    {
        return this.side;
    }

    public Set<WiimoteButton> getPressedButtons()
    {
        return this.pressedButtons;
    }
}
