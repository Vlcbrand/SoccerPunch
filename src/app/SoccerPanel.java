package app;

import app.entity.*;
import app.physics.BallPhysics;
import util.Resource;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

class SoccerPanel extends JPanel
{
    private static Dimension preferredSize;

    private BallPhysics ballPhysics = new BallPhysics();
    private Field field;
    private Ball ball;
    private Drawable[] drawables;
    private FieldPlayer[] players;
    Point2D ballLoc = new Point2D.Double(10, 10);

    static
    {
        preferredSize = new Dimension(Resource.getInteger("app.width"), Resource.getInteger("app.height"));
    }

    SoccerPanel(SoccerModel model)
    {
        super(null);

        this.players = model.getFieldPlayers();

        final int width = (int)this.getPreferredSize().getWidth();
        final int height = (int)this.getPreferredSize().getHeight();

        this.drawables = new Drawable[] {
            field = new Field(width, height),
                ball = new Ball(400, 400, 20)
        };
        updateBall();
    }

    private void updateBackground()
    {
        field.update(this.getWidth(), this.getHeight());
    }

    private void updateBall()
    {
        Thread ballThread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                while(true)
                {
                    try {
                        ball.ballMotion(field.getField());
                        repaint();
                        Thread.sleep(1000/60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ballThread.start();
        ball.kickBall(20, 45);
    }

    @Override public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.black);

        this.updateBackground(); // Ververs alle objecten met nieuwe waarden.

        for (Drawable object : drawables)
            object.draw(g2d);
    }

    @Override public Dimension getPreferredSize()
    {
        return preferredSize;
    }
}
