package app.entity;

import app.SoccerConstants;

import java.awt.*;

/**
 * Een tekenbare speler voor op het voetbalveld.
 */
public class Player implements Drawable
{
    private final SoccerConstants side;
    private int x, y;

    public Player(SoccerConstants side)
    {
        this.side = side;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public SoccerConstants getSide()
    {
        return this.side;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.setPaint(Color.black);
        g2d.drawOval(this.x, this.y, 10, 10);
    }
}
