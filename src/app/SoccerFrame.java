package app;

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
            UIManager.getLookAndFeelDefaults().put("Button.showMnemonics", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> new SoccerFrame().setVisible(true));
    }

    SoccerFrame()
    {
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // F11 voor Full Screen en ESC voor reguliere scherm.
        JRootPane rootPane = this.getRootPane();
        rootPane.registerKeyboardAction(e -> this.leaveFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(e -> this.enterFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 0));
        this.setMinimumSize(this.getMinimumSize());
        this.setLocationRelativeTo(null);

        this.setContentPane(new SoccerPanel());

        this.enterFullScreenMode(); // TEST.
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

    @Override public Dimension getMinimumSize()
    {
        return new Dimension(800, 600);
    }
}
