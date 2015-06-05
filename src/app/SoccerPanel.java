package app;

import app.entity.Drawable;
import app.entity.Field;
import app.entity.Player;
import util.Resource;

import javax.swing.*;
import java.awt.*;

class SoccerPanel extends JPanel
{
    private final static Dimension minimumSize, preferredSize;

    private final Field field;
    private final Drawable[] mainComponents;

    private Drawable[] fieldPlayers;

    static
    {
        minimumSize = new Dimension(Resource.getInteger("app.width.min"), Resource.getInteger("app.height.min"));
        preferredSize = new Dimension(Resource.getInteger("app.width"), Resource.getInteger("app.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        final int width = (int)this.getPreferredSize().getWidth();
        final int height = (int)this.getPreferredSize().getHeight();

        this.mainComponents = new Drawable[] {
            field = new Field(width, height)
        };

        this.fieldPlayers = model.getFieldPlayers();
    }

    public Field getInnerField()
    {
        return this.field;
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        // Update onderdelen vóór het tekenen.
        field.update(this.getWidth(), this.getHeight());

        // Teken alle onderdelen.
        if (mainComponents != null)
            for (Drawable component : mainComponents)
                component.draw(g2d);

        if (fieldPlayers != null)
            for (Drawable fieldPlayer : fieldPlayers)
                fieldPlayer.draw(g2d);
    }

    @Override public Dimension getMinimumSize()
    {
        return minimumSize;
    }

    @Override public Dimension getPreferredSize()
    {
        return preferredSize;
    }
}
