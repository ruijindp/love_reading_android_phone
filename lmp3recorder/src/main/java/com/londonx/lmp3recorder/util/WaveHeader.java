package com.londonx.lmp3recorder.util;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by london on 15/10/14.
 * RIFF/WAVE head
 */
public class WaveHeader {
    private static long SAMPLE_RATE;
    private static int channels;
    private static long byteRate;

    /**
     * insert RIFF/WAVE file header
     */
    public static void writeWaveFileHeader(RandomAccessFile raf, long totalAudioLen,
                                           long SAMPLE_RATE, int channels, long byteRate)
            throws IOException {
        long totalDataLen = totalAudioLen + 36;
        if (SAMPLE_RATE == -1) {
            SAMPLE_RATE = WaveHeader.SAMPLE_RATE;
        } else {
            WaveHeader.SAMPLE_RATE = SAMPLE_RATE;
        }
        if (channels == -1) {
            channels = WaveHeader.channels;
        } else {
            WaveHeader.channels = channels;
        }
        if (byteRate == -1) {
            byteRate = WaveHeader.byteRate;
        } else {
            WaveHeader.byteRate = byteRate;
        }
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (SAMPLE_RATE & 0xff);
        header[25] = (byte) ((SAMPLE_RATE >> 8) & 0xff);
        header[26] = (byte) ((SAMPLE_RATE >> 16) & 0xff);
        header[27] = (byte) ((SAMPLE_RATE >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        raf.write(header, 0, 44);
    }

    public static void changeLength(RandomAccessFile raf, long totalAudioLen) {
        try {
            raf.seek(0);
            writeWaveFileHeader(raf, totalAudioLen, -1, -1, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getSampleRate(byte[] headData) {
        int sampleRate = (headData[24] & 0xFF)
                | ((headData[25] & 0xFF) << 8)
                | ((headData[26] & 0xFF) << 16)
                | ((headData[27] & 0xFF) << 24);

        sampleRate /= getChannelCount(headData);
        return sampleRate;
    }

    public static int getChannelCount(byte[] headData) {
        return (headData[22] & 0xFF)
                | ((headData[23] & 0xFF) << 8);
    }
}
