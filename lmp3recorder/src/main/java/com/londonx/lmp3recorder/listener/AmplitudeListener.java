package com.londonx.lmp3recorder.listener;

/**
 * Created by london on 15/10/13.
 * AmplitudeListener
 */
public interface AmplitudeListener {
    /**
     * Will be called when amplitude changed
     *
     * @param amplitude amplitude between 0.0f~1.0f
     */
    void onAmplitude(float amplitude);
}
