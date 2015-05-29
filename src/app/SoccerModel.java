package app;

import app.entity.PhysicalPlayer;
import wiiusej.wiiusejevents.physicalevents.ButtonsEvent;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;

/**
 * Bezit én verwerkt spellogica.
 */
class SoccerModel
{
    static final int PLAYERS = 2;

    PhysicalPlayer[] players;

    SoccerModel()
    {
        this.players = new PhysicalPlayer[PLAYERS];

        for (int i = 0; i < PLAYERS; i++)
            this.players[i] = new PhysicalPlayer(300 + i*50, 300 + i*50);
    }

    public int getNumberOfPlayers()
    {
        return this.players.length;
    }

    public void update(MotionSensingEvent e)
    {
        //this.players[e.getWiimoteId()].update(e);
    }

    public void buttonUpdate(ButtonsEvent e)
    {
        this.players[e.getWiimoteId()].nextCharacter(); // TEST.
    }

    public void expansionUpdate(ExpansionEvent e)
    {
        //this.players[e.getWiimoteId()].update(e);
    }

    public PhysicalPlayer[] getPlayers()
    {
        return this.players;
    }
}
