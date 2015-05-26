package app.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;

public class Field extends JComponent implements Drawable
{
    int x, y, xBorder, yBorder;
    Rectangle2D fieldRect, bKL, bKR, sKL, sKR;
    Ellipse2D circleLeft, circleRight, penaltyLeft, penaltyRight, centerDot,centerCircle;
    Line2D centerLine;
    Shape[] lines;
    Shape[] ellipses;

    public Field(int xBorder, int yBorder, int x, int y)
    {
        this.x = x;
        this.y = y;
        this.xBorder = xBorder;
        this.yBorder = yBorder;

        setField();
    }

    public void setField()
    {
        fieldRect = new Rectangle2D.Double(xBorder, yBorder, x, y);

        bKL = new Rectangle2D.Double(xBorder, yBorder + 250, 165, 400);
        sKL = new Rectangle2D.Double(xBorder, yBorder + 360, 55, 180);
        penaltyLeft = new Ellipse2D.Double(xBorder + 105, yBorder + 440, 10, 10);
        circleLeft = new Ellipse2D.Double(xBorder + 150, yBorder + 350, 60, 140);

        bKR = new Rectangle2D.Double(xBorder +1035, yBorder + 250, 165, 400);
        sKR = new Rectangle2D.Double(xBorder+1145, yBorder+360, 55, 180);
        penaltyRight = new Ellipse2D.Double(xBorder+ 1095, yBorder+440, 10,10);
        circleRight = new Ellipse2D.Double(xBorder + x, yBorder + 125, x/4, y/3);

        centerLine = new Line2D.Double(xBorder+600,yBorder,xBorder+600,yBorder+900);
        centerDot = new Ellipse2D.Double(xBorder+595, yBorder+445, 10,10);
        centerCircle = new Ellipse2D.Double(xBorder+510, yBorder+360,180,180);


        lines = new Shape[] {fieldRect, bKL, sKL, bKR,sKR,centerLine};
        ellipses = new Shape[] {penaltyLeft,penaltyRight,centerDot};
    }

    @Override public void draw(Graphics2D g2d)
    {
        super.paintComponent(g2d);

        float dash[] = {10, 0f};
        g2d.setStroke(new BasicStroke(5.0f, BasicStroke.JOIN_BEVEL, BasicStroke.CAP_SQUARE, 10.0f, dash, 0.0f));
        g2d.draw(fieldRect);
        for (Shape s : lines)
            g2d.draw(s);
        for (Shape s : ellipses){
            g2d.draw(s);
            g2d.fill(s);
        }
        g2d.draw(centerCircle);



        g2d.setClip(new Rectangle2D.Double(xBorder, yBorder + 250, 165, 400));
    }
}
