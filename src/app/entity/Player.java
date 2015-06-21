package app.entity;

import app.SoccerConstants;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * Een tekenbare speler voor op het voetbalveld.
 */
public class Player implements Drawable
{
    public static final int SIZE = 30;
    public static final String TITLE_DEFAULT = "CPU";
    private static final BufferedImage playerImage;
    public static  final int SPRITE_HEIGHT = 105;
    public static  final int SPRITE_WIDTH = 70;



    private double angle;

    private Field field = Field.getInstance();
    private String title;
    private final SoccerConstants side;
    private int x = 0, y = 0;
    private double x1 = 0;
    private double[] dxdy;
    private boolean isControlled;
    private Ellipse2D.Double playerEllipse;

    private int count;
    private int imgCount;
    private BufferedImage[] sprites = new BufferedImage[6];

    boolean moving = false;
    boolean moving1 = false;
    boolean movingLeft = false;
    boolean moved = false;

    static {
        playerImage = util.Image.get("sprite_player_brazil.png");
    }

    public Player(SoccerConstants side)
    {
        this.side = side;
        this.title = TITLE_DEFAULT;
        this.isControlled = false;
        this.dxdy = new double[] {0, 0};
        sprites[0] = playerImage.getSubimage(0, 105, 70, 105);
        sprites[1] = playerImage.getSubimage(70, 105, 70, 105);
        sprites[2] = playerImage.getSubimage(140, 105, 70, 105);
        sprites[3] = playerImage.getSubimage(0, 300, 70, 105);
        sprites[4] = playerImage.getSubimage(70, 300, 70, 105);
        sprites[5] = playerImage.getSubimage(140, 300, 70, 105);
    }

    BufferedImage playerSprite()
    {
        if (!moving) {
            imgCount = 1;
        } else {
            if (imgCount >= (3 - 1)) {
                imgCount = 0;
                return sprites[imgCount];
            } else {
                if (count%6 == 0)
                    imgCount++;
            }
        }

        return sprites[imgCount];
    }

    public BufferedImage playerSpriteLeft()
    {
        if (imgCount >= 5) {
            imgCount = 3;
        } else {
            if (count%6 == 0)
                imgCount++;
        }

        return sprites[imgCount];
    }

    public void setPosition(int x, int y)
    {
        if (x != 0 && y != 0 && field.getX() < x-20 && field.getY() < y && field.getWidth() + field.getX() - SIZE+40 > x && field.getHeight() + field.getY() - SIZE > y) {
            this.x = x;
            this.y = y;
            playerEllipse = new Ellipse2D.Double(this.x - (SIZE / 2), this.y, SIZE, SIZE);
        }
    }

    public void setMovement(double[] dxdy)
    {
        double[] doubleArray = {0, 0};
        if (!dxdy.equals(doubleArray))
            this.dxdy = dxdy;
        x1 = dxdy[1];
        if (dxdy[0] > 0.2 || dxdy[1] > 0.2)
            moving = true;
        else if (dxdy[0] < -0.2)
            movingLeft = true;
        else {
            moving = false;
            movingLeft = false;
        }
    }

    public double[] getMovement()
    {
        return this.dxdy;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setControlled(Boolean isControlled)
    {
        this.isControlled = isControlled;
    }

    public boolean isControlled()
    {
        return this.isControlled;
    }

    public SoccerConstants getSide()
    {
        return this.side;
    }

    @Override public void draw(Graphics2D g2d)
    {
        g2d.setPaint(side.equals(SoccerConstants.EAST) ? Color.red : Color.blue);

        if (moving)
            moving1 = true;

        if (!moving1 && side.equals(SoccerConstants.WEST))
            g2d.drawImage(sprites[1], this.x - 70, this.y - 75, null);
        else if (!moving1 && side.equals(SoccerConstants.EAST))
            g2d.drawImage(sprites[4], this.x, this.y - 75, null);

        else if (moved && !moving && !movingLeft && side.equals(SoccerConstants.EAST))
            g2d.drawImage(sprites[1], this.x - 20, this.y - 75, null);
        else if (!moved && !moving && !movingLeft && side.equals(SoccerConstants.EAST))
            g2d.drawImage(sprites[4], this.x, this.y - 75, null);

        else if (moved && !moving && !movingLeft && side.equals(SoccerConstants.WEST))
            g2d.drawImage(sprites[1], this.x - 70, this.y - 75, null);
        else if (!moved && !moving && !movingLeft && side.equals(SoccerConstants.WEST))
            g2d.drawImage(sprites[4], this.x - 70, this.y - 75, null);

        else if (side.equals(SoccerConstants.WEST))
            if (!movingLeft) {
                g2d.drawImage(playerSprite(), this.x - 70, this.y - 75, null);
                moved = true;
            } else {
                g2d.drawImage(playerSpriteLeft(), this.x - 60, this.y - 75, null);
                moved = false;
            }
        else {
            if (!movingLeft) {
                g2d.drawImage(playerSprite(), this.x - 20, this.y - 75, null);
                moved = true;
            } else {
                g2d.drawImage(playerSpriteLeft(), this.x, this.y - 75, null);
                moved = false;
            }
        }

        if (side.equals(SoccerConstants.WEST))
            g2d.drawString(title.trim(), this.x, this.y - 2);
        else
            g2d.drawString(title.trim(), this.x + 55, this.y - 2);

        count++;
    }

    @Override public int getWidth()
    {
        return SIZE;
    }

    @Override public int getHeight()
    {
        return SIZE;
    }

    public double getAngle()
    {
        return angle;
    }

    public void setAngle(double angle)
    {
        this.angle = angle;
    }

    public Ellipse2D getEllipse()
    {
        return this.playerEllipse;
    }
}
