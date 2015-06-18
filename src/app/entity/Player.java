package app.entity;

import app.SoccerConstants;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Een tekenbare speler voor op het voetbalveld.
 */
public class Player implements Drawable {
    public static final int SIZE = 20;
    public static final String TITLE_DEFAULT = "CPU";

    private String title;
    private final SoccerConstants side;
    private int x = 0, y = 0;
    private double[] dxdy;
    private boolean isControlled;

    public Ellipse2D ellipsePlayer;

    Field field = Field.getInstance();

    public Player(SoccerConstants side) {
        this.side = side;
        this.title = TITLE_DEFAULT;
        this.isControlled = false;
        this.dxdy = new double[]{0, 0};
        ellipsePlayer = new Ellipse2D.Double(this.x, this.y, 20, 20);
    }

    public void setPosition(int x, int y) {
        if (x != 0 && y != 0 && field.getX() < x && field.getY() < y && field.getWidth() + field.getX() - 20 > x && field.getHeight() + field.getY() - 20 > y) {
            this.x = x;
            this.y = y;
            ellipsePlayer = new Ellipse2D.Double(this.x, this.y, 20, 20);
        }

    }

    public void setMovement(double[] dxdy) {
        double[] doubleArray = {0, 0};
        if (!dxdy.equals(doubleArray)) {
            this.dxdy = dxdy;
            ellipsePlayer = new Ellipse2D.Double(this.x, this.y, 20, 20);
        }
    }

    public double[] getMovement() {
        return this.dxdy;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setControlled(Boolean isControlled) {
        this.isControlled = isControlled;
    }

    public boolean isControlled() {
        return this.isControlled;
    }

    public SoccerConstants getSide() {
        return this.side;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setPaint(side.equals(SoccerConstants.EAST) ? Color.red : Color.blue);
        g2d.fill(ellipsePlayer);
        g2d.drawString(title.trim(), this.x + 10, this.y - 2);
    }

    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }
}
