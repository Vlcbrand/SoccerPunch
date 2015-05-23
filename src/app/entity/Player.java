package app.entity;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Player
    implements Drawable
{
    public double x, y;

    private final int step = 5;

    public Player()
    {
        x = 390;
        y = 290;
    }

    public void up()
    {
        y -= step;
    }

    public void down()
    {
        y += step;
    }

    public void left()
    {
        x -= step;
    }

    public void right()
    {
        x += step;
    }

    @Override public void draw(Graphics2D g2d)
    {
        Ellipse2D ellipse = new Ellipse2D.Double(x, y, 20, 20);

        g2d.setColor(Color.BLACK);
        g2d.fill(ellipse);
    }
}

