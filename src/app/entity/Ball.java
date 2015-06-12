package app.entity;

import app.physics.BallPhysics;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by Tom Remeeus on 5-6-2015.
 */
public class Ball extends BallPhysics implements Drawable
{
    private Ellipse2D ball;
    private int imgCount;

    public Ball(double locX, double locY)
    {
        super.x = locX;
        super.y = locY;

        super.ballSize = ballImages().getHeight();
        ball = new Ellipse2D.Double();
    }

    public void offset(int dx, int dy)
    {
        super.x += dx;
        super.y += dy;
    }

    private BufferedImage ballImages()
    {
        BufferedImage ballImage[] = new BufferedImage[7];
        ballImage[0] = util.Image.get("ball1.png");
        ballImage[1] = util.Image.get("ball2.png");
        ballImage[2] = util.Image.get("ball3.png");
        ballImage[3] = util.Image.get("ball4.png");
        ballImage[4] = util.Image.get("ball5.png");
        ballImage[5] = util.Image.get("ball6.png");
        ballImage[6] = util.Image.get("ball7.png");

        if (imgCount >= (ballImage.length - 1))
        {
            imgCount = 0;
            return ballImage[imgCount];
        }

        else
            imgCount ++;
            return ballImage[imgCount];
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.fill(ball);
        g2d.drawImage(ballImages(), (int)getX(), (int)getY(), null);
    }

    public Rectangle2D getBall()
    {
        Rectangle2D ballRect = new Rectangle2D.Double();
        ballRect.setRect(getX(), getY(), ballSize, ballSize);
        return ballRect;
    }
}
