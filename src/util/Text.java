package util;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

/**
 * Text biedt methoden voor het nauwkeurig verkrijgen van eigenschappen van tekst.
 * Vóór het opvragen van eigenschappen de gewenste font voor de gegeven tekst gebruiken.
 *
 * @author Nikita
 * @since 05-01-2014
 */
public final class Text
{
    private Text()
    {
    }

    public static class Double
    {
        public static double getWidth(Graphics2D g2d, String text)
        {
            return getBounds(g2d, text).getWidth();
        }

        public static double getHeight(Graphics2D g2d, String text)
        {
            return getBounds(g2d, text).getHeight();
        }
    }

    public static class Integer
    {
        public static int getWidth(Graphics2D g2d, String text)
        {
            return (int)(getBounds(g2d, text).getWidth() + .5d);
        }

        public static int getHeight(Graphics2D g2d, String text)
        {
            return (int)(getBounds(g2d, text).getHeight() + .5d);
        }
    }

    public static Rectangle getBounds(String text)
    {
        return getBounds(null, text);
    }

    public static Rectangle getBounds(Graphics2D g2d, String text)
    {
        return getBounds(g2d, text, 0, 0);
    }

    public static Rectangle getBounds(Graphics2D g2d, String text, float x, float y)
    {
        final FontRenderContext context = g2d.getFontRenderContext();
        GlyphVector gv = g2d.getFont().createGlyphVector(context, text);
        return gv.getPixelBounds(null, x, y);
    }
}
