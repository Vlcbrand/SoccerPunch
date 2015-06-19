package app.physics;

import app.wii.WiimoteButton;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tom Remeeus on 4-6-2015.
 */
public class BallPhysics
{
    protected int ballSize;
    protected double x, y;
    protected double hSpeed, vSpeed;

    private int top, bot, left, right;
    private ArrayList <Ellipse2D.Double> players = new ArrayList<Ellipse2D.Double>();
    private Random random = new Random();

    private void step()
    {
        //update spelerlocaties
        for (int i = 0; players.size() - 1 > i; i ++)
        {
            //speler collision
            if (players.get(i).intersects(x, y, ballSize, ballSize))
            {
                if (vSpeed > 4 || hSpeed > 4)
                {
                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                if (vSpeed > 4 && hSpeed > 4)
                {
                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                else
                {
                    hSpeed = 0;
                    vSpeed = 0;

                    x = players.get(i).getX();
                    y = players.get(i).getY();
                }
            }
        }

        //Muur collision
        if (y < top) {
            this.y = top;
            vSpeed *= 0.75;
            hSpeed *= 0.75;
            vSpeed = -vSpeed;
        } else if (y + ballSize > bot) {
            this.y = bot - ballSize;
            vSpeed *= 0.75;
            hSpeed *= 0.75;
            vSpeed = -vSpeed;
        } else if (x + ballSize > right) {
            this.x = right - ballSize;
            vSpeed *= 0.75;
            hSpeed *= 0.75;
            hSpeed = -hSpeed;
        } else if (x < left) {
            this.x = left;
            vSpeed *= 0.75;
            hSpeed *= 0.75;
            hSpeed = -hSpeed;
        }

        //rolweerstand
        hSpeed *= 0.99;
        vSpeed *= 0.99;

        //snelheidsupdate
        this.x += hSpeed;
        this.y += vSpeed;
    }

    public void update(int top, int bot, int left, int right, ArrayList<Ellipse2D.Double> players)
    {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;
        this.players = players;

        this.step();

    }

    public void accelerate(int force, double degrees)
    {
        double radians = Math.toRadians(degrees);

        hSpeed = Math.sin(radians)*force;
        vSpeed = -Math.cos(radians)*force;
    }

    public double getBallX()
    {
        return x;
    }
    public double getBallY()
    {
        return y;
    }
}