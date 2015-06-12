package app;

import app.wii.WiimoteAdapter;
import app.wii.WiimoteButton;
import wiiusej.WiiUseApiManager;
import wiiusej.Wiimote;
import wiiusej.wiiusejevents.physicalevents.ExpansionEvent;
import wiiusej.wiiusejevents.physicalevents.IREvent;
import wiiusej.wiiusejevents.physicalevents.MotionSensingEvent;
import wiiusej.wiiusejevents.physicalevents.WiimoteButtonsEvent;
import wiiusej.wiiusejevents.utils.WiimoteListener;
import wiiusej.wiiusejevents.wiiuseapievents.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by frits on 4-6-15.
 */
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
        rootPane.registerKeyboardAction(e -> this.options(), KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(e -> this.back(), KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


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

class TitlePanel extends JPanel implements WiimoteListener
{

    BufferedImage background;
    Color fColor = Color.BLACK;
    float alpha = 0.5f;
    Color color = new Color(1, 1, 1, alpha);
    TitleScreenFrame frame;

    public int menuNumber = 0;

    Rectangle2D rect, pg, go, eg;

    Wiimote wiimote;


    public TitlePanel(TitleScreenFrame frame)
    {
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.GREEN);
        background = util.Image.get("grass_texture3.jpg");

        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, true);
        if (wiimotes != null) {
            Wiimote wiimote = wiimotes[0];
            this.wiimote = wiimote;
            wiimote.setLeds(true, false, false, false);
        }

        addMouseListener(new MouseAdapter()
        {
            @Override public void mouseClicked(MouseEvent e)
            {
                super.mousePressed(e);
                if (e.getX() > pg.getX() && e.getX() < pg.getX() + 380 && e.getY() > pg.getY() && e.getY() < pg.getY() + 100) {
                    fColor = Color.blue;

                    TitleScreenFrame title = new TitleScreenFrame();
                    title.leaveFullScreenMode();

                    new GameMain();
                } else if (e.getX() > go.getX() && e.getX() < go.getX() + 500 && e.getY() > go.getY() && e.getY() < go.getY() + 100) {
                    fColor = Color.cyan;
                    TitleScreenFrame title = new TitleScreenFrame();
                    title.options();
                    repaint();
                } else if (e.getX() > eg.getX() && e.getX() < eg.getX() + 380 && e.getY() > eg.getY() && e.getY() < eg.getY() + 100) {
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Warning", dialogButton) == JOptionPane.YES_OPTION)
                        System.exit(0);
                } else
                    fColor = Color.BLACK;
                repaint();

            }
        });

        wiimotes[0].addWiiMoteEventListeners(this);
    }

    public void stopMotionListening()
    {
        this.wiimote.removeWiiMoteEventListeners(this);
    }

    public int setMenuNumberUp()
    {
        if (menuNumber < 2)
            menuNumber++;
        System.out.println(menuNumber);
        return menuNumber;
    }

    public int setMenuNumberDown()
    {
        if (menuNumber <= 2 && menuNumber >0)
            menuNumber--;
        System.out.println(menuNumber);
        return menuNumber;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        Font font = new Font("SansSerif", Font.PLAIN, 70);
        Font font1 = new Font("Impact", Font.ITALIC, 100);
        g2.setFont(font);


        g2.drawImage(background, -100, -100, getWidth() + 200, getHeight() + 200, null);


        rect = new Rectangle2D.Double(getWidth()/2 - 330, 0, 680, getHeight());
        pg = new Rectangle2D.Double(setWidthString("Play Game", getWidth(), g2), getHeight()/2 - 80, 380, 100);
        go = new Rectangle2D.Double(setWidthString("Game Options", getWidth(), g2), getHeight()/2 + 20, 500, 100);
        eg = new Rectangle2D.Double(setWidthString("Exit Game", getWidth(), g2), getHeight()/2 + 120, 380, 100);


        g2.draw(rect);
        g2.setPaint(color);
        g2.fill(rect);

        g2.setColor(fColor);

        draw(g2);

        g2.drawString("Play Game", setWidthString("Play Game", getWidth(), g2), getHeight()/2 - 00);
        g2.drawString("Game Options", setWidthString("Game Options", getWidth(), g2), getHeight()/2 + 100);
        g2.drawString("Exit Game", setWidthString("Exit Game", getWidth(), g2), getHeight()/2 + 200);


        g2.setFont(font1);
        g2.drawString("SoccerPunch!", setWidthString("SoccerPunch!", getWidth(), g2), getHeight()/2 - 200);


    }

    public int setWidthString(String s, int width, Graphics g)
    {

        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(s))/2 + 2;
        return x;
    }

    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        if(menuNumber == 0)
            g2.draw(pg);
        if(menuNumber ==1)
            g2.draw(go);
        if(menuNumber == 2)
            g2.draw(eg);
        repaint();
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
        if (e.isButtonDownPressed()) {
            setMenuNumberUp();
        }

        if (e.isButtonUpPressed())
            setMenuNumberDown();

        if(e.isButtonAPressed()){
            if(menuNumber ==0){
                TitleScreenFrame title = new TitleScreenFrame();
                title.leaveFullScreenMode();

                new GameMain();
            }
            if(menuNumber == 1)
                frame.options();
            if(menuNumber == 2){
                int dialogButton = JOptionPane.YES_NO_OPTION;
                    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Warning", dialogButton) == JOptionPane.YES_OPTION)
                        System.exit(0);

            }


        }
    }

    @Override public void onIrEvent(IREvent irEvent)
    {

    }

    @Override public void onMotionSensingEvent(MotionSensingEvent motionSensingEvent)
    {

    }

    @Override public void onExpansionEvent(ExpansionEvent expansionEvent)
    {

    }

    @Override public void onStatusEvent(StatusEvent statusEvent)
    {

    }

    @Override public void onDisconnectionEvent(DisconnectionEvent disconnectionEvent)
    {

    }

    @Override public void onNunchukInsertedEvent(NunchukInsertedEvent nunchukInsertedEvent)
    {

    }

    @Override public void onNunchukRemovedEvent(NunchukRemovedEvent nunchukRemovedEvent)
    {

    }

    @Override public void onGuitarHeroInsertedEvent(GuitarHeroInsertedEvent guitarHeroInsertedEvent)
    {

    }

    @Override public void onGuitarHeroRemovedEvent(GuitarHeroRemovedEvent guitarHeroRemovedEvent)
    {

    }

    @Override
    public void onClassicControllerInsertedEvent(ClassicControllerInsertedEvent classicControllerInsertedEvent)
    {

    }

    @Override public void onClassicControllerRemovedEvent(ClassicControllerRemovedEvent classicControllerRemovedEvent)
    {

    }
}

