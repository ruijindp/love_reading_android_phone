package com.londonx.lmp3recorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created by london on 15/10/16.
 * Mp3 decoder
 */
public class Mp3Decoder {
    public static int getSampleRate(File file) {
        int sampleRate = 0;
        FileInputStream fis = null;
        Bitstream bitstream = null;
        try {
            fis = new FileInputStream(file);
            bitstream = new Bitstream(fis);
            Decoder decoder = new Decoder();
            long totalMs = 0;
            while (true) {
                Header frameHeader = bitstream.readFrame();
                totalMs += frameHeader.ms_per_frame();
                boolean seeking = true;
                if (totalMs >= 0) {
                    seeking = false;
                }
                if (!seeking) {
                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                    int numChannel = output.getChannelCount();
                    sampleRate = output.getSampleFrequency() * numChannel;
                    break;
                }
            }
        } catch (IOException | BitstreamException | DecoderException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitstream != null) {
                try {
                    bitstream.close();
                } catch (BitstreamException e) {
                    e.printStackTrace();
                }
            }
        }
        return sampleRate;
    }

    public static File syncDecode(File file, long requestDuration)
            throws IOException {

        float totalMs = 0;
        boolean seeking = true;
        boolean hasHeader = false;
        File wavFile = new File(file.getAbsolutePath() + ".wav");
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(wavFile);
        try {
            Bitstream bitstream = new Bitstream(fis);
            Decoder decoder = new Decoder();

            byte[] bufferData = new byte[20480];

            long readTime = 0;
            while (true) {
                if (readTime > requestDuration) {
                    break;
                }
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    break;
                }
                totalMs += frameHeader.ms_per_frame();
                if (totalMs >= 0) {
                    seeking = false;
                }
                if (!seeking) {
                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                    if (!hasHeader) {
                        hasHeader = true;
                        int numChannel = output.getChannelCount();
                        int sampleRate = output.getSampleFrequency() * numChannel;
                        int bit = 16;
                        long byteRate = bit * sampleRate * numChannel;
                        long totalLen = requestDuration * byteRate / (bit * 1000);
                        WaveHeader.writeWaveFileHeader(fos, totalLen, totalLen + 36,
                                sampleRate, output.getChannelCount(), byteRate);
                    }
                    short[] shorts = output.getBuffer();
                    ByteConverter.shorts2bytes(shorts, bufferData);
                    fos.write(bufferData, 0, shorts.length * 2);
                    fos.flush();
                }
                bitstream.closeFrame();
                readTime += frameHeader.ms_per_frame();
            }
        } catch (BitstreamException | DecoderException e) {
            e.printStackTrace();
        } finally {
            fis.close();
            fos.close();
        }
        return wavFile;
    }
}
