package app.physics;

import java.awt.geom.AffineTransform;

public class BallPhysics
{
    protected int ballSize;
    protected double x, y;

    protected double hSpeed, vSpeed;
    private int top, bot, left, right;

    protected AffineTransform ballTx = new AffineTransform();

    private void step()
    {
        //Correctie vooraf.
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

        hSpeed *= 0.99;
        vSpeed *= 0.99;

        this.x += hSpeed;
        this.y += vSpeed;
    }

    public void update(int top, int bot, int left, int right)
    {
        this.top = top;
        this.bot = bot;
        this.left = left;
        this.right = right;

        this.step();
    }

    public void accelerate(int force, int degrees)
    {
        double radians = Math.toRadians(degrees);

        hSpeed = Math.cos(radians)*force;
        vSpeed = Math.sin(radians)*force;
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
