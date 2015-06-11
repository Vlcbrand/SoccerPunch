package app.physics;

/**
 * Created by Tom Remeeus on 4-6-2015.
 */
public class BallPhysics
{
    protected int ballSize;
    protected double x, y;

    private double hSpeed, vSpeed;
    private int top, bot, left, right;

    private void step()
    {
        // Correctie vooraf.
        if (y < top) {
            this.y = top + ballSize;
            vSpeed = -vSpeed;
        } else if (y > bot) {
            this.y = bot - ballSize;
            vSpeed = -vSpeed;
        } else if (x > right) {
            this.x = right - ballSize;
            hSpeed = -hSpeed;
        } else if (x < left) {
            this.x = left + ballSize;
            hSpeed = -hSpeed;
        }

        //horizontale botsing
        if ((x <= left || x >= (right - ballSize)))
            hSpeed = -hSpeed;

        //verticale botsing
        if ((y <= top || y >= (bot - ballSize)))
            vSpeed = -vSpeed;

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

    protected double getX()
    {
        return x;
    }

    protected double getY()
    {
        return y;
    }

    protected int getTop()
    {
        return top;
    }

    protected int getBottom()
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
