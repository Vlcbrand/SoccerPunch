package app.entity;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Field extends JComponent
    implements Drawable
{
    private int width, height;
    private Shape[] lines, spots;

    public Field(int parentWidth, int parentHeight)
    {
        this.refreshField(parentWidth, parentHeight);
    }

    public void refreshField()
    {
        refreshField(this.width, this.height);
    }

    public void refreshField(int width, int height)
    {
        // Beïnvloeden veldgrootte.
        final int horizontalScale = width/120; // Factor voor horizontale afmetingen.
        final int verticalScale = height/90; // Factor voor verticale afmetingen.
        final int goalWidth = 50; // Dit opvragen in toekomst.
        final int fieldX = width/10 + goalWidth;
        final int fieldY = height/10;

        // Veldgrootte.
        this.width = width - fieldX*2;
        this.height = height - fieldY*2;

        // Startpunten.
        final int centerX = fieldX + this.width/2;
        final int centerY = fieldY + this.height/2;

        // Maten van overige veldonderdelen.
        final int centerCircleSize = (int)(horizontalScale*9.15*2);
        final int centerSpotSize = centerCircleSize/16;
        final int penaltyAreaWidth = (int)(horizontalScale*16.5 *.8), penaltyAreaHeight = (int)(verticalScale*40.3);
        final int goalAreaWidth = (int)(horizontalScale*5.5 * .9), goalAreaHeight = (int)(verticalScale*(40.3-22));

        // Tekent veldgrenzen.
        Rectangle2D fieldRect = new Rectangle2D.Double(fieldX, fieldY, this.width, this.height);

        // Midden veldonderdelen.
        Line2D centerLine = new Line2D.Double(centerX, fieldY, centerX, fieldY + this.height);
        Ellipse2D centerCircle = new Ellipse2D.Double(centerX - centerCircleSize/2, centerY - centerCircleSize/2, centerCircleSize, centerCircleSize);
        Ellipse2D centerSpot = new Ellipse2D.Double(centerX - centerSpotSize/2, centerY - centerSpotSize/2, centerSpotSize, centerSpotSize);

        // Goal veldonderdelen.
        Rectangle2D leftPenaltyArea = new Rectangle.Double(fieldX, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        Rectangle2D leftGoalArea = new Rectangle.Double(fieldX, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);
        Rectangle2D rightPenaltyArea = new Rectangle.Double(fieldX + this.width - penaltyAreaWidth, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        Rectangle2D rightGoalArea = new Rectangle.Double(fieldX + this.width - goalAreaWidth, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);

        // Lines worden getekent en spots worden gevuld.
        lines = new Shape[] {fieldRect, centerCircle, centerLine, leftPenaltyArea, rightPenaltyArea, leftGoalArea, rightGoalArea};
        spots = new Shape[] {centerSpot};
    }

    @Override public void draw(Graphics2D g2d)
    {
        final BasicStroke stroke = new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        g2d.setStroke(stroke);

        for (Shape s : lines)
            g2d.draw(s);

        for (Shape s : spots)
            g2d.fill(s);
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
