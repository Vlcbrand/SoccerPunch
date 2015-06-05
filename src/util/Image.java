package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Image is voornamelijk verantwoordelijk voor het veilig inladen van afbeelding.
 * In tegenstelling tot ImageIO, zal Image <u>altijd</u> een afbeelding produceren.
 *
 * <ul>
 *  Gebruik (altijd met file extension):
 *  <li><b>Standaard</b>: Zet {@link Image} tussen afbeeldingen en laad afbeeldingen op naam.</li>
 *  <li><b>In package</b>: Zet {@link Image} in een willekeurige package en laad afbeeldingen relatief aan project.</li>
 *  <li><b>Anders</b>: {@link Image} werkt ook met absolute paden.</li>
 * </ul>
 *
 * @author Nikita
 * @since 05-01-2014
 */
public final class Image
{
    public final static Color COLOR_DEFAULT = new Color(240, 227, 98);
    public final static int FONT_SIZE = 12, MARGIN_DEFAULT = 15;
    public final static String FOLDER_DEFAULT = "img/";

    private static Image instance = null;

    private Image()
    {
        super();
    }

    private static synchronized Image getInstance()
    {
        if (instance == null)
            instance = new Image();

        return instance;
    }

    /**
     * Eenvoudige manier om afbeeldingen te verkrijgen zonder het opvangen van Exceptions.
     *
     * @param path dit pad is relatief aan het projectdomein.
     * @return {@link BufferedImage} van een bestaande afbeelding of een dummy get als foutmelding.
     */
    public static BufferedImage get(String path)
    {
        getInstance();

        File file;

        // Indien het pad in bezit is van absolute kenmerken, deze aannemen als absoluut.
        if (path.contains(":") || path.startsWith("/")) {
            file = new File(path);
        } else if (new File(System.getProperty("user.dir") + "/" + path).exists()) {
            file = new File(System.getProperty("user.dir") + "/" + path);
        } else {
            // Poging tot het ophalen van de standaard map.
            file = new File(System.getProperty("user.dir") + "/" + FOLDER_DEFAULT + path);
        }

        if (!path.isEmpty()) {
            if (file.exists()) {
                try {
                    BufferedImage src = ImageIO.read(file);
                    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                    BufferedImage img = gc.createCompatibleImage(src.getWidth(), src.getHeight(), Transparency.BITMASK);
                    img.getGraphics().drawImage(src, 0, 0, null);
                    return img;
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                } finally {
                    //System.out.println("Name: " + path + "\nPath: " + file.getAbsolutePath() + "\nExists: " + file.exists());
                }

                return newBuffered(file.getPath() + "couldn't instance!", COLOR_DEFAULT);
            } else {
                return newBuffered(file.getPath() + " doesn't exist!", COLOR_DEFAULT, null, 10);
            }
        } else {
            return newBuffered("No path specified!", COLOR_DEFAULT);
        }
    }

    /**
     * Eenvoudige manier om meerdere afbeeldingen te verkrijgen zonder het opvangen van Exceptions.
     *
     * @param path dit pad is relatief aan het projectdomein.
     * @return {@link BufferedImage} van een bestaande afbeelding of een dummy get als foutmelding.
     */
    public static BufferedImage[] getAll(String path)
    {
        final String exp = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

        final File dir;
        if (path.contains(":") || path.startsWith("/"))
            dir = new File(path);
        else
            dir = new File(System.getProperty("user.dir"));

        if (!dir.isDirectory())
            return new BufferedImage[] {newBuffered(path + " is not a directory!")};

        File[] files = dir.listFiles((final File fdir, final String name) -> name.matches(exp));
        BufferedImage[] imgs = new BufferedImage[files.length];

        for (int i = 0; i < files.length; i++)
            if (files[i].exists())
                imgs[i] = get(files[i].getPath());

        if (imgs.length == 0)
            return new BufferedImage[] {newBuffered(path + " didn't include images!")};
        else
            return imgs;
    }

    /**
     * Converteert een {@link java.awt.Image} naar een {@link BufferedImage}.
     *
     * @param img een willekeurige afbeelding.
     * @return een {@link BufferedImage} van de oorspronkelijke afbeelding.
     */
    public static BufferedImage toBuffered(java.awt.Image img)
    {
        if (img.getHeight(null) <= 0 || img.getWidth(null) <= 0)
            return newBuffered("Image couldn't instance properly!", COLOR_DEFAULT);

        BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bimg.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return bimg;
    }

    /**
     * Maakt een {@link BufferedImage} met eigen tekst.
     *
     * @param t een foutmelding of overige tekst.
     * @return een dummy get met eigen tekst.
     */
    public static BufferedImage newBuffered(String t)
    {
        return newBuffered(t, COLOR_DEFAULT, null, 5);
    }

    /**
     * Maakt een {@link BufferedImage} met eigen tekst en kleur.
     *
     * @param t een foutmelding of overige tekst.
     * @param c achtergrondkleur van de afbeelding.
     * @param m standaard = {@value #MARGIN_DEFAULT}. Afstand vanaf randen; gebruikt CSS conventies.
     *
     * @return een dummy get met eigen tekst.
     */
    public static BufferedImage newBuffered(String t, Color c, int...m)
    {
        return newBuffered(t, c, null, m);
    }

    /**
     * Maakt een {@link BufferedImage} met eigen tekst, afmetingen en kleur.
     *
     * @param t een foutmelding of overige tekst.
     * @param c achtergrondkleur van de afbeelding.
     * @param d vast formaat voor afbeelding.
     * @param m standaard = {@value #MARGIN_DEFAULT}. Afstand vanaf randen; gebruikt CSS conventies.
     *
     * @return een dummy get met eigen tekst.
     */
    public static BufferedImage newBuffered(String t, Color c, Dimension d, int...m)
    {
        final int mt, mr, mb, ml, w, h;
        if (m.length == 1) {
            mt = mr = mb = ml = m[0];
        } else if (m.length > 3) {
            mt = m[0]; mr = m[1]; mb = m[2]; ml = m[3];
        } else if (m.length == 3 ) {
            mt = m[0]; mr = m[1]; mb = m[2]; ml = m[1];
        } else if (m.length == 2) {
            mt = m[0]; mr = m[1]; mb = m[0]; ml = m[1];
        } else {
            mt = mr = mb = ml = MARGIN_DEFAULT;
        }

        Font f = new Font("verdana", Font.ITALIC + Font.BOLD, FONT_SIZE);
        FontMetrics fm = new Canvas().getFontMetrics(f);
        Color fc = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue(), 150);

        BufferedImage bimg;
        if (d == null)
            bimg = new BufferedImage(w = fm.stringWidth(t) + mr + ml, h = (m.length > 0) ? (fm.getHeight() + mt + mb) : w, BufferedImage.TYPE_INT_ARGB);
        else
            bimg = new BufferedImage(w = (int)d.getWidth() + mr + ml, h = (d.getHeight() > 0) ? (int)d.getHeight() + mt + mb : w, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bimg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(c);
        g2d.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());

        g2d.setPaint(c);
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1f, new float[]{3f, 10f}, 0f));
        g2d.drawLine(0, 0, bimg.getWidth(), bimg.getHeight());
        g2d.drawLine(bimg.getWidth(), 0, 0, bimg.getHeight());

        g2d.setPaint(fc);
        g2d.setFont(f);
        if (d == null)
            g2d.drawString(t, ml, h/2 + (int)(fm.getHeight()/2.5));

        g2d.dispose();

        return bimg;
    }
}
