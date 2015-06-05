package app.entity;

import app.SoccerConstants;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Field implements Drawable
{
    static final SoccerConstants goalOpenFrom;
    static final BufferedImage goalImage;
    AffineTransform txLeft;
    AffineTransform txRight;
    Rectangle2D fieldRect;

    private int width, height;

    private Shape[] lines, spots;

    static {
        goalOpenFrom = SoccerConstants.SOUTH;
        goalImage = util.Image.get("goal.gif");
    }

    public Field(int parentWidth, int parentHeight)
    {
        this.update(parentWidth, parentHeight);
    }

    public void update()
    {
        this.update(this.width, this.height);
    }

    public void update(int parentWidth, int parentHeight)
    {
        // Beïnvloeden veldgrootte.
        final int horizontalScale = parentWidth/120; // Factor voor horizontale afmetingen.
        final int verticalScale = parentHeight/90; // Factor voor verticale afmetingen.
        final int fieldX = parentWidth/10;
        final int fieldY = parentHeight/10;

        // Veldgrootte.
        this.width = parentWidth - fieldX*2;
        this.height = parentHeight - fieldY*2;

        // Startpunten.
        final int centerX = fieldX + this.width/2;
        final int centerY = fieldY + this.height/2;

        // Maten van overige veldonderdelen.
        final int centerCircleSize = (int)(horizontalScale*9.15*2);
        final int centerSpotSize = centerCircleSize/16;
        final int penaltyAreaWidth = (int)(horizontalScale*16.5*.8), penaltyAreaHeight = (int)(verticalScale*40.3);
        final int goalAreaWidth = (int)(horizontalScale*5.5*.9), goalAreaHeight = (int)(verticalScale*(18.3));
        final int goalWidth = (int)Math.max(horizontalScale*1.5, goalAreaWidth*.8);
        final int goalHeight = (int)Math.max(verticalScale*7.5, goalAreaHeight);

        // Tekent veldgrenzen.
        fieldRect = new Rectangle2D.Double(fieldX, fieldY, this.width, this.height);

        // Midden veldonderdelen.
        Line2D centerLine = new Line2D.Double(centerX, fieldY, centerX, fieldY + this.height);
        Ellipse2D centerCircle = new Ellipse2D.Double(centerX - centerCircleSize/2, centerY - centerCircleSize/2, centerCircleSize, centerCircleSize);
        Ellipse2D centerSpot = new Ellipse2D.Double(centerX - centerSpotSize/2, centerY - centerSpotSize/2, centerSpotSize, centerSpotSize);

        // Goal veldonderdelen.
        Rectangle2D leftPenaltyArea = new Rectangle.Double(fieldX, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        Rectangle2D leftGoalArea = new Rectangle.Double(fieldX, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);
        Rectangle2D rightPenaltyArea = new Rectangle.Double(fieldX + this.width - penaltyAreaWidth, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        Rectangle2D rightGoalArea = new Rectangle.Double(fieldX + this.width - goalAreaWidth, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);
        Rectangle2D leftGoal = new Rectangle.Double(fieldX - goalWidth, centerY - goalHeight/2, goalWidth, goalHeight);
        Rectangle2D rightGoal = new Rectangle.Double(fieldX + this.width, centerY - goalHeight/2, goalWidth, goalHeight);

        //linkerdoel met afbeelding
        //transforms worden van beneden afgewerkt.
        txLeft = new AffineTransform();
        //zet het doel op de juiste positie
        txLeft.translate(fieldX - leftGoal.getWidth(),
                 centerY - (leftGoal.getHeight() / 2));
        //schaalt het doel
        double leftGoalScaleW = leftGoal.getWidth() / goalImage.getHeight();
        double leftGoalScaleH = leftGoal.getHeight() / goalImage.getWidth();
        txLeft.scale(leftGoalScaleW, leftGoalScaleH);
        //roteert het doel en zet het doel terug op de beginpositie
        txLeft.translate(goalImage.getHeight() - goalImage.getHeight(), goalImage.getWidth());
        txLeft.rotate(Math.toRadians(-90));

        //rechterdoel met afbeelding
        //transforms worden van beneden afgewerkt.
        txRight = new AffineTransform();
        //zet het doel op de juiste positie
        txRight.translate(fieldX + this.width,
                centerY - (rightGoal.getHeight() / 2));
        //schaalt het doel
        double rightGoalScaleW = rightGoal.getWidth() / goalImage.getHeight();
        double rightGoalScaleH = rightGoal.getHeight() / goalImage.getWidth();
        txRight.scale(rightGoalScaleW, rightGoalScaleH);
        //roteert het doel en zet het doel terug op de beginpositie
        txRight.translate(goalImage.getHeight(), 0);
        txRight.rotate(Math.toRadians(90));

        // Lines worden getekent en spots worden gevuld.
        lines = new Shape[] {fieldRect, centerCircle, centerLine, leftPenaltyArea, rightPenaltyArea, leftGoalArea, rightGoalArea};
        spots = new Shape[] {centerSpot};
    }

    /**
     * Verkrijg de hoek voor het draaien van een doel.
     *
     * @param fieldSide {@link SoccerConstants#WEST} of {@link SoccerConstants#EAST}
     * @return hoek in radialen
     */
    public final double getGoalRotation(SoccerConstants fieldSide)
    {
        double theta;

        // Alles richting het noorden draaien.
        switch (goalOpenFrom) {
            case NORTH:
                theta = 0;
                break;
            case EAST:
                theta = 90;
                break;
            default:
            case SOUTH:
                theta = 180;
                break;
            case WEST:
                theta = 270;
        }

        // In de juiste richting draaien.
        switch (fieldSide) {
            default:
            case WEST:
                theta += 90;
                break;
            case EAST:
                theta -= 90;
        }

        return Math.toRadians(theta);
    }

    public Rectangle2D getField()
    {
        return fieldRect;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final BasicStroke stroke = new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        g2d.setStroke(stroke);

        for (Shape s : lines)
            g2d.draw(s);

        for (Shape s : spots)
            g2d.fill(s);

        g2d.drawImage(goalImage, txLeft, null);
        g2d.drawImage(goalImage, txRight, null);
    }
}
