package app.entity;

import app.physics.BallPhysics;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Ball extends BallPhysics implements Drawable
{
    private final BufferedImage[] ballImage;

    private int imageIndex;

    public Ball(double x, double y)
    {
        super.x = x;
        super.y = y;

        this.ballImage = new BufferedImage[7];
        this.ballImage[0] = util.Image.get("ball/ball1.1.png");
        this.ballImage[1] = util.Image.get("ball/ball2.2.png");
        this.ballImage[2] = util.Image.get("ball/ball3.3.png");
        this.ballImage[3] = util.Image.get("ball/ball4.4.png");
        this.ballImage[4] = util.Image.get("ball/ball5.5.png");
        this.ballImage[5] = util.Image.get("ball/ball6.6.png");
        this.ballImage[6] = util.Image.get("ball/ball7.7.png");

        super.ballSize = getBallImage().getHeight() - 5;
    }

    public void offset(int dx, int dy)
    {
        super.x += dx;
        super.y += dy;
    }

    private BufferedImage getBallImage()
    {
        if (this.imageIndex >= this.ballImage.length - 1)
            this.imageIndex = 0;
        else
            this.imageIndex++;

        return ballImage[imageIndex];
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.drawImage(this.isMoving() ? getBallImage() : ballImage[0], (int)getBallX(), (int)getBallY(), null);
    }

    public Rectangle2D getBall()
    {
        Rectangle2D ballRect = new Rectangle2D.Double();
        ballRect.setRect(getBallX(), getBallY(), ballSize, ballSize);
        return ballRect;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    @Override public int getX()
    {
        return 0;
    }

    @Override public int getY()
    {
        return 0;
    }

    @Override public int getWidth()
    {
        return 0;
    }

    @Override public int getHeight()
    {
        return 0;
    }
}
