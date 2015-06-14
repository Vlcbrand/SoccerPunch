package app;

import app.entity.Player;
import app.wii.WiimoteButton;
import wiiusej.Wiimote;

import java.util.HashSet;
import java.util.Set;

/**
 * Een fysieke speler met een remote.
 * SoccerRemote is een soort wrapper voor {@link Wiimote}.
 */
class SoccerRemote
{
    private final Wiimote mote;
    private final SoccerConstants side;
    private final Set<WiimoteButton> pressedButtons;

    private Player controlledPlayer;

    public SoccerRemote(Wiimote mote, SoccerConstants side)
    {
        this.mote = mote;
        this.side = side;
        this.pressedButtons = new HashSet<>();
    }

    public void controlPlayer(Player player)
    {
        final Player old = this.controlledPlayer;

        if (this.controlledPlayer == null)
            this.controlledPlayer = player;

        if (player != old) {
            this.controlledPlayer = player;
            this.controlledPlayer.setControlled(true);
            this.controlledPlayer.setTitle("P" + mote.getId());

            if (old != null) {
                old.setControlled(false);
                old.setTitle(Player.TITLE_DEFAULT);
            }
        }
    }

    public void pressButton(WiimoteButton button)
    {
        this.pressedButtons.add(button);
    }

    public void releaseButton(WiimoteButton button)
    {
        this.pressedButtons.remove(button);
    }

    public Set<WiimoteButton> getPressedButtons()
    {
        return this.pressedButtons;
    }

    public Player getControlledPlayer()
    {
        return this.controlledPlayer;
    }

    public int getID()
    {
        return this.mote.getId();
    }

    public SoccerConstants getSide()
    {
        return this.side;
    }
}
