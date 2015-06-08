package app;

import javax.swing.*;
import java.awt.*;

/**
 * Created by frits on 5-6-15.
 */
public class GameMain
{
    GameMain(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> new SoccerFrame().setVisible(true));
    }
}
