package app.entity;

public class PhysicalPlayer
{
    final int id;

    FieldPlayer controlledPlayer;

    public PhysicalPlayer(int id)
    {
        this.id = id;
    }

    public void controlFieldPlayer(FieldPlayer player)
    {
        this.controlledPlayer = player;
    }

    public FieldPlayer getControlledPlayer()
    {
        return this.controlledPlayer;
    }
}
