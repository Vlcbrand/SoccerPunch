package app.entity;

import java.awt.*;

public class HUD implements Drawable
{
    private static HUD instance = null;

    private int parentWidth, parentHeight;
    private int fps;

    private HUD()
    {
    }

    public static HUD getInstance()
    {
        if (instance == null)
            instance = new HUD();

        return instance;
    }

    public void update(int fps)
    {
        this.fps = fps;
    }

    public void update(int parentWidth, int parentHeight)
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

        // Tekenen.
        g2d.drawString("FPS: " + this.fps, 5, fm.getHeight());

        // Herstellen.
        g2d.setFont(originalFont);
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
        return this.parentWidth;
    }

    @Override public int getHeight()
    {
        return this.parentHeight;
    }
}
