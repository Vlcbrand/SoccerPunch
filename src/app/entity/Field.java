package app.entity;

import app.SoccerConstants;
import app.SoccerPanel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Een tekenbaar veld voor op een SoccerPanel.
 * Field bezit twee doelen en overige veldonderdelen.
 * Field is een singleton, dus gebruik {@link #getInstance()}.
 */
public class Field implements Drawable, Updatable
{
    public static final int FIELD_PLAYERS_SUPPORTED = 8;

    private static final SoccerConstants IMAGE_GOAL_OPEN_FROM;
    private static final BufferedImage IMAGE_GOAL;

    private static Field instance = null;

    private int fieldWidth, fieldHeight;
    private int fieldX, fieldY;
    private AffineTransform leftGoalTransform, rightGoalTransform;
    private int[][] defaultLeftPositions, defaultRightPositions;
    private Shape[] lines, spots;

    private Rectangle2D leftGoal, rightGoal;

    static {
        IMAGE_GOAL_OPEN_FROM = SoccerConstants.SOUTH;
        IMAGE_GOAL = util.Image.get("goal.gif");
    }

    private Field()
    {
        // Voor elke speler een x- en y-positie.
        this.defaultLeftPositions = new int[FIELD_PLAYERS_SUPPORTED/2][2];
        this.defaultRightPositions = new int[FIELD_PLAYERS_SUPPORTED/2][2];
    }

    public static Field getInstance()
    {
        if (instance == null)
            instance = new Field();

        return instance;
    }

    public int[][] getDefaultPositions(SoccerConstants side)
    {
        switch (side) {
            case EAST:
                return this.defaultRightPositions;
            case WEST:
                return this.defaultLeftPositions;
            default:
                return null;
        }
    }

    /**
     * Verkrijg de hoogte van het doel. De hoogte is groter dan de breedte bij voetbal.
     *
     * @return de hoogte van het doel, gelet op de oriëntatie van de originele afbeelding
     */
    private static float getGoalImageWidth()
    {
        switch (IMAGE_GOAL_OPEN_FROM) {
            case NORTH:
            case SOUTH:
                return IMAGE_GOAL.getHeight();
            case EAST:
            case WEST:
                return IMAGE_GOAL.getWidth();
            default:
                return -1;
        }
    }

    /**
     * Verkrijg de breedte van het doel. De hoogte is groter dan de breedte bij voetbal.
     *
     * @return de breedte van het doel, gelet op de oriëntatie van de originele afbeelding
     */
    private static float getGoalImageHeight()
    {
        switch (IMAGE_GOAL_OPEN_FROM) {
            case NORTH:
            case SOUTH:
                return IMAGE_GOAL.getWidth();
            case EAST:
            case WEST:
                return IMAGE_GOAL.getHeight();
            default:
                return -1;
        }
    }

    /**
     * Verkrijg de hoek voor het draaien van een doel.
     *
     * @param fieldSide {@link SoccerConstants#WEST} of {@link SoccerConstants#EAST}
     * @return hoek in radialen
     */
    private static double getGoalRotation(SoccerConstants fieldSide)
    {
        double theta;

        // Alles richting het noorden draaien.
        switch (IMAGE_GOAL_OPEN_FROM) {
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

    public int getFieldTop()
    {
        return this.fieldY;
    }

    public int getFieldBot()
    {
        return this.fieldY + this.fieldHeight;
    }

    public int getFieldRight()
    {
        return this.fieldX + this.fieldWidth;
    }

    public int getFieldLeft()
    {
        return fieldX;
    }

    public Rectangle2D getLeftGoal()
    {
        return leftGoal;
    }

    public Rectangle2D getRightGoal()
    {
        return rightGoal;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

        for (Shape s : lines)
            g2d.draw(s);

        for (Shape s : spots)
            g2d.fill(s);

        // Tekent doelen.
        g2d.drawImage(IMAGE_GOAL, this.leftGoalTransform, null);
        g2d.drawImage(IMAGE_GOAL, this.rightGoalTransform, null);
    }

    @Override public void update(final SoccerPanel parent)
    {
        // Beïnvloeden veldgrootte.
        final int horizontalScale = parent.getWidth()/120; // Factor voor horizontale afmetingen.
        final int verticalScale = parent.getHeight()/90; // Factor voor verticale afmetingen.
        this.fieldX = parent.getWidth()/10;
        this.fieldY = parent.getHeight()/10;

        // Veldgrootte.
        this.fieldWidth = parent.getWidth() - fieldX*2;
        this.fieldHeight = parent.getHeight() - fieldY*2;

        // Startpunten.
        final int centerX = parent.getWidth()/2;
        final int centerY = parent.getHeight()/2;

        // Maten van veldonderdelen.
        final int centerCircleSize = (int)(horizontalScale*9.15*2);
        final int centerSpotSize = centerCircleSize/16;
        final int penaltyAreaWidth = (int)(horizontalScale*16.5*.8), penaltyAreaHeight = (int)(verticalScale*40.3);
        final int goalAreaWidth = (int)(horizontalScale*5.5*.9), goalAreaHeight = (int)(verticalScale*(18.3));

        // Maten van goals.
        final int goalWidth = (int)(goalAreaWidth*.85);
        final int goalHeight = (int)(goalAreaHeight*.95);
        final float goalWidthScale = goalWidth/getGoalImageWidth();
        final float goalHeightScale = goalHeight/getGoalImageHeight();

        // Transformatie linker doel.
        this.leftGoalTransform = new AffineTransform();
        this.leftGoalTransform.translate(fieldX - goalWidth, centerY + goalHeight/2);
        this.leftGoalTransform.scale(goalWidthScale, goalHeightScale);
        this.leftGoalTransform.rotate(getGoalRotation(SoccerConstants.WEST));

        // Transformatie rechter doel.
        this.rightGoalTransform = new AffineTransform();
        this.rightGoalTransform.translate(fieldX + this.fieldWidth + goalWidth, centerY - goalHeight/2);
        this.rightGoalTransform.scale(goalWidthScale, goalHeightScale);
        this.rightGoalTransform.rotate(getGoalRotation(SoccerConstants.EAST));

        // Tekent veldgrenzen.
        final Rectangle2D fieldRect = new Rectangle2D.Double(fieldX, fieldY, this.fieldWidth, this.fieldHeight);

        // Midden veldonderdelen.
        final Line2D centerLine = new Line2D.Double(centerX, fieldY, centerX, fieldY + this.fieldHeight);
        final Ellipse2D centerCircle = new Ellipse2D.Double(centerX - centerCircleSize/2, centerY - centerCircleSize/2, centerCircleSize, centerCircleSize);
        final Ellipse2D centerSpot = new Ellipse2D.Double(centerX - centerSpotSize/2, centerY - centerSpotSize/2, centerSpotSize, centerSpotSize);

        // Goal veldonderdelen.
        final Rectangle2D leftPenaltyArea = new Rectangle.Double(fieldX, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        final Rectangle2D leftGoalArea = new Rectangle.Double(fieldX, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);
        final Rectangle2D rightPenaltyArea = new Rectangle.Double(fieldX + this.fieldWidth - penaltyAreaWidth, centerY - penaltyAreaHeight/2, penaltyAreaWidth, penaltyAreaHeight);
        final Rectangle2D rightGoalArea = new Rectangle.Double(fieldX + this.fieldWidth - goalAreaWidth, centerY - goalAreaHeight/2, goalAreaWidth, goalAreaHeight);
        this.leftGoal = new Rectangle.Double(fieldX - goalWidth, centerY - goalHeight/2, goalWidth, goalHeight);
        this.rightGoal = new Rectangle.Double(fieldX + this.fieldWidth, centerY - goalHeight/2, goalWidth, goalHeight);

        // Bereken hulpwaarden voor standaardposities.
        final int positionsPerSide = FIELD_PLAYERS_SUPPORTED/2;
        final int playerOffset = Player.SIZE/2;
        final int posXSpacing = (this.fieldWidth/2)/(positionsPerSide + 1);
        final int posYSpacing = (this.fieldHeight/2)/2;
        final int posXLeftFirst = fieldX + posXSpacing*2;
        final int posXRightFirst = fieldX + this.fieldWidth - posXSpacing*2;
        final int posYUpperHalf = centerY - posYSpacing - playerOffset;
        final int posYLowerHalf = centerY + posYSpacing - playerOffset;

        // Bereken linkse posities.
        this.defaultLeftPositions[0] = new int[] {posXLeftFirst, centerY - playerOffset};
        this.defaultLeftPositions[1] = new int[] {centerX - posXSpacing - playerOffset*2, centerY - playerOffset};
        this.defaultLeftPositions[2] = new int[] {posXLeftFirst + posXSpacing, posYUpperHalf};
        this.defaultLeftPositions[3] = new int[] {posXLeftFirst + posXSpacing, posYLowerHalf};

        // Bereken rechtse posities.
        this.defaultRightPositions[0] = new int[] {posXRightFirst - playerOffset*2, centerY - playerOffset};
        this.defaultRightPositions[1] = new int[] {centerX + posXSpacing, centerY - playerOffset};
        this.defaultRightPositions[2] = new int[] {posXRightFirst - posXSpacing, posYUpperHalf};
        this.defaultRightPositions[3] = new int[] {posXRightFirst - posXSpacing, posYLowerHalf};

        // Lines worden getekend en spots worden gevuld.
        this.lines = new Shape[] {fieldRect, centerCircle, centerLine, leftPenaltyArea, rightPenaltyArea, leftGoalArea, rightGoalArea};
        this.spots = new Shape[] {centerSpot};
    }

    @Override public int getX()
    {
        return this.fieldX;
    }

    @Override public int getY()
    {
        return this.fieldY;
    }

    @Override public int getWidth()
    {
        return this.fieldWidth;
    }

    @Override public int getHeight()
    {
        return this.fieldHeight;
    }
}
