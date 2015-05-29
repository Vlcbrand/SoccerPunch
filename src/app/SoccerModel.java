package app;

import app.entity.FieldPlayer;
import app.entity.PhysicalPlayer;
import util.WiimoteButton;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Bezit én verwerkt spellogica.
 */
class SoccerModel
{
    static final int FIELDPLAYERS_SUPPORTED = 8;
    static final int PLAYERS_SUPPORTED = 2;

    private FieldPlayer[] fieldPlayers;
    private PhysicalPlayer[] physicalPlayers;

    SoccerModel()
    {
        this.fieldPlayers = new FieldPlayer[FIELDPLAYERS_SUPPORTED];
        this.physicalPlayers = new PhysicalPlayer[PLAYERS_SUPPORTED];
    }

    public int getNumberOfPhysicalPlayers()
    {
        return this.physicalPlayers.length;
    }

    public int getNumberOfFieldPlayers()
    {
        return this.fieldPlayers.length;
    }

    public FieldPlayer[] getFieldPlayers()
    {
        return this.fieldPlayers;
    }

    public PhysicalPlayer getPhysicalPlayer(int numb)
    {
        return this.physicalPlayers[numb - 1];
    }

    public PhysicalPlayer[] getPhysicalPlayers()
    {
        return this.physicalPlayers;
    }

    public void buttonUpdate(final WiimoteButtonsEvent e)
    {
        final PhysicalPlayer player = this.physicalPlayers[e.getWiimoteId() - 1];
    }

    public void motionUpdate(final MotionSensingEvent e)
    {
        //this.physicalPlayers[e.getWiimoteId() - 1].;
    }

    public void expansionUpdate(final ExpansionEvent e)
    {
        //this.physicalPlayers[e.getWiimoteId() - 1];
    }

    public FieldPlayer chooseNextFieldPlayer(PhysicalPlayer physicalPlayer)
    {
        final int x = physicalPlayer.getControlledPlayer().getX();
        final int y = physicalPlayer.getControlledPlayer().getY();

        FieldPlayer nearestPlayer = null;

        for (FieldPlayer fieldPlayer : fieldPlayers) {
            if (fieldPlayer.equals(physicalPlayer.getControlledPlayer()))
                continue;

            if (nearestPlayer == null && !fieldPlayer.isControlled()) {
                nearestPlayer = fieldPlayer;
                continue;
            }

            if (nearestPlayer == null)
                continue;

            if (Math.min(nearestPlayer.getX(), nearestPlayer.getY()) > Math.min(fieldPlayer.getX(), fieldPlayer.getY()))
                nearestPlayer = fieldPlayer;
        }

        return nearestPlayer;
    }
}
