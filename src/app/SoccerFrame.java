package app;

import util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(SoccerFrame::new);
    }

    SoccerFrame()
    {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        this.setTitle(Resource.get().getString("string.frame.wait"));

        final SoccerModel model = new SoccerModel();
        final SoccerPanel panel = new SoccerPanel(model);

        JRootPane rootPane = this.getRootPane();
        rootPane.registerKeyboardAction(e -> this.leaveFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(e -> this.enterFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 0));
        this.setMinimumSize(panel.getMinimumSize());
        this.setLocationRelativeTo(null);
        this.setContentPane(panel);
        this.setVisible(true);

        new SoccerController(panel, model);

        this.setTitle("Soccer Punch!");
    }

    private void enterFullScreenMode()
    {
        if (device.getFullScreenWindow() == null) {
            this.dispose();
            this.setUndecorated(true);
            this.setResizable(false);

            device.setFullScreenWindow(this);

            this.repaint();
            this.requestFocus();
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
            this.requestFocus();
        }
    }
}
