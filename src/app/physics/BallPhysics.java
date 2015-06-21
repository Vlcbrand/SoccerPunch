package app.physics;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public abstract class BallPhysics
{
    protected int ballSize;
    protected double x, y;
    protected double hSpeed, vSpeed;

    private int top, bot, left, right;
    private ArrayList <Ellipse2D> players;

    protected BallPhysics()
    {
        players = new ArrayList<>();
    }

    private void step()
    {
        //update spelerlocaties
        for (int i = 0; players.size() > i; i ++)
        {
            //speler collision
            if (players.get(i).intersects(x, y, ballSize, ballSize))
            {
                if (vSpeed > 8 || hSpeed > 8)
                {
                    vSpeed *= 0.75;
                    hSpeed *= 0.75;

                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                if (vSpeed < -8 || hSpeed < -8)
                {
                    vSpeed *= 0.75;
                    hSpeed *= 0.75;

                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                if (vSpeed > 8 && hSpeed > 8)
                {
                    vSpeed *= 0.75;
                    hSpeed *= 0.75;

                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                if (vSpeed < -8 && hSpeed < -8)
                {
                    vSpeed *= 0.75;
                    hSpeed *= 0.75;

                    vSpeed = -vSpeed;
                    hSpeed = -hSpeed;
                    break;
                }
                //speler snapping
                else
                {
                    x = players.get(i).getX() + (ballSize / 2);
                    y = players.get(i).getY() + (ballSize / 2);
                    break;
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
        hSpeed *= 0.9899;
        vSpeed *= 0.9899;

        //snelheidsupdate
        this.x += hSpeed;
        this.y += vSpeed;
    }

    public void update(int top, int bot, int left, int right, ArrayList<Ellipse2D> players)
    {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;
        this.players = players;

        this.step();
    }

    public boolean isMoving()
    {
        final double horizontalSpeed = Math.abs(this.hSpeed);
        final double verticalSpeed = Math.abs(this.vSpeed);
        return horizontalSpeed > .1 || verticalSpeed > .1;
    }

    public void accelerate(int force, double degrees)
    {
        double radians = Math.toRadians(degrees);

        hSpeed = -Math.sin(radians)*force;
        vSpeed = Math.cos(radians)*force;
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
