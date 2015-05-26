package app;

import app.entity.Field;
import app.entity.Goal;

import javax.swing.*;
import java.awt.*;


public class SoccerPanel extends JPanel
{
    Field field = new Field(50,50, 600,400);
    Goal goal = new Goal(250, 250, 0.5, 0.5, 0);

    public SoccerPanel()
    {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.green);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        field.draw(g2);
        goal.draw(g2);
    }

}
