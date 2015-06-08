package app.entity;

import app.physics.BallPhysics;
import util.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by Tom Remeeus on 5-6-2015.
 */
public class Ball extends BallPhysics implements Drawable
{
    private Rectangle2D ball;
    private static BufferedImage ballImage = util.Image.get("ball.gif");

    public Ball(double locX, double locY, int ballSize)
    {
        super.locX = locX;
        super.locY = locY;
        super.ballSize = ballSize;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.fillRect((int)getLocX(), (int)getLocY(), ballSize, ballSize);
    }
}
