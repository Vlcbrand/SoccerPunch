package app.menu;

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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by frits on 8-6-15.
 */
public class OptionsPanel extends JPanel implements WiimoteListener
{
    BufferedImage background;
    Rectangle2D rect;
    float alpha = 0.5f;
    Color color = new Color(1, 1, 1, alpha);
    private int min, pts, menuNumber1, x;
    private String time, points, choice, ch;
    Rectangle2D o1, o2, o3;
    TitleScreenFrame frame;
    Wiimote wiimote;


    public OptionsPanel(TitleScreenFrame frame)
    {
        x = 0;
        this.frame = frame;
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.GREEN);
        background = util.Image.get("grass_texture.jpg");
        menuNumber1 = 0;
        min = 6;
        pts = 6;
        ch = "Points and Time";
        choice = "Gamemode: " + ch;
        points = "Points to win: " + pts;
        time = "Playtime: " + min + " min";

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

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        Font font = new Font("SansSerif", Font.PLAIN, 40);
        Font font1 = new Font("Impact", Font.ITALIC, 100);
        Font font2 = new Font("SansSerif", Font.PLAIN, 40);

        g2.drawImage(background, -100, -100, getWidth() + 200, getHeight() + 200, null);

        rect = new Rectangle2D.Double(getWidth()/2 - 330, 0, 680, getHeight());
        g2.draw(rect);
        g2.setPaint(color);
        g2.fill(rect);

        o1 = new Rectangle2D.Double(setWidthString(time, getWidth(), g2) - 120, getHeight()/2 - 100, 350, 70);
        o2 = new Rectangle2D.Double(setWidthString(points, getWidth(), g2) - 120, getHeight()/2 - 30, 350, 70);
        o3 = new Rectangle2D.Double(setWidthString("Gamemode: Points and Time", getWidth(), g2) - 205, getHeight()/2 + 40, 595, 70);

        g2.setColor(Color.black);

        g2.setFont(font);

        g2.drawString(time, setWidthString(time, getWidth(), g2), getHeight()/2 - 50);
        g2.drawString(points, setWidthString(points, getWidth(), g2), getHeight()/2 + 20);
        g2.drawString(choice, setWidthString(choice, getWidth(), g2), getHeight()/2 + 90);
        draw(g2);


        g2.setFont(font2);

        g2.drawString("Press B to go back", setWidthString("Press B to go back", getWidth(), g2), getHeight() - 50);

        g2.setFont(font1);
        g2.drawString("SoccerPunch!", setWidthString("SoccerPunch!", getWidth(), g2), getHeight()/2 - 200);

    }

    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        time = "Playtime: " + min + " min";
        points = "Points to win: " + pts;
        choice = "Gamemode: " + ch;
        g2.drawString(time, setWidthString(time, getWidth(), g2), getHeight()/2 - 50);
        g2.drawString(points, setWidthString(points, getWidth(), g2), getHeight()/2 + 20);

        if (menuNumber1 == 0)
            g2.draw(o1);
        if (menuNumber1 == 1)
            g2.draw(o2);
        if (menuNumber1 == 2)
            g2.draw(o3);
        repaint();
    }

    public int setWidthString(String s, int width, Graphics g)
    {
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(s))/2 + 2;
        return x;
    }

    public int setMenuNumberUp()
    {
        if (menuNumber1 < 2)
            menuNumber1++;
        return menuNumber1;
    }

    public int setMenuNumberDown()
    {
        if (menuNumber1 <= 2 && menuNumber1 > 0)
            menuNumber1--;
        return menuNumber1;
    }

    @Override public void onButtonsEvent(WiimoteButtonsEvent e)
    {
        if (e.isButtonDownPressed()) {
            setMenuNumberUp();
        }

        if (e.isButtonUpPressed())
            setMenuNumberDown();

        if (e.isButtonBPressed())
            frame.back();

        if (e.isButtonRightPressed()) {
            if (menuNumber1 == 0) {
                min++;
            }
            if (menuNumber1 == 1)
                pts++;
            if (menuNumber1 == 2)
                if (x < 2)
                    x++;
                else
                    x = 0;
            switch (x) {
                case 0:
                    ch = "Time and Points";
                    break;
                case 1:
                    ch = "Time";
                    break;
                case 2:
                    ch = "Points";
                    break;
                default:
                    ch = "Swag";
                    break;
            }
        }
        if (e.isButtonLeftPressed()) {
            if (menuNumber1 == 0)
                min--;
            if (menuNumber1 == 1)
                pts--;
            if (menuNumber1 == 2) {
                if (x <= 2 && x > 0)
                    x--;
                else
                    x = 0;
                switch (x) {
                    case 0:
                        ch = "Time and Points";
                        break;
                    case 1:
                        ch = "Time";
                        break;
                    case 2:
                        ch = "Points";
                        break;
                    default:
                        ch = "Swag";
                        break;
                }
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
