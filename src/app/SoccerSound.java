package app;

import util.Resource;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

class SoccerSound
{
    public static final File MUSIC_MAIN;
    public static final File SOUND_COIN;
    public static final File SOUND_CHEER;

    private static SoccerSound instance = null;

    static {
        MUSIC_MAIN = new File(Resource.get().getString("path.sound.music"));
        SOUND_COIN = new File(Resource.get().getString("path.sound.coin"));
        SOUND_CHEER = new File(Resource.get().getString("path.sound.cheer"));
    }

    private SoccerSound()
    {
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

        return new Sound(file);
    }

    public class Sound
    {
        private File file;
        private Clip clip;

        Sound(File file)
        {
            try {
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(this.file = file);
                DataLine.Info info = new DataLine.Info(Clip.class, inputStream.getFormat());
                this.clip = (Clip)AudioSystem.getLine(info);
                this.clip.open(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Sound play()
        {
            this.clip.start();
            return this;
        }

        Sound loop()
        {
            this.clip.loop(Clip.LOOP_CONTINUOUSLY);
            return this;
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
