package util;

import java.awt.*;

/**
 * Eenvoudige manipulatie van {@link Color kleur}.
 *
 * @author Nikita
 * @since 05-01-2014
 */
public class Colour
{
    private Colour()
    {
    }

    /**
     * @param with een factor, hoger dan 0 en lager dan 1 (0 is dichter bij zwart).
     */
    public static Color darken(Color color, double with)
    {
        if (with < 0 || with > 1)
            return color;

        return new Color(
            Math.max((int)((double)color.getRed() * with), 0),
            Math.max((int)((double)color.getGreen() * with), 0),
            Math.max((int)((double)color.getBlue() * with), 0),
            color.getAlpha()
        );
    }

    /**
     * @param with een factor, hoger dan 1 en lager dan 2 (2 is dichter bij wit).
     */
    public static Color brighten(Color color, double with)
    {
        int r = color.getRed(), g = color.getGreen(), b = color.getBlue(), a = color.getAlpha();
        int fallback = 5;

        if (with < 1 || with > 2)
            return color;

        if (r == 0 && g == 0 && b == 0)
            return new Color(fallback, fallback, fallback, a);
        else
            return new Color(
                Math.min((int)((double)r * with), 255),
                Math.min((int)((double)g * with), 255),
                Math.min((int)((double)b * with), 255), a
            );
    }
}
