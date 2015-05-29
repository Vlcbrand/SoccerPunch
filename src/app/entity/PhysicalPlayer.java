package app.entity;

public class PhysicalPlayer
{
    private int x, y;
    private int currentPlayer;
    private int currentCharacter;

    public PhysicalPlayer(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.currentPlayer = -1;
    }

    public PhysicalPlayer(int x, int y, int currentPlayer)
    {
        this.x = x;
        this.y = y;
        this.currentPlayer = currentPlayer;
    }

    public void move(int dx, int dy)
    {
        this.x += dx;
        this.y += dy;
    }

    public void setControllerID(int id)
    {
        this.currentPlayer = id;
    }

    public void nextCharacter()
    {
        this.currentCharacter = this.currentCharacter < 4 ? currentCharacter += 1 : 0;
    }

    public int getCurrentCharacter()
    {
        return this.currentCharacter;
    }

    public int getCurrentPlayer()
    {
        return this.currentPlayer;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }
}
