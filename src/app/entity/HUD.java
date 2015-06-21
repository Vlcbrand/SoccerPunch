package app.entity;

import app.SoccerConstants;
import app.SoccerModel;
import app.SoccerPanel;
import util.*;
import util.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Heads-up display voor op een {@link SoccerPanel}.
 * HUD is een singleton, dus gerbruik {@link #getInstance()}.
 */
public class HUD implements Drawable, Updatable
{
    private final static BufferedImage BANNER_IMAGE_WEST, BANNER_IMAGE_EAST;
    private final static int OFFSET_x, OFFSET_Y;
    private final static int SCORE_MARGIN_HORIZONTAL;
    private final static int BANNER_MARGIN_TOP;
    private final static double BANNER_IMAGE_SCALE;
    private final static boolean SHOW_FPS;

    private static HUD instance = null;

    private int parentWidth, parentHeight;
    private boolean hasSuze;
    private String leftTeamScore, rightTeamScore;
    private int fps;

    static {
        BANNER_IMAGE_WEST = Image.get("teambanner_blue.png");
        BANNER_IMAGE_EAST = Image.get("teambanner_red.png");

        OFFSET_x = OFFSET_Y = 10;
        SCORE_MARGIN_HORIZONTAL = 15;
        BANNER_MARGIN_TOP = 20;
        BANNER_IMAGE_SCALE = .2;

        SHOW_FPS = Resource.getBoolean("bool.show_fps");
    }

    private HUD()
    {
        this.hasSuze = false;
    }

    public static HUD getInstance()
    {
        if (instance == null)
            instance = new HUD();

        return instance;
    }

    @Override public void update(final SoccerPanel parent)
    {
        if (!hasSuze) {
            this.parentWidth = parent.getWidth();
            this.parentHeight = parent.getHeight();
            this.hasSuze = true;
        }

        final SoccerModel activeModel = parent.getActiveModel();

        this.fps = activeModel.getFramesPerSecond();
        this.leftTeamScore = Integer.toString(activeModel.getScore(SoccerConstants.WEST));
        this.rightTeamScore = Integer.toString(activeModel.getScore(SoccerConstants.EAST));
    }

    @Override public void draw(Graphics2D g2d)
    {
        final Font originalFont = g2d.getFont();
        final Stroke originalStroke = g2d.getStroke();
        final Paint originalColor = g2d.getPaint();

        // Hulpafmetingen.
        final int centerY = this.parentHeight/2;
        final int centerX = this.parentWidth/2;

        // Tekent FPS-meter.
        if (SHOW_FPS) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            final String fps = "FPS " + this.fps;
            g2d.drawString(fps, OFFSET_x, Text.Integer.getHeight(g2d, fps) + OFFSET_Y);
        }

        // Tekent scores.
        g2d.setFont(new Font("Arial Black", Font.BOLD + Font.ITALIC, 32));
        g2d.setColor(Color.darkGray);
        g2d.setStroke(new BasicStroke(7));
        final int leftTeamScoreWidth = Text.Integer.getWidth(g2d, leftTeamScore);
        final int scoreHeight = Text.Integer.getHeight(g2d, rightTeamScore);
        g2d.drawString(this.leftTeamScore, centerX - leftTeamScoreWidth - SCORE_MARGIN_HORIZONTAL, scoreHeight + OFFSET_Y);
        g2d.drawString(this.rightTeamScore, centerX + SCORE_MARGIN_HORIZONTAL, scoreHeight + OFFSET_Y);
        g2d.drawLine(centerX - SCORE_MARGIN_HORIZONTAL/2, OFFSET_Y + scoreHeight/2, centerX + SCORE_MARGIN_HORIZONTAL/2, OFFSET_Y + scoreHeight/2);

        // Banners voorbereiden.
        final int bannerWidth = (int)(BANNER_IMAGE_WEST.getWidth()*BANNER_IMAGE_SCALE);
        final int bannerHeight = (int)(BANNER_IMAGE_WEST.getHeight()*BANNER_IMAGE_SCALE);

        // Banners tekenen.
        g2d.drawImage(BANNER_IMAGE_WEST, OFFSET_x, OFFSET_Y + BANNER_MARGIN_TOP, bannerWidth, bannerHeight, null);
        g2d.drawImage(BANNER_IMAGE_EAST, this.parentWidth - OFFSET_x - bannerWidth, OFFSET_Y + BANNER_MARGIN_TOP, bannerWidth, bannerHeight, null);

        // Herstellen.
        g2d.setFont(originalFont);
        g2d.setStroke(originalStroke);
        g2d.setPaint(originalColor);
    }

    @Override public int getX()
    {
        return OFFSET_x;
    }

    @Override public int getY()
    {
        return OFFSET_Y;
    }

    @Override public int getWidth()
    {
        return this.parentWidth - OFFSET_x*2;
    }

    @Override public int getHeight()
    {
        return this.parentHeight - OFFSET_Y*2;
    }
}
