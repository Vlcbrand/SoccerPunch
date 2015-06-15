package app;

import util.Resource;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

class SoccerSound
{
    public static final File MUSIC_MAIN;
    public static final File SOUND_COIN;
    public static final File SOUND_CHEER;

    private static SoccerSound instance = null;

    private Map<File, Sound> sounds;

    static {
        MUSIC_MAIN = new File(Resource.get().getString("path.sound.music"));
        SOUND_COIN = new File(Resource.get().getString("path.sound.coin"));
        SOUND_CHEER = new File(Resource.get().getString("path.sound.cheer"));
    }

    private SoccerSound()
    {
        this.sounds = new HashMap<>(3);
    }

    public static SoccerSound getInstance()
    {
        if (instance == null)
            instance = new SoccerSound();

        return instance;
    }

    public Sound addFile(File file)
    {
        if (!file.exists())
            return null;

        if (this.sounds.containsKey(file))
            return this.sounds.get(file);

        final Sound sound = new Sound(file);
        this.sounds.put(file, sound);
        return sound;
    }

    public class Sound
    {
        private Clip clip;

        Sound(File file)
        {
            try {
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
                DataLine.Info lineInfo = new DataLine.Info(Clip.class, inputStream.getFormat());
                this.clip = (Clip)AudioSystem.getLine(lineInfo);
                this.clip.open(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void play()
        {
            this.stop();

            this.clip.setFramePosition(0);
            this.clip.start();
        }

        void stop()
        {
            this.clip.stop();
        }

        void loop()
        {
            this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

        Sound setVolume(float volume)
        {
            final FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            float actualVolume = volume;

            if (volume > ctrl.getMaximum())
                actualVolume = ctrl.getMaximum();
            else if (volume < ctrl.getMinimum())
                actualVolume = ctrl.getMinimum();

            ctrl.setValue(actualVolume);
            return this;
        }
    }
}
