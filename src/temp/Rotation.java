package temp;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

/**
 * Created by nikita on 28-05-2015
 */
public class Rotation extends JPanel
{
    public static void main(final String... args)
    {
        EventQueue.invokeLater(() -> {
            JFrame f = new JFrame("Rotation");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setContentPane(new Rotation());
            f.setSize(800, 600);
            f.setVisible(true);
        });
    }

    @Override protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw axi.
        g2d.drawLine(this.getWidth()/2, 0, this.getWidth()/2, this.getHeight());
        g2d.drawLine(0, this.getHeight()/2, this.getWidth(), this.getHeight()/2);

        // Create font and Glyph Vector based on created font.
        String str = "Hello World!";
        Font f = new Font("Arial", Font.PLAIN, 24);
        FontMetrics fm = this.getFontMetrics(f);
        GlyphVector gv = f.createGlyphVector(fm.getFontRenderContext(), str);

        // Calculate points.
        final int centerX = this.getWidth()/2;
        final int centerY = this.getHeight()/2;
        final int strWidth = fm.stringWidth(str);
        final int strHeight = fm.getHeight();
        final int anchorX = strWidth/2;
        final int anchorY = strHeight/2;

        final int tempXPos = this.getWidth() - 100;
        g2d.drawLine(tempXPos, 0, tempXPos, this.getHeight());

        g2d.translate(centerX, centerY);

        // Transform 1.
        AffineTransform tx1 = new AffineTransform();
        tx1.rotate(Math.PI/-2, anchorX, 0);

        // Transform 2.
        AffineTransform tx2 = new AffineTransform();
        tx2.rotate(Math.PI/2, anchorX, 0);

        // Create shapes.
        Shape[] shapes = {
                tx1.createTransformedShape(gv.getOutline()),
                tx2.createTransformedShape(gv.getOutline())
        };

        // Draw shapes.
        for (Shape shape : shapes)
            g2d.fill(shape);
    }
}
