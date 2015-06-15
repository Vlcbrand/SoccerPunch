package app.menu;

import app.SoccerConstants;
import app.SoccerFrame;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class TitlePanel extends JPanel implements WiimoteListener
{
    BufferedImage background;
    Color fColor = Color.BLACK;
    float alpha = 0.5f;
    Color color = new Color(1, 1, 1, alpha);
    Rectangle2D rect, pg, go, eg;
    public int menuNumber = 0;
    TitleScreenFrame frame;
    Wiimote wiimote;

    static final BufferedImage redBanner;
    static final BufferedImage blueBanner;


    static {
        redBanner = util.Image.get("img/teambanner_red.png");
        blueBanner = util.Image.get("img/teambanner_blue.png");
    }

    public TitlePanel(TitleScreenFrame frame)
    {
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        background = util.Image.get("grass_texture.jpg");

        Wiimote[] wiimotes = WiiUseApiManager.getWiimotes(1, true);
        if (wiimotes != null) {
            Wiimote wiimote = wiimotes[0];
            this.wiimote = wiimote;
            wiimote.setLeds(true, false, false, false);
        }

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
        return menuNumber;
    }

    public int setMenuNumberDown()
    {
        if (menuNumber <= 2 && menuNumber > 0)
            menuNumber--;
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


        rect = new Rectangle2D.Double(getWidth()/2 - 330, 0, 660, getHeight());
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

        //  draw rechterbanner
        AffineTransform tx = new AffineTransform();
        tx.translate(getWidth()/2 + 350, getHeight()/-redBanner.getHeight());
        g2.drawImage(redBanner, tx, this);

        //  draw linkerbanner
        AffineTransform txx = new AffineTransform();
        txx.translate(getWidth()/2 -blueBanner.getWidth()-350, getHeight()/-blueBanner.getHeight());
        g2.drawImage(blueBanner, txx, this);

        g2.setFont(font1);
        g2.drawString("SoccerPunch!", setWidthString("SoccerPunch!", getWidth(), g2), getHeight()/2 - 200);
    }

    public int setWidthString(String s, int width, Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(s))/2 + 2;
        return x;
    }

    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        if (menuNumber == 0)
            g2.draw(pg);
        if (menuNumber == 1)
            g2.draw(go);
        if (menuNumber == 2)
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
        if (e.isButtonAPressed()) {
            if (menuNumber == 0) {
                TitleScreenFrame title = new TitleScreenFrame();
                title.leaveFullScreenMode();
                new SoccerFrame();
            }
            if (menuNumber == 1)
                frame.options();
            if (menuNumber == 2) {
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
