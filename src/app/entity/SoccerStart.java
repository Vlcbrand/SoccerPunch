package app.entity;

import app.entity.Drawable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SoccerStart implements Drawable {
    private boolean drawscreen;
    private int x, y, width, height, textX, textY;
    private String text;

    public void setDrawscreen(boolean drawscreen) {
        this.drawscreen = drawscreen;
    }

    @Override
    public void draw(Graphics2D g2d)
    {
        float x = 0.5f;
        Color colorBg = new Color(0, 0, 0, x);
        Color colorTxt = new Color(1, 1, 1, x);
        Color clear = new Color(0, 0, 0, 0f);
        Rectangle2D fade = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        Font font = new Font("Arial",Font.ITALIC,80);

        if (drawscreen) {
            g2d.setColor(colorBg);
            g2d.fill(fade);
            g2d.draw(fade);
            g2d.setFont(font);
            g2d.setColor(colorTxt);
            g2d.drawString(text, textX, textY);
        }

        if (!drawscreen) {
            g2d.setColor(clear);
        }
    }

    public void setText(int x, int y, String text)
    {
        this.textX = x;
        this.textY = y;
        this.text = text;
    }

    public void setBackground(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
