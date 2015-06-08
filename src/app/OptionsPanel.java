package app;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by frits on 8-6-15.
 */
public class OptionsPanel extends JPanel
{
    BufferedImage background;
    Rectangle2D rect;
    float alpha = 0.5f;
    Color color = new Color(1, 1, 1, alpha);
    private int min;
    private String time;

    public OptionsPanel()
    {
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.GREEN);
        background = util.Image.get("grass_texture3.jpg");
        time = "Playtime: " + min;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        Font font = new Font("SansSerif", Font.PLAIN, 70);
        Font font1 = new Font("Impact", Font.ITALIC, 100);

        g2.drawImage(background, -100, -100, getWidth() + 200, getHeight() + 200, null);

        rect = new Rectangle2D.Double(getWidth()/2 - 330, 0, 680, getHeight());
        g2.draw(rect);
        g2.setPaint(color);
        g2.fill(rect);

        g2.setColor(Color.black);

        g2.setFont(font);

        g2.drawString(time, setWidthString(time, getWidth(), g2), getHeight()/2);

        g2.setFont(font1);
        g2.drawString("SoccerPunch!", setWidthString("SoccerPunch!", getWidth(), g2), getHeight()/2 - 200);

    }

    public int setWidthString(String s, int width, Graphics g)
    {

        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(s))/2 + 2;
        return x;
    }
}
