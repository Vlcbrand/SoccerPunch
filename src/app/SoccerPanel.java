package app;

import app.entity.*;
import util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

class SoccerPanel extends JPanel
{
    private final static Dimension minimumSize, preferredSize;

    private final int initialWidth, initialHeight;
    private final SoccerModel model;
    private final Drawable[] mainDrawables;

    private final StartSequence startSequence;
    private final Field field;
    private final Ball ball;
    private final HUD hud;

    private Random random = new Random();

    private List<Player> fieldPlayers;

    static {
        minimumSize = new Dimension(Resource.getInteger("int.width.min"), Resource.getInteger("int.height.min"));
        preferredSize = new Dimension(Resource.getInteger("int.width"), Resource.getInteger("int.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.model = model;

        this.initialWidth = (int)this.getPreferredSize().getWidth() - 2;
        this.initialHeight = (int)this.getPreferredSize().getHeight() - 25;

        this.mainDrawables = new Drawable[] {
            field = Field.getInstance(),
            hud = HUD.getInstance()
        };

        // Bal aanmaken - tijdelijk.
        this.ball = new Ball(initialWidth/2 - 10, initialHeight/2 - 10);

        // Startscherm opvragen.
        this.startSequence = StartSequence.getInstance();

        // Beginafmetingen voor het veld instellen.
        this.startSequence.update(initialWidth, initialHeight);
        this.field.update(initialWidth, initialHeight);
        this.hud.updateParentDimensions(initialWidth, initialHeight);
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
        // HUD verversen.
        this.hud.updateParentDimensions(this.getWidth(), this.getHeight());

        // Spelers verversen.
        this.fieldPlayers = this.model.getPlayers();

        // Startsequence verversen.
        this.startSequence.update(this.getWidth(), this.getHeight());

        //tijdelijke code
        updateBall();
        randomKick();
    }

    private SoccerConstants checkForGoal()
    {
        if (field.getLeftGoal().intersects(ball.getBall())) {
            ball.setX(field.getWidth()/2 + field.getX() - 10);
            ball.setY(field.getHeight()/2 + field.getY() - 10);
            ball.accelerate(0, 0);
            SoccerSound.getInstance().addFile(SoccerSound.SOUND_CHEER).play();
            return SoccerConstants.WEST;
        }

        if (field.getRightGoal().intersects(ball.getBall())) {
            ball.setX(field.getWidth()/2 + field.getX() - 10);
            ball.setY(field.getHeight()/2 + field.getY() - 10);
            ball.accelerate(0, 0);
            SoccerSound.getInstance().addFile(SoccerSound.SOUND_CHEER).play();
            return SoccerConstants.WEST;
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

                ball.update(field.getFieldTop(), field.getFieldBot(), field.getFieldLeft(), field.getFieldRight());
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

    private void randomKick()
    {
        new Thread(() -> {
            while (true) {
                ball.accelerate(20 + random.nextInt(50), random.nextInt(360));

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        BufferedImage scene = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D sceneGraphics = (Graphics2D)scene.getGraphics();
        sceneGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);

        // Tekent hoofdonderdelen.
        if (mainDrawables != null)
            for (Drawable component : mainDrawables)
                component.draw(sceneGraphics);

        // Tekent spelers.
        if (fieldPlayers != null)
            for (Drawable fieldPlayer : fieldPlayers)
                fieldPlayer.draw(sceneGraphics);

        // Tekent test indien spelers aangemaakt zijn.
        if (fieldPlayers != null && fieldPlayers.size() > 0)
            this.drawJoystickTest(sceneGraphics);

        // Tekent bal - tijdelijk.
        this.ball.draw(sceneGraphics);

        // Tekent startanimatie - indien nodig.
        this.startSequence.draw(sceneGraphics);

        // Scalet met het hoofdscherm.
        final double parentWidth = this.getParent().getWidth();
        final double parentHeight = this.getParent().getHeight();
        AffineTransform tx = new AffineTransform();
        tx.scale(parentWidth/this.initialWidth, parentHeight/this.initialHeight);

        sceneGraphics.dispose();
        g2d.drawImage(scene, tx, this);
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
