package com.jingye.coffeemac.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by niubolity on 2015/7/22.
 */
public class AudioPlayer {
    private static AudioPlayer player = new AudioPlayer();

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        return player;
    }

    private MediaPlayer mediaPlayer;

    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void play(Context c, int resource) {
        if(mediaPlayer != null &&  mediaPlayer.isPlaying()) {
            stop();
        }

        try {
            if(mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(c, resource);
                mediaPlayer.setVolume(1.0f,1.0f);
                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stop();
                    }
                });
            }
            mediaPlayer.start();
        } catch (Exception ex) {
            Log.d("Audio", "create failed:", ex);
            // fall through
        }
    }
}
