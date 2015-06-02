package app.entity;

import util.WiimoteButton;

import java.util.HashSet;
import java.util.Set;

public class PhysicalPlayer
{
    final int id;

    private FieldPlayer controlledPlayer;
    private Set<WiimoteButton> buttons;

    public PhysicalPlayer(int id)
    {
        this.id = id;

        this.buttons = new HashSet<>();
    }

    public void controlFieldPlayer(FieldPlayer player)
    {
        this.controlledPlayer = player;
    }

    public FieldPlayer getControlledPlayer()
    {
        return this.controlledPlayer;
    }

    public void addButton(WiimoteButton button)
    {
        buttons.add(button);
    }

    public void removeButton(WiimoteButton button)
    {
        buttons.remove(button);
    }
}
