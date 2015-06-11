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
        super.locX = locX;
        super.locY = locY;
        super.ballSize = ballSize;
    }

    public void kickBall(int force, int degrees)
    {
        super.kickBall(force, degrees);
    }

    public void ballMotion(int top, int bot, int left, int right)
    {
        super.ballMotion(top, bot, left, right);
    }

    public void scale()
    {
        double wMin = 908 - 100;
        double wCurrent = super.getRight() - super.getLeft();
        wScale = wCurrent / wMin;

        double hMin = 506 - 56;
        double hCurrent = super.getBot() - super.getTop();
        hScale = hCurrent / hMin;

        System.out.println(hScale);

        if (wScale == 0)
            wScale = 1;
        if (hScale == 0)
            hScale = 1;
    }

    private double relativeX()
    {
        System.out.println(getLocX());
        return getLocX() * wScale;
    }
    private double relativeY()
    {
        return getLocY() * hScale;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.fillRect((int)getLocX(), (int)getLocY(), ballSize, ballSize);
    }
}
