package app;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;
import util.Resource;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class SoccerSound
{
    public static final File MUSIC_MAIN;
    public static final File SOUND_COIN;
    public static final File SOUND_CHEER;

    static {
        MUSIC_MAIN = new File(Resource.get().getString("path.sound.music"));
        SOUND_COIN = new File(Resource.get().getString("path.sound.coin"));
        SOUND_CHEER = new File(Resource.get().getString("path.sound.cheer"));
    }

    private SoccerSound()
    {
    }

    public static void play(File file)
    {
        if (!file.exists())
            return;

        AudioStream stream = null;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            stream = new AudioStream(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        AudioPlayer.player.start(stream);
    }

    public static void loop(File file)
    {
        if (!file.exists())
            return;

        ContinuousAudioDataStream continuous = null;

        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new FileInputStream(file));
            AudioStream audioStream = new AudioStream(inputStream);
            continuous = new ContinuousAudioDataStream(audioStream.getData());
        } catch (IOException | UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        }

        AudioPlayer.player.start(continuous);
    }
}
