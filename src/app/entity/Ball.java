package app.entity;

import app.physics.BallPhysics;
import java.awt.*;

/**
 * Created by Tom Remeeus on 5-6-2015.
 */
public class Ball extends BallPhysics implements Drawable
{
    private double hScale = 1;
    private double wScale = 1;

    public Ball(double locX, double locY, int ballSize)
    {
        super.x = locX;
        super.y = locY;
        super.ballSize = ballSize;
    }

    public void scale()
    {
        double wMin = 908 - 100;
        double wCurrent = super.getRight() - super.getLeft();
        wScale = wCurrent / wMin;

        double hMin = 506 - 56;
        double hCurrent = super.getBottom() - super.getTop();
        hScale = hCurrent / hMin;

        if (wScale == 0)
            wScale = 1;
        if (hScale == 0)
            hScale = 1;
    }

    public void offset(int dx, int dy)
    {
        super.x += dx;
        super.y += dy;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.fillRect((int)getX(), (int)getY(), ballSize, ballSize);
    }
}
