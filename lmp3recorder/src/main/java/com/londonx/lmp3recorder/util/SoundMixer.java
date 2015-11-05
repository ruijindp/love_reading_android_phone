package com.londonx.lmp3recorder.util;

import com.londonx.lmp3recorder.LRecorder;
import com.londonx.lmp3recorder.listener.AsyncSoundMixerListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by london on 15/10/14.
 * mix 2 sounds into one
 */
public class SoundMixer {
    private final File mixedFile;
    private final File accompanyFile;
    private final File originalFile;

    private AsyncSoundMixerListener asyncSoundMixerListener;

    public SoundMixer(File accompanyFile, File originalFile, File mixedFile) {
        this.accompanyFile = accompanyFile;
        this.originalFile = originalFile;
        this.mixedFile = mixedFile;
        if (asyncSoundMixerListener != null) {
            asyncSoundMixerListener.onStartMix();
        }
    }

    public void syncMixWav() {
        FileInputStream accompany = null;
        FileInputStream original = null;
        FileOutputStream mix = null;
        try {
            accompany = new FileInputStream(accompanyFile);
            original = new FileInputStream(originalFile);
            // 跳过头
            byte[] accompanyHead = new byte[44];
            byte[] originalHead = new byte[44];
            int readAccompany = accompany.read(accompanyHead);
            int readOriginal = original.read(originalHead);

            int accompanySampleRate = WaveHeader.getSampleRate(accompanyHead);
            int originalSampleRate = WaveHeader.getSampleRate(originalHead);
            int accompanyChannel = WaveHeader.getChannelCount(accompanyHead);
            int originalChannel = WaveHeader.getChannelCount(originalHead);
            float magnification = accompanySampleRate / originalSampleRate *
                    (accompanyChannel / originalChannel);

            if (readAccompany == 0 || readOriginal == 0) {
                throw new IOException("accompanyFile or originalFile is 0B");
            }
            mix = new FileOutputStream(mixedFile);
            int readLength = 1024;
            byte[] accompanyReader = new byte[(int) (readLength * magnification)];
            byte[] originalReader = new byte[readLength];
            short[] accompanyShorts = new short[(int) (readLength / 2 * magnification)];
            short[] originalShorts = new short[readLength / 2];
            short[] mixedShort = new short[readLength / 2];
            byte[] outData = new byte[readLength];

            //RIFF/WAVE header
            long totalLen = original.getChannel().size() - 44;
            long byteRate = 16 * LRecorder.SAMPLE_RATE * LRecorder.NUM_CHANNELS / 8;
            WaveHeader.writeWaveFileHeader(mix, totalLen, totalLen + 36,
                    LRecorder.SAMPLE_RATE, LRecorder.NUM_CHANNELS, byteRate);
            while (true) {
                int accompanyReadSize = accompany.read(accompanyReader, 0, accompanyReader.length);
                int originalReadSize = original.read(originalReader, 0, originalReader.length);
                if (originalReadSize <= 0) {
                    break;
                }
                if (accompanyReadSize <= 0) {//seek to 0, and try to read it again
                    accompany = new FileInputStream(accompanyFile);
                    readAccompany = accompany.read(new byte[44]);
                    if (readAccompany == 0) {
                        throw new IOException("accompanyFile is 0B");
                    }
                    accompanyReadSize = accompany.read(accompanyReader, 0, accompanyReader.length);
                    if (accompanyReadSize == 0) {
                        for (int i = 0; i < accompanyReader.length; i++) {
                            accompanyReader[i] = 0;
                        }
                    }
                }
                ByteConverter.bytes2shorts(accompanyReader, accompanyShorts);
                ByteConverter.bytes2shorts(originalReader, originalShorts);
                mixVoice(accompanyShorts, originalShorts, magnification, mixedShort);
                ByteConverter.shorts2bytes(mixedShort, outData);
                mix.write(outData, 0, originalReadSize);
                mix.flush();
            }
            mix.close();
            if (asyncSoundMixerListener != null) {
                asyncSoundMixerListener.onMixDone(mixedFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (asyncSoundMixerListener != null) {
                asyncSoundMixerListener.onMixError(e);
            }
        } finally {
            try {
                if (accompany != null) {
                    accompany.close();
                }
                if (original != null) {
                    original.close();
                }
                if (mix != null) {
                    mix.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mixVoice(short[] accompany, short[] original, float magnification, short[] mixedShort) {
        for (int i = 0; i < original.length; i++) {
            mixedShort[i] = (short) (accompany[((int) (i * magnification))] * 0.75f + original[i] * 0.75f);
        }
    }

    public void setAsyncSoundMixerListener(AsyncSoundMixerListener asyncSoundMixerListener) {
        this.asyncSoundMixerListener = asyncSoundMixerListener;
    }
}
