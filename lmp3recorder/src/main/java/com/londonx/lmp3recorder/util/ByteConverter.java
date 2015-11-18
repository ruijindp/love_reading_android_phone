package com.londonx.lmp3recorder.util;

/**
 * Created by london on 15/10/14.
 * tools for handle byte[]
 */
public class ByteConverter {
    public static int shorts2bytes(short[] shortData, byte[] bytes) {
        int shortArraySize = shortData.length;
        double sum = 0;
        for (int i = 0; i < shortArraySize; i++) {
            bytes[i * 2] = (byte) shortData[i];
            bytes[(i * 2) + 1] = (byte) (shortData[i] >> 8);

            sum += Math.abs(shortData[i]);
        }
        return shortArraySize * 2;
    }

    public static float getAmplitude(short[] shortData) {
        double sum = 0;
        for (short aShortData : shortData) {
            sum += Math.abs(aShortData);
        }
        return (float) Math.sqrt(sum / shortData.length);
    }

    public static void bytes2shorts(byte[] bytes, short[] shortData) {
        int shortArraySize = shortData.length;
        for (int i = 0; i < shortArraySize; i++) {
            shortData[i] = (short) ((bytes[i * 2] & 0xff) | (bytes[i * 2 + 1] & 0xff) << 8);
        }
    }
}
