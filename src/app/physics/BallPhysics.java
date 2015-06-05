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
    protected double locX;
    protected double locY;
    private double hSpeed;
    private double vSpeed;
    private Rectangle2D ball;

    public void ballMotion(Rectangle2D field)
    {
        //rolweerstand
        if (hSpeed > 0 || hSpeed < 0) {
            hSpeed = hSpeed*0.99;
            locX = locX + hSpeed;
        }

        if (vSpeed > 0 || vSpeed < 0) {
            vSpeed = vSpeed*0.99;
            locY = locY + vSpeed;
        }

        //if (vSpeed > 0 || vSpeed < 0 && ball)
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