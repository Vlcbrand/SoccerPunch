package app.entity;

import app.SoccerConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Een tekenbaar veld voor op een paneel.
 * Field bezit twee doelen en overige veldonderdelen.
 */
public class Field implements Drawable
{
    public static final int FIELD_PLAYERS_SUPPORTED = 8;

    static final SoccerConstants goalImageOpenFrom;
    static final BufferedImage goalImage;

    private static Field instance = null;

    private AffineTransform leftGoalTransform, rightGoalTransform;

    private int width, height;
    private int fieldX, fieldY;
    private int[][] defaultLeftPositions, defaultRightPositions;
    private Shape[] lines, spots;

    static
    {
        goalImageOpenFrom = SoccerConstants.SOUTH;
        goalImage = util.Image.get("goal.gif");
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

    public void update(int parentWidth, int parentHeight)
    {
        // Beïnvloeden veldgrootte.
        final int horizontalScale = parentWidth/120; // Factor voor horizontale afmetingen.
        final int verticalScale = parentHeight/90; // Factor voor verticale afmetingen.
        this.fieldX = parentWidth/10;
        this.fieldY = parentHeight/10;

        // Veldgrootte.
        this.width = parentWidth - fieldX*2;
        this.height = parentHeight - fieldY*2;

        // Startpunten.
        final int centerX = parentWidth/2;
        final int centerY = parentHeight/2;

        // Maten van veldonderdelen.
        final int centerCircleSize = (int)(horizontalScale*9.15*2);
        final int centerSpotSize = centerCircleSize/16;
        final int penaltyAreaWidth = (int)(horizontalScale*16.5*.8), penaltyAreaHeight = (int)(verticalScale*40.3);
        final int goalAreaWidth = (int)(horizontalScale*5.5*.9), goalAreaHeight = (int)(verticalScale*(18.3));

        // Maten van goals.
        final int goalWidth = (int)(goalAreaWidth*.85);
        final int goalHeight = (int)(goalAreaHeight*.95);
        final float goalWidthScale = goalWidth / getGoalImageWidth();
        final float goalHeightScale = goalHeight / getGoalImageHeight();

        // Transformatie linker doel.
        leftGoalTransform = new AffineTransform();
        leftGoalTransform.translate(fieldX - goalWidth, centerY + goalHeight/2);
        leftGoalTransform.scale(goalWidthScale, goalHeightScale);
        leftGoalTransform.rotate(getGoalRotation(SoccerConstants.WEST));

        // Transformatie rechter doel.
        rightGoalTransform = new AffineTransform();
        rightGoalTransform.translate(fieldX + this.width + goalWidth, centerY - goalHeight/2);
        rightGoalTransform.scale(goalWidthScale, goalHeightScale);
        rightGoalTransform.rotate(getGoalRotation(SoccerConstants.EAST));

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

        // Bereken hulpwaarden voor standaardposities.
        final int positionsPerSide = FIELD_PLAYERS_SUPPORTED/2;
        final int playerOffset = Player.SIZE/2;
        final int posXSpacing = (this.width/2) / (positionsPerSide + 1);
        final int posYSpacing = (this.height/2) / 2;
        final int posXLeftFirst = fieldX + posXSpacing*2;
        final int posXRightFirst = fieldX + this.width - posXSpacing*2;
        final int posYUpperHalf = centerY - posYSpacing - playerOffset;
        final int posYLowerHalf = centerY + posYSpacing - playerOffset;

        // Bereken linkse posities.
        defaultLeftPositions[0] = new int[] {posXLeftFirst, centerY - playerOffset};
        defaultLeftPositions[1] = new int[] {centerX - posXSpacing - playerOffset*2, centerY - playerOffset};
        defaultLeftPositions[2] = new int[] {posXLeftFirst + posXSpacing, posYUpperHalf};
        defaultLeftPositions[3] = new int[] {posXLeftFirst + posXSpacing, posYLowerHalf};

        // Bereken rechtse posities.
        defaultRightPositions[0] = new int[] {posXRightFirst - playerOffset*2, centerY - playerOffset};
        defaultRightPositions[1] = new int[] {centerX + posXSpacing, centerY - playerOffset};
        defaultRightPositions[2] = new int[] {posXRightFirst - posXSpacing, posYUpperHalf};
        defaultRightPositions[3] = new int[] {posXRightFirst - posXSpacing, posYLowerHalf};

        // Lines worden getekend en spots worden gevuld.
        lines = new Shape[] {fieldRect, centerCircle, centerLine, leftPenaltyArea, rightPenaltyArea, leftGoalArea, rightGoalArea};
        spots = new Shape[] {centerSpot};
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
    public static float getGoalImageWidth()
    {
        switch (goalImageOpenFrom) {
            case NORTH:
            case SOUTH:
                return goalImage.getHeight();
            case EAST:
            case WEST:
                return goalImage.getWidth();
            default:
                return -1;
        }
    }

    /**
     * Verkrijg de breedte van het doel. De hoogte is groter dan de breedte bij voetbal.
     *
     * @return de breedte van het doel, gelet op de oriëntatie van de originele afbeelding
     */
    public static float getGoalImageHeight()
    {
        switch (goalImageOpenFrom) {
            case NORTH:
            case SOUTH:
                return goalImage.getWidth();
            case EAST:
            case WEST:
                return goalImage.getHeight();
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
        switch (goalImageOpenFrom) {
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

    @Override public void draw(Graphics2D g2d)
    {
        g2d.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));

        for (Shape s : lines)
            g2d.draw(s);

        for (Shape s : spots)
            g2d.fill(s);

        // Tekent doelen.
        g2d.drawImage(goalImage, leftGoalTransform, null);
        g2d.drawImage(goalImage, rightGoalTransform, null);
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
        return this.width;
    }

    @Override public int getHeight()
    {
        return this.height;
    }
}
