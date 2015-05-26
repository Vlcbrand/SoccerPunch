package app.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Field extends JComponent
        implements Drawable
{
    int x,y,xBorder, yBorder;
    Rectangle2D fieldRect;


    public Field(int xBorder, int yBorder, int x, int y)
    {
        this.x = x;
        this.y = y;
        this.xBorder = xBorder;
        this.yBorder = yBorder;
        fieldRect = new Rectangle2D.Double(xBorder, yBorder, x, y);

    }

    @Override public void draw(Graphics2D g2d)
    {
        super.paintComponent(g2d);
        float dash[] = {10, 0f};
        g2d.setStroke(new BasicStroke(10.0f, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_SQUARE, 10.0f, dash, 0.0f));
        g2d.draw(fieldRect);
    }
}
