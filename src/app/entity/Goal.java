package app.entity;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Goal extends JComponent
    implements Drawable
{
    public static final int EAST = 0, WEST = 1;

    private int locX;
    private int locY;
    private double scaleW;
    private double scaleH;
    private BufferedImage image;
    private Point2D loc;
    private int side;

    /**
     * @param side {@link #EAST} of {@link #WEST}.
     */
    public Goal(int locX, int locY, double scaleW, double scaleH, int side)
    {
        this.locX = locX;
        this.locY = locY;
        this.scaleW = scaleW;
        this.scaleH = scaleH;
        this.side = side;

        try {
            image = ImageIO.read(new File("images/goal.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void draw(Graphics2D g2d)
    {
        super.paintComponent(g2d);
        AffineTransform tx = new AffineTransform();

        // goal verplaatsen.
        tx.translate(locX, locY);

        // goal schalen.
        tx.scale(scaleW, scaleH);

        // goal draaien voor juiste zijde.
        switch (this.side) {
            case EAST:
                tx.rotate(Math.PI/2); break;
            case WEST:
                tx.rotate(Math.PI/-2);
        }

        tx.translate(-image.getWidth()/2, -image.getHeight()/2);

        // goal tekenen.
        g2d.drawImage(image, tx, null);
    }

    public Point2D getLoc()
    {
        loc.setLocation(locX, locY);
        return loc;
    }
}
