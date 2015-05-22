package irpad;

import javax.swing.*;

public class WiiMoteJApp
{
    public static void main(final String...args)
    {
        JFrame frame = new JFrame("Wii Infrarood camera, tracking 4 dots");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WiiMoteView view = new WiiMoteView();
        WiiMoteModel model = new WiiMoteModel();
        WiiMoteController controller = new WiiMoteController(view, model);

        frame.getContentPane().add(view);
        frame.pack();
        frame.setVisible(true);
    }
}
