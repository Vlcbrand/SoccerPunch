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

    private final int initialWidth, initialHeight;
    private final Field field;
    private final SoccerModel model;
    private final Drawable[] mainComponents;

    private List<Player> fieldPlayers;

    static
    {
        minimumSize = new Dimension(Resource.getInteger("app.width.min"), Resource.getInteger("app.height.min"));
        preferredSize = new Dimension(Resource.getInteger("app.width"), Resource.getInteger("app.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.model = model;

        this.initialWidth = (int)this.getPreferredSize().getWidth();
        this.initialHeight = (int)this.getPreferredSize().getHeight();

        this.mainComponents = new Drawable[] {
            field = new Field(this.initialWidth, this.initialHeight)
        };
    }

    public Field getInnerField()
    {
        return this.field;
    }

    public void update()
    {
        fieldPlayers = this.model.getFieldPlayers();
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        BufferedImage scene = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D sceneGraphics = (Graphics2D)scene.getGraphics();
        sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Teken hoofdonderdelen.
        if (mainComponents != null)
            for (Drawable component : mainComponents)
                component.draw(sceneGraphics);

        // Teken spelers.
        if (fieldPlayers != null)
            for (Drawable fieldPlayer : fieldPlayers)
                fieldPlayer.draw(sceneGraphics);

        // Teken test indien spelers aangemaakt zijn.
        if (fieldPlayers != null && fieldPlayers.size() > 0)
            this.drawJoystickTest(sceneGraphics);

        // Scale met het hoofdscherm.
        AffineTransform tx = new AffineTransform();
        final double parentWidth = this.getParent().getWidth();
        final double parentHeight = this.getParent().getHeight();
        tx.scale(parentWidth/this.initialWidth, parentHeight/this.initialHeight);

        sceneGraphics.dispose();
        g2d.drawImage(scene, tx, this);
    }

    private void drawJoystickTest(Graphics2D g2d)
    {
        // Afstanden e.d.
        final int boundRadius = 20;
        final int cursorSize = 10;
        final int xOffset = this.field.getX()/2 - boundRadius/2;
        final int yOffset = this.field.getHeight() + this.field.getY() + boundRadius/2;

        // Bronnen.
        final Player[] fieldPlayers = {
            this.fieldPlayers.get(0), // Spits links.
            this.fieldPlayers.get(4) // Spits rechts.
        };

        // Voorbereiding.
        g2d.setStroke(new BasicStroke());

        for (Player fieldPlayer : fieldPlayers) {
            if (fieldPlayer == null)
                continue;

            final int trueXOffset = fieldPlayer.getSide() == SoccerConstants.WEST ? xOffset : xOffset + field.getX() + field.getWidth();

            final double[] coords = fieldPlayer.getMovement();
            final int x = (int)(coords[0]*boundRadius/2) + trueXOffset;
            final int y = (int)(coords[1]*boundRadius/2) + yOffset;

            // Tekent titel.
            g2d.setPaint(Color.black);
            g2d.drawString(fieldPlayer.getX() + ", " + fieldPlayer.getY(), trueXOffset - 30, yOffset - 15);

            // Tekent omheining.
            g2d.drawOval(-boundRadius/2 + trueXOffset, -boundRadius/2 + yOffset, boundRadius, boundRadius);

            // Tekent cursor.
            g2d.setPaint(Color.red);
            g2d.fillOval(x - cursorSize/2, y - cursorSize/2, cursorSize, cursorSize);
            g2d.drawString("(" + (x - trueXOffset) + ", " + (y - yOffset) + ")", x + cursorSize + 5, y + cursorSize/2);
        }
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
