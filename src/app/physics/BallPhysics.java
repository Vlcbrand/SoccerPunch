package app.physics;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created byCurrent Tom Remeeus on 4-6-2015.
 */
public class BallPhysics
{
    protected int ballSize;
    protected double locX;
    protected double locY;
    private double hSpeed;
    private double vSpeed;

    public void ballMotion(Rectangle2D field)
    {
        //rolweerstand
        if (hSpeed > 0 || hSpeed < 0) {
            hSpeed = hSpeed * 0.99;
            locX = locX + hSpeed;
        }
        if (vSpeed > 0 || vSpeed < 0) {
            vSpeed = vSpeed * 0.99;
            locY = locY + vSpeed;
        }

        //botsing muur met loodrechte hoek
        if (!field.intersects(locX, locY, ballSize, ballSize) && vSpeed == 0)
        {
            hSpeed = -hSpeed * 0.50;
            locX = locX + hSpeed;
        }
        if (!field.intersects(locX, locY, ballSize, ballSize) && hSpeed == 0)
        {
            vSpeed = -vSpeed * 0.50;
            locY = locY + vSpeed;
        }

        //botsing muur
        if (!field.intersects(locX, locY, ballSize, ballSize) && hSpeed != 0)
        {
            vSpeed = vSpeed * 0.50;
            locY = locY + vSpeed;

            hSpeed = -hSpeed * 0.50;
            locX = locX + hSpeed;
        }
        if (!field.intersects(locX, locY, ballSize, ballSize) && vSpeed != 0)
        {
            hSpeed = hSpeed * 0.50;
            locX = locX + hSpeed;

            vSpeed = -vSpeed * 0.50;
            locY = locY + vSpeed;
        }
    }

    public void kickBall(int force, int degrees)
    {
        double radians = Math.toRadians(degrees);

        hSpeed = Math.cos(radians) * force;
        vSpeed = Math.sin(radians) * force;
    }

    protected double getLocX()
    {
        return locX;
    }
    protected double getLocY()
    {
        return locY;
    }
}