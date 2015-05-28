package app.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import util.Image;

public class Goal extends JComponent implements Drawable
{
    private static final int EAST = 0, WEST = 1;
    private int locX;
    private int locY;
    private double scaleW;
    private double scaleH;
    private BufferedImage image;
    private int side;
    private Rectangle collisionRec = new Rectangle();

    /**
     * @param side {@link #EAST} of {@link #WEST}.
     */
    public Goal(int locX, int locY, double scaleW, double scaleH, int side)
    {
        this.side = side;
        this.setRect(locX, locY, scaleW, scaleH);
        image = Image.get("goal.gif");
    }

    @Override public void draw(Graphics2D g2d)
    {
        AffineTransform tx = new AffineTransform();

        // goal verplaatsen.
        tx.translate(locX, locY);
        // goal schalen.
        //tx.scale(scaleW, scaleH);
        tx.scale(scaleW/image.getWidth(), scaleH/image.getHeight());
        // goal draaien voor juiste zijde.
        switch (this.side) {
            case EAST:
                tx.rotate(Math.PI/2);
                break;
            case WEST:
                tx.rotate(Math.PI/-2);
        }
        tx.translate(-image.getWidth()/2, -image.getHeight()/2);
        g2d.drawImage(image, tx, null);
        //g2d.drawImage(image, locX, locY, (int)scaleH,(int)scaleW,null);
    }

    public void setRect(int x, int y, double scaleW, double scaleH)
    {
        this.locX = x;
        this.locY = y;
        this.scaleW = scaleW;
        this.scaleH = scaleH;

    }

    public double getScaleWidth()
    {
        return scaleW;
    }

    public double getScaleHeight()
    {
        return scaleH;
    }

    public int getLocX()
    {
        return locX;
    }
}
