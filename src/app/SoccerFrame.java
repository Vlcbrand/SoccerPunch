package app;

import util.Resource;

import javax.swing.*;
import java.awt.*;

/**
 * Hoofdscherm van het spel.
 */
class SoccerFrame extends JFrame
{
    public static void main(final String... args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.getLookAndFeelDefaults().put("Button.showMnemonics", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> new SoccerFrame().setVisible(true));
    }

    public SoccerFrame()
    {
        final int minWidth = Resource.getInteger("app.width.min"), minHeight = Resource.getInteger("app.height.min");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 0));
        this.setMinimumSize(new Dimension(minWidth, minHeight));
        this.setLocationRelativeTo(null);
        this.setSize(minWidth, minHeight);

        JPanel panel1 = new SoccerPanel();
        this.getContentPane().add(panel1);
        this.pack();

    }

    public void refresh()
    {
    }
}
