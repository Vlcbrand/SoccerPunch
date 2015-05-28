package app;

import app.entity.Drawable;
import app.entity.Field;

import javax.swing.*;
import java.awt.*;


class SoccerPanel extends JPanel
{
    private static Dimension preferredSize;

    private Field field;
    private Drawable[] drawables;

    static {
        preferredSize = new Dimension(1024, 600);
    }

    SoccerPanel()
    {
        super(null);

        final int width = (int)this.getPreferredSize().getWidth();
        final int height = (int)this.getPreferredSize().getHeight();

        field = new Field(width, height);

        this.drawables = new Drawable[] {
            field
        };
    }

    /**
     * Biedt nieuwe waarden aan alle tekenbare objecten.
     */
    private void refreshDrawables()
    {
        field.refreshField(this.getWidth(), this.getHeight());
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        this.refreshDrawables(); // Ververs alle objecten met nieuwe waarden.

        for (Drawable object : drawables)
            object.draw(g2d);
    }

    @Override public Dimension getPreferredSize()
    {
        return preferredSize;
    }
}
