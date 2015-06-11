package app.physics;

import app.entity.Field;

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
    private int top;
    private int bot;
    private int left;
    private int right;

    protected void ballMotion(int top, int bot, int left, int right)
    {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;

        //rolweerstand
        if (hSpeed > 0 || hSpeed < 0) {
            //hSpeed = hSpeed * 0.99;
            locX = locX + hSpeed;
        }
        if (vSpeed > 0 || vSpeed < 0) {
            //vSpeed = vSpeed * 0.99;
            locY = locY + vSpeed;
        }

        //horizontale botsing
        if ((locX <= left || locX >= (right - ballSize)))
        {
            System.out.println("horizontale boem");
            //hSpeed = -hSpeed * 0.50;
            hSpeed = -hSpeed;
            locX = locX + hSpeed;

            //vSpeed = vSpeed * 0.50;
            locY = locY + vSpeed;
        }

        //verticale botsing
        if ((locY <= top || locY >= (bot - ballSize)))
        {
            System.out.println("verticale boem");
           // vSpeed = -vSpeed * 0.50;
            vSpeed = -vSpeed;
            locY = locY + vSpeed;

            //hSpeed = hSpeed * 0.50;
            locX = locX + hSpeed;
        }
    }

    protected void kickBall(int force, int degrees)
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
    protected int getTop()
    {
        return top;
    }
    protected int getBot()
    {
        return bot;
    }
    protected int getLeft()
    {
        return left;
    }
    protected int getRight()
    {
        return right;
    }
}