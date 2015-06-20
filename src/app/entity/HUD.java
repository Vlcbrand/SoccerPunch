package app.entity;

import app.SoccerConstants;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Heads-up display voor op een SoccerPanel.
 * HUD is een singleton, dus gerbruik {@link #getInstance()}.
 */
public class HUD implements Drawable
{
    private final static int xOffset, yOffest;
    private final static BufferedImage bannerImageLeft, bannerImageRight;
    private final static double bannerImageScale = .2;

    private static HUD instance = null;

    private int parentWidth, parentHeight;
    private int scoreLeft, scoreRight;
    private int fps;

    static {
        xOffset = yOffest = 10;

        bannerImageLeft = util.Image.get("teambanner_blue.png");
        bannerImageRight = util.Image.get("teambanner_red.png");
    }

    private HUD()
    {
    }

    public void addGoalPoint(SoccerConstants soccerConstants)
    {
        if(soccerConstants.equals(SoccerConstants.EAST))
            scoreLeft++;
        else if(soccerConstants.equals(SoccerConstants.WEST))
            scoreRight++;
    }

    public static HUD getInstance()
    {
        if (instance == null)
            instance = new HUD();

        return instance;
    }

    public void updateFPS(int fps)
    {
        this.fps = fps;
    }

    public void updateParentDimensions(int parentWidth, int parentHeight)
    {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    @Override public void draw(Graphics2D g2d)
    {
        final Font originalFont = g2d.getFont();
        final Font font = new Font("Arial", Font.BOLD, 14);

        // Voorbereiden.
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        // Tekent FPS-meter.
        g2d.drawString("FPS " + this.fps, 5, fm.getHeight());

        Font fontScore = new Font("Arial", font.BOLD, 40);
        g2d.setFont(fontScore);

        // Banners voorbereiden.
        final int bannerWidth = (int)(bannerImageLeft.getWidth()*bannerImageScale);
        final int bannerHeight = (int)(bannerImageLeft.getHeight()*bannerImageScale);
        final int marginTop = 10;

        // Banners tekenen.
        g2d.drawImage(bannerImageLeft, xOffset, yOffest + marginTop, bannerWidth, bannerHeight, null);
        g2d.drawImage(bannerImageRight, this.parentWidth - xOffset - bannerWidth, yOffest + marginTop, bannerWidth, bannerHeight, null);


        // Scores tekenen.
        g2d.setColor(Color.blue);
        g2d.drawString(""+ scoreLeft, xOffset + bannerWidth/4, yOffest + 300);
        g2d.setColor(Color.red);
        g2d.drawString(""+ scoreRight, this.parentWidth-xOffset -bannerWidth/2, yOffest + 300);

        // Herstellen.
        g2d.setFont(originalFont);
        g2d.setColor(Color.white);
    }

    @Override public int getX()
    {
        return xOffset;
    }

    @Override public int getY()
    {
        return yOffest;
    }

    @Override public int getWidth()
    {
        return this.parentWidth - xOffset*2;
    }

    @Override public int getHeight()
    {
        return this.parentHeight - yOffest*2;
    }
}
