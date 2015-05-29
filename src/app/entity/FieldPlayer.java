package app.entity;

public class FieldPlayer
{
    private int x, y;
    private boolean isControlled;

    public FieldPlayer(int x, int y)
    {
        this.move(x, y);
        this.isControlled = false;
    }

    public void move(int dx, int dy)
    {
        this.x += dx;
        this.y += dy;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public boolean isControlled()
    {
        return this.isControlled;
    }

    public void toggleControl()
    {
        this.isControlled = !isControlled;
    }
}
