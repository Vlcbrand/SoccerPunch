package app;

import app.entity.Ball;
import app.entity.Drawable;
import app.entity.Field;
import app.entity.FieldPlayer;
import util.Resource;

import javax.swing.*;
import java.awt.*;

class SoccerPanel extends JPanel
{
    private static Dimension preferredSize;

    private Field field;
    private Ball ball;
    private Drawable[] drawables;
    private FieldPlayer[] players;

    static {
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
            ball = new Ball(width/2, height/2)
        };

        updateBall();
    }

    private void updateBackground()
    {
        field.update(this.getWidth(), this.getHeight());
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
                // Niewe afmetingen opvragen.
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

        ball.accelerate(10, 33);
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
