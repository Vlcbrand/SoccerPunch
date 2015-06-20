package app.entity;

import app.SoccerPanel;
import util.Resource;

import java.awt.*;

public class StartSequence implements Drawable, Updatable
{
    private static final String text;
    private static final Color backgroundColor, foregroundColor;

    private static StartSequence instance;

    private int width, height;
    private boolean isActive;
    private boolean hasSize;

    static {
        text = Resource.get().getString("string.press_a");
        backgroundColor = new Color(0, 0, 0, .6f);
        foregroundColor = new Color(1, 1, 1, .5f);
    }

    private StartSequence()
    {
        this.hasSize = false;
        this.activate();
    }

    public static StartSequence getInstance()
    {
        if (instance == null)
            instance = new StartSequence();

        return instance;
    }

    public void deactivate()
    {
        this.isActive = false;
    }

    public void activate()
    {
        this.isActive = true;
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    @Override public void update(final SoccerPanel parent)
    {
        if (!isActive || hasSize)
            return;

        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.hasSize = true;
    }

    @Override public void draw(Graphics2D g2d)
    {
        if (!isActive)
            return;

        final Font originalFont = g2d.getFont();
        final Color originalColor = g2d.getColor();

        final Font font = new Font("Arial", Font.BOLD, 28);
        g2d.setFont(font);

        final FontMetrics fm = g2d.getFontMetrics();
        final int textX = this.width/2 - fm.stringWidth(text)/2;
        final int textY = this.height - (this.height/3 - fm.getHeight()/2);

        // Achtergrond tekenen.
        g2d.setPaint(backgroundColor);
        g2d.fillRect(0, 0, this.width, this.height);

        // Voorgrond tekenen.
        g2d.setPaint(foregroundColor);
        g2d.drawString(text, textX, textY);

        // Herstellen.
        g2d.setFont(originalFont);
        g2d.setPaint(originalColor);
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
        return this.width;
    }

    @Override public int getHeight()
    {
        return this.height;
    }
}
