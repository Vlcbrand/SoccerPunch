package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by Tom Remeeus on 29-5-2015.
 */
public class RotationTom extends JPanel
{
    public static void main(final String... args)
    {
       {
            JFrame f = new JFrame("RotationTom");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setContentPane(new RotationTom());
            f.setSize(800, 600);
            f.setVisible(true);
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        BufferedImage img = util.Image.get("img/goal.gif");

        //transforms worden van beneden afgewerkt, in dit geval begint de translatie dus bij de tx.translante(-50, -50)
        AffineTransform tx = new AffineTransform();

        tx.translate(this.getWidth()/2 - img.getWidth()/2, this.getHeight() / 2 -img.getHeight()/2);
        tx.translate(img.getWidth()/2, img.getHeight()/2);
        tx.rotate(Math.toRadians(180));
        tx.translate(-img.getWidth()/2, -img.getHeight()/2);

        //teken assenstelsel
        g.setColor(Color.RED);
        g.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
        g.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);


        // util.Image.newBuffered("TEWTERT", Color.CYAN, new Dimension(100, 100), 0);
        g2d.drawImage(img, tx, this);
    }
}
