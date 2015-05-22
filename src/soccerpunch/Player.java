package soccerpunch;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;

/**
 * Created by frits on 30-4-15.
 */
public class Player
{
    private final int step = 5;

    public double x,y;

    KeyEvent event;

    public Player(){

        x = 390;
        y = 290;

    }

    public void right()
    {
        x += step;
    }

    public void left()
    {
        x-= step;
    }

    public void up()
    {
        y -= step;
    }

    public void down()
    {
        y += step;
    }

    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;

        Ellipse2D ellipse = new Ellipse2D.Double(x,y,20,20);

        g2.setColor(Color.BLACK);

        g2.fill(ellipse);
    }
}

