package app;

import app.entity.*;
import app.entity.HUD;
import app.entity.StartSequence;
import util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SoccerPanel extends JPanel
{
    private final static Dimension minimumSize, preferredSize;

    private final SoccerModel model;
    private final Drawable[] mainDrawables;
    private final Updatable[] mainUpdatables;

    private final StartSequence startSequence;
    private final Field field;
    private final Ball ball;
    private final HUD hud;

    private volatile ArrayList<Ellipse2D> playerEllipses;

    static {
        minimumSize = new Dimension(Resource.getInteger("int.width.min"), Resource.getInteger("int.height.min"));
        preferredSize = new Dimension(Resource.getInteger("int.width"), Resource.getInteger("int.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.setSize((int)this.getPreferredSize().getWidth(), (int)this.getPreferredSize().getHeight());

        this.model = model;
        this.field = Field.getInstance();
        this.hud = HUD.getInstance();
        this.startSequence = StartSequence.getInstance();

        this.mainUpdatables = new Updatable[] {hud, startSequence};
        this.mainDrawables = new Drawable[] {field, hud, startSequence};

        playerEllipses = new ArrayList<>();

        // Veld eenmalig updaten.
        this.field.update(this);

        // Bal aanmaken - tijdelijk.
        this.ball = new Ball(this.getWidth()/2 - 10, this.getHeight()/2 - 10);

        // Update bal - tijdelijk.
        this.updateBall();

        // Update overige onderdelen.
        this.update();
    }

    public Field getInnerField()
    {
        return this.field;
    }

    public HUD getHeadsUpDisplay()
    {
        return this.hud;
    }

    public StartSequence getStartSequence()
    {
        return this.startSequence;
    }

    public void update()
    {
        // Hoofdonderdelen verversen.
        for (Updatable updatable : mainUpdatables)
            updatable.update(this);
    }

    private SoccerConstants checkForGoal()
    {
        if (field.getLeftGoal().intersects(ball.getBall())) {
            ball.setX(field.getWidth()/2 + field.getX() - 10);
            ball.setY(field.getHeight()/2 + field.getY() - 10);
            ball.accelerate(0, 0);
            SoccerSound.getInstance().addFile(SoccerSound.SOUND_CHEER).play();
            this.model.appendScore(SoccerConstants.WEST, 1);
            return SoccerConstants.WEST;
        } else if (field.getRightGoal().intersects(ball.getBall())) {
            ball.setX(field.getWidth()/2 + field.getX() - 10);
            ball.setY(field.getHeight()/2 + field.getY() - 10);
            ball.accelerate(0, 0);
            SoccerSound.getInstance().addFile(SoccerSound.SOUND_CHEER).play();
            this.model.appendScore(SoccerConstants.EAST, 1);
            return SoccerConstants.EAST;
        }

        return null;
    }

    private void updateBall()
    {
        new Thread(() -> {
            // Verantwoordelijk voor offsetberekening.
            int newWidth = this.getWidth();
            int newHeight = this.getHeight();
            int oldWidth = newWidth;
            int oldHeight = newHeight;

            while (true) {
                // Nieuwe afmetingen opvragen.
                newWidth = this.getWidth();
                newHeight = this.getHeight();

                if (oldWidth != newWidth || oldHeight != newHeight)
                    ball.offset(newWidth - oldWidth, newHeight - oldHeight);

                this.ball.update(field.getFieldTop(), field.getFieldBot(), field.getFieldLeft(), field.getFieldRight(), this.getPlayerEllipses());
                this.checkForGoal();
                this.repaint();

                try {
                    Thread.sleep(1000/60);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Oude afmetingen onthouden.
                oldWidth = this.getWidth();
                oldHeight = this.getHeight();
            }
        }).start();
    }

    public SoccerModel getActiveModel()
    {
        return this.model;
    }

    public Ball getBall()
    {
        return this.ball;
    }

    private synchronized ArrayList<Ellipse2D> getPlayerEllipses()
    {
        if (this.model.getPlayerCount() == 0)
            return new ArrayList<>(0);

        this.playerEllipses.clear();
        this.playerEllipses.addAll(this.model.getPlayers().stream().map(Player::getEllipse).collect(Collectors.toList()));
        return this.playerEllipses;
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        final int initialWidth = (int)this.getPreferredSize().getWidth(), initialHeight = (int)this.getPreferredSize().getHeight();
        final double currentWidth = this.getWidth(), currentHeight = this.getHeight();
        final double widthScale = currentWidth/initialWidth, heightScale = currentHeight/initialHeight;

        BufferedImage scene = new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sceneGraphics = (Graphics2D)scene.getGraphics();
        sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);

        // Tekent hoofdonderdelen.
        if (mainDrawables != null)
            for (Drawable component : mainDrawables)
                component.draw(sceneGraphics);

        if (this.model.existPlayers()) {
            // Tekent spelers.
            for (Drawable fieldPlayer : this.model.getPlayers())
                fieldPlayer.draw(sceneGraphics);

            // Tekent test indien spelers aangemaakt zijn.
            this.drawJoystickTest(sceneGraphics);

            // Tekent bal.
            this.ball.draw(sceneGraphics);
        }

        sceneGraphics.dispose();
        g2d.drawImage(scene, AffineTransform.getScaleInstance(widthScale, heightScale), this);
    }

    private void drawJoystickTest(Graphics2D g2d)
    {
        // Afstanden e.d.
        final int boundRadius = 20;
        final int cursorSize = 10;
        final int xOffset = this.field.getX()/2 - boundRadius/2;
        final int yOffset = this.field.getHeight() + this.field.getY() + boundRadius/2;

        // Voorbereiding.
        g2d.setStroke(new BasicStroke());

        for (SoccerRemote remote : this.model.getRemotes()) {
            final Player fieldPlayer = remote.getControlledPlayer();

            if (fieldPlayer == null)
                continue;

            final int trueXOffset = fieldPlayer.getSide() == SoccerConstants.WEST ? xOffset : xOffset + field.getX() + field.getWidth();

            final double[] coords = fieldPlayer.getMovement();
            final int x = (int)(coords[0]*boundRadius/2) + trueXOffset;
            final int y = (int)(coords[1]*boundRadius/2) + yOffset;

            // Tekent titel.
            g2d.setPaint(Color.black);
            g2d.drawString("P" + remote.getID() + " " + fieldPlayer.getX() + ", " + fieldPlayer.getY(), trueXOffset - 30, yOffset - 15);

            // Tekent omheining.
            g2d.drawOval(-boundRadius/2 + trueXOffset, -boundRadius/2 + yOffset, boundRadius, boundRadius);

            // Tekent cursor.
            g2d.setPaint(Color.red);
            g2d.fillOval(x - cursorSize/2, y - cursorSize/2, cursorSize, cursorSize);
            g2d.drawString("(" + (x - trueXOffset) + ", " + (y - yOffset) + ")", x + cursorSize + 5, y + cursorSize/2);
        }
    }

    @Override public Dimension getMinimumSize()
    {
        return minimumSize;
    }

    @Override public Dimension getPreferredSize()
    {
        return preferredSize;
    }
}
