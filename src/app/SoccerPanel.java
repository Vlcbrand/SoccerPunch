package app;

import app.entity.Drawable;
import app.entity.Field;
import app.entity.PhysicalPlayer;
import util.Resource;

import javax.swing.*;
import java.awt.*;

class SoccerPanel extends JPanel
{
    private static Dimension preferredSize;

    private Field field;
    private Drawable[] drawables;
    private PhysicalPlayer[] players;

    static
    {
        preferredSize = new Dimension(Resource.getInteger("app.width"), Resource.getInteger("app.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.players = model.getPlayers();

        final int width = (int)this.getPreferredSize().getWidth();
        final int height = (int)this.getPreferredSize().getHeight();

        this.drawables = new Drawable[] {
            field = new Field(width, height)
        };
    }

    private void updateBackground()
    {
        field.update(this.getWidth(), this.getHeight());
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.black);

        this.updateBackground(); // Ververs alle objecten met nieuwe waarden.

        for (Drawable object : drawables)
            object.draw(g2d);

        for (PhysicalPlayer player : players) {
            g2d.setColor(Color.black);
            g2d.drawOval(player.getX(), player.getY(), 10, 10);
            g2d.setColor(Color.red);
            g2d.drawString("P" + player.getCurrentPlayer(), player.getX() + 10, player.getY() - 5);
        }
    }

    @Override public Dimension getPreferredSize()
    {
        return preferredSize;
    }
}
