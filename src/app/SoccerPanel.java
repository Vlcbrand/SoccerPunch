package app;

import app.entity.Field;

import javax.swing.*;
import java.awt.*;


public class SoccerPanel extends JPanel
{
    Field field = new Field(250,50 ,1200,900);
    public SoccerPanel()
    {
        setPreferredSize(new Dimension(1700,1000));
        setBackground(Color.green);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.white);
        field.draw(g2);
    }

}
