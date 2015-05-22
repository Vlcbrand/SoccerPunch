package temp;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;

public class Goal extends JComponent {
	private int locX;
	private int locY;
	private double scaleW;
	private double scaleH;
	private boolean side; //right = right, false = left
	private BufferedImage image;
	private Point2D loc;
	
	public Goal(int locX, int locY, double scaleW, double scaleH, boolean side) {
		this.locX = locX;
		this.locY = locY;
		this.scaleW = scaleW;
		this.scaleH = scaleH;
		this.side = side;
		
		try {
			image = ImageIO.read(new File("images/goal.gif"));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void draw (Graphics g) {
		super.paintComponent(g);		
		AffineTransform trans = new AffineTransform();
		
		//goal verplaatsen
		trans.translate(locX, locY);
		
		//goal schalen
		trans.scale(scaleW, scaleH);
				
		//goal draaien voor juiste zijde
		if (side) {
			trans.rotate(Math.PI / 2);
		}
		if (!side) {
			trans.rotate(Math.PI / -2);
		}
		
		//goal draaien om as
		trans.translate(-image.getWidth() / 2, -image.getHeight() / 2);
		
		//goal tekenen
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, trans, null);
	}
	
	public Point2D getLoc(){
		loc.setLocation(locX, locY);
		return loc;
	}
}