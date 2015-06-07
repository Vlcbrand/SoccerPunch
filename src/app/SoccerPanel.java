package app;

import app.entity.Drawable;
import app.entity.Field;
import app.entity.Player;
import util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

class SoccerPanel extends JPanel
{
    private final static Dimension minimumSize, preferredSize;

    private final int width, height;
    private final Field field;
    private final SoccerModel model;
    private final Drawable[] mainComponents;

    static
    {
        minimumSize = new Dimension(Resource.getInteger("app.width.min"), Resource.getInteger("app.height.min"));
        preferredSize = new Dimension(Resource.getInteger("app.width"), Resource.getInteger("app.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.model = model;

        this.width = (int)this.getPreferredSize().getWidth();
        this.height = (int)this.getPreferredSize().getHeight();

        this.mainComponents = new Drawable[] {
            field = new Field(this.width, this.height)
        };
    }

    public Field getInnerField()
    {
        return this.field;
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        BufferedImage scene = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D sceneGraphics = (Graphics2D)scene.getGraphics();
        sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Update onderdelen.
        List<Player> fieldPlayers = this.model.getFieldPlayers();

        // Teken onderdelen.
        if (mainComponents != null)
            for (Drawable component : mainComponents)
                component.draw(sceneGraphics);

        if (fieldPlayers != null)
            for (Drawable fieldPlayer : fieldPlayers)
                fieldPlayer.draw(sceneGraphics);

        // Scale met het hoofdscherm.
        AffineTransform tx = new AffineTransform();
        final double parentWidth = this.getParent().getWidth();
        final double parentHeight = this.getParent().getHeight();
        tx.scale(parentWidth/this.width, parentHeight/this.height);

        sceneGraphics.dispose();
        g2d.drawImage(scene, tx, this);
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
