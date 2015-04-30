package irpad;

import wiiusej.values.IRSource;
import wiiusej.wiiusejevents.physicalevents.IREvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

class WiiMoteView extends JPanel implements ActionListener
{
    private IRSource[] irs = null;
    private Timer render;
    private GeneralPath path = new GeneralPath();

    public WiiMoteView()
    {
        setPreferredSize(new Dimension(1024, 768));

        // setup render interval
        render = new Timer(100, this);
        render.start();
    }

    // Called by
    public void update(IREvent arg0)
    {
        irs = arg0.getIRPoints();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Teken assenstelsel
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g2.translate(1024 / 2, 768 / 2);
        g2.drawLine(-200, 0, 200, 0);    // y-as
        g2.drawLine(0, -200, 0, 200);    // x-as

        g2.setColor(Color.RED);
        g2.draw(path);
    }

    public void actionPerformed(ActionEvent arg0)
    {
        // calc hoek
        if (irs != null) {
            if (irs.length == 4) {
                //Draw Pad
                path.reset();
                path = new GeneralPath();
                path.moveTo(irs[0].getX() / 4, irs[0].getY() / 4);
                path.lineTo(irs[1].getX() / 4, irs[1].getY() / 4);
                path.lineTo(irs[2].getX() / 4, irs[2].getY() / 4);
                path.lineTo(irs[3].getX() / 4, irs[3].getY() / 4);
                path.closePath();
            }
        }

        // Update every 100ms
        repaint();
    }
}
