package com.londonx.lmp3recorder.listener;

import java.io.File;

/**
 * Created by london on 15/10/14.
 * listener for SoundMixer status
 */
public interface AsyncSoundMixerListener {
    void onStartMix();

    void onMixError(Exception e);

    void onMixDone(File mixedFile);
}
