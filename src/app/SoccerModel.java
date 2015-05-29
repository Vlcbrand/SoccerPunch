package app;

import app.entity.Player;
import wiiusej.wiiusejevents.physicalevents.ButtonsEvent;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;

/**
 * Bezit én verwerkt spellogica.
 */
class SoccerModel
{
    Player[] players;

    SoccerModel(int players)
    {
        this.players = new Player[players];

        for (int i = 0; i < players; i++)
            this.players[i] = new Player(300 + i*50, 300 + i*50);
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

    public Player[] getPlayers()
    {
        return this.players;
    }
}
