package app;

import app.entity.Drawable;
import app.entity.Field;
import app.entity.Goal;

import javax.swing.*;
import java.awt.*;


class SoccerPanel extends JPanel
{
    private static Dimension preferredSize;

    private Field field;
    private Goal goal1, goal2;
    private Drawable[] drawables;

    static {
        preferredSize = new Dimension(1024, 600);
    }

    public SoccerPanel()
    {
        super(null);

        final int width = (int)this.getPreferredSize().getWidth();
        final int height = (int)this.getPreferredSize().getHeight();

        field = new Field(width, height);
        goal1 = new Goal(field.getFieldX(), field.getFieldY(), 0.5, 0.5, 1);
        goal2 = new Goal(field.getFieldX() + field.getWidth(), field.getFieldY(), 0.5, 0.5, 0);
        this.drawables = new Drawable[] {field, goal1, goal2};
    }

    /**
     * Biedt nieuwe waarden aan alle tekenbare objecten.
     */

    private void refreshDrawables()
    {
        field.refreshField(this.getWidth(), this.getHeight());
        goal1.setRect(field.getFieldX() - (int)goal1.getScaleWidth()/10, field.getHeight()/2 - field.getGoalAreaHeight()/8 + (int)(field.getFieldY()*1.25), field.getGoalAreaWidth()*4, field.getGoalAreaHeight()/8);
        goal2.setRect(field.getFieldX() + (int)goal1.getScaleWidth()/10 + field.getWidth(), field.getHeight()/2 - field.getGoalAreaHeight()/10 + (int)(field.getFieldY()*1.25), field.getGoalAreaWidth()*4, field.getGoalAreaHeight()/8);
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
