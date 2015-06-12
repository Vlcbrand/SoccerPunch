package app.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TitleScreenFrame extends JFrame
{
    private final GraphicsDevice device;
    public JPanel tp = new TitlePanel(this);

    public static void main(String[] args)
    {
        new TitleScreenFrame().setVisible(true);
    }

    public TitleScreenFrame()
    {
        super("TitleMenu");

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        JRootPane rootPane = this.getRootPane();
        rootPane.registerKeyboardAction(e -> this.leaveFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(e -> this.enterFullScreenMode(), KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(tp);
        this.pack();

        this.enterFullScreenMode();
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

    public void leaveFullScreenMode()
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

    public void options()
    {
        ((TitlePanel)(this.tp)).stopMotionListening();
        this.setContentPane(new OptionsPanel(this));
        this.setVisible(true);
    }

    public void back()
    {
        this.remove(tp);
        this.setContentPane(new TitlePanel(this));
        this.setVisible(true);
    }
}
