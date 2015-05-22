package SoccerPunch;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by frits on 30-4-15.
 */
class SoccerFrame
{

    public static void main(String[] args)
    {
        new SoccerFrame();
    }

    public SoccerFrame()
    {
        JFrame frame = new JFrame("Voetbal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(new SoccerPanel());
        frame.pack();
        frame.setVisible(true);
    }

}

class SoccerPanel extends JPanel implements ActionListener
{
    private BufferedImage veld;
    private BufferedImage powerup;
    Player player;
    public boolean z = false;
    private final Set<Integer> keys = new HashSet<>();

    public SoccerPanel()
    {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        InputMap inputs = this.getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = this.getActionMap();
        //        inputs.put(KeyEvent.VK_UP, "LEFT-UP");
        //        actions.put("LEFT-UP", new AbstractAction() {
        //            @Override public void actionPerformed(ActionEvent actionEvent) {
        //                player.up();
        //            }
        //        });d


//        try
//        {
//            veld = ImageIO.read(new File("images/voetbalveld.jpg"));
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        if(player.x<100)
//        {
//
//
//            try
//            {
//                powerup = ImageIO.read(new File("images/Power_up.jpg"));
//            } catch(IOException e)
//            {
//                e.printStackTrace();
//            }
//        }

        player = new Player();

        this.addKeyListener(new KeyAdapter()
        {
            void move()
            {
                if (keys.size() > 3)
                    return;

                if (keys.contains(KeyEvent.VK_UP))
                    player.up();
                if (keys.contains(KeyEvent.VK_DOWN))
                    player.down();

                if (keys.contains(KeyEvent.VK_LEFT))
                    player.left();
                if (keys.contains(KeyEvent.VK_RIGHT))
                    player.right();
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                keys.add(e.getKeyCode());

                move();
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                keys.remove(e.getKeyCode());
                move();
            }
        });



        Timer timer = new Timer(1000 / 120, this);
        timer.start();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        g2.drawImage(veld, 0, 0, 800, 600, null);
        if(player.x<100)
        {
            g2.drawImage(powerup, 200, 200, 100, 100, null);
        }
        player.draw(g);
        g2.setColor(Color.white);
        //        g2.drawString("Hoi",50,50);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        repaint();
    }






}
