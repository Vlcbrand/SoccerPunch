package app;

import javax.swing.*;
import java.awt.*;

/**
 * Hoofdscherm van het spel.
 */
class SoccerFrame extends JFrame
{
    private final GraphicsDevice device;

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

    SoccerFrame()
    {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 0));
        this.setMinimumSize(this.getMinimumSize());
        this.setSize(this.getMinimumSize());
        this.setLocationRelativeTo(null);

        this.enterFullScreenMode(); // Test.
    }

    public void refresh()
    {
    }

    private void enterFullScreenMode()
    {
        if (device.getFullScreenWindow() == null) {
            this.dispose();
            this.setUndecorated(true);
            this.setResizable(false);

            device.setFullScreenWindow(this);
        }
    }

    private void leaveFullScreenMode()
    {
        if (device.getFullScreenWindow() != null) {
            this.setVisible(false);
            this.dispose();
            this.setUndecorated(false);

            device.setFullScreenWindow(null);

            this.setResizable(true);
            this.setVisible(true);

            this.repaint();
        }
    }

    @Override public Dimension getMinimumSize()
    {
        return new Dimension(800, 600);
    }
}
