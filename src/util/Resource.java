package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Resource laadt XML-bestanden op dezelfde manier als dat properties geladen worden.
 * Echter maakt Resource het mogelijk om meerdere properties tegelijkertijd op te vragen met {@link #getAllWith(String)}.
 * Ten slotte zal Resource na bijvoorbeeld {@link #get(String)} het gebruikte bestand onthouden en hergebruiken.
 *
 * @author Nikita
 * @since 04-01-2014
 */
final public class Resource extends ResourceBundle
{
    public static final String NAME_DEFAULT = "config";

    private static Resource instance = null;
    private static String fileName = NAME_DEFAULT;

    private Properties props;

    private Resource(InputStream stream)
    {
        props = new Properties();

        try {
            props.loadFromXML(stream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Verkrijg een {@link ResourceBundle} van een XML-bestand met als naam '{@value #fileName}'.
     * Indien al eerder een Resource is opgevraagd, zal dezelfde worden geretourneerd.
     */
    public static Resource get()
    {
        if (instance == null)
            instance = (Resource)ResourceBundle.getBundle(fileName = NAME_DEFAULT, new Control());

        return instance;
    }

    /**
     * Verkrijg een {@link ResourceBundle} van een XML-bestand met als naam {@code name}.
     */
    public static Resource get(String name)
    {
        if (instance == null || !Resource.fileName.equals(name))
            instance = (Resource)ResourceBundle.getBundle(fileName = name.isEmpty() ? NAME_DEFAULT : name, new Control());

        return instance;
    }

    /**
     * Verkrijg alle waarden met een bepaalde voorvoegsel. Voorbeeld: frame.title, waarbij 'frame' het voorvoegsel is.
     *
     * @param prefix voorvoegsel, bijvoorbeeld 'frame'.
     * @return een {@link Map} met sleutels die het gegeven voorvoegsel hebben.
     */
    public static Map<String, String> getAllWith(String prefix)
    {
        Map<String, String> temp = new TreeMap<>();

        if (get() == null)
            return temp;

        Enumeration<String> keys = instance.getKeys();
        String key;

        while (keys.hasMoreElements())
            if ((key = keys.nextElement()).startsWith(prefix))
                temp.put(key, instance.getString(key));

        return temp;
    }

    /**
     * Deze methode is hetzelfde als het aanroepen van
     * <pre>Integer.valueOf(get().getString(String))</pre>
     */
    public static int getInteger(String key)
    {
        return Integer.valueOf(get().getString(key));
    }

    /**
     * Verkrijg de boolean waarde bij een bepaalde sleutel.
     * De waarde (niet hoogdlettergevoelig) mag enkel: True, Yes of 1 zijn om True te retourneren.
     */
    public static boolean getBoolean(String key)
    {
        String temp = get().getString(key);
        return temp.toLowerCase().matches("true|yes|1");
    }

    @Override protected Object handleGetObject(String key)
    {
        return props.getProperty(key);
    }

    @Override public Enumeration<String> getKeys()
    {
        Set<String> handleKeys = props.stringPropertyNames();
        return Collections.enumeration(handleKeys);
    }

    private static class Control extends ResourceBundle.Control
    {
        final String FORMAT_XML = "xml";

        @Override public List<String> getFormats(String baseName)
        {
            return Collections.unmodifiableList(Collections.singletonList(FORMAT_XML));
        }

        @Override public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException
        {
            if ((baseName == null) || (locale == null) || (format == null) || (loader == null))
                throw new NullPointerException();

            if (!format.equals(FORMAT_XML))
                return null;

            final String bundleName = toBundleName(baseName, locale),
                    resourceName = toResourceName(bundleName, format);

            URL url = loader.getResource(resourceName);
            if (url == null)
                return null;

            URLConnection connection = url.openConnection();
            if (connection == null)
                return null;

            if (reload)
                connection.setUseCaches(false);

            InputStream stream = connection.getInputStream();
            if (stream == null)
                return null;

            BufferedInputStream bis = new BufferedInputStream(stream);
            ResourceBundle rsc = new Resource(bis); // Stream moet nog open zijn.

            bis.close();

            return rsc;
        }
    }
}
