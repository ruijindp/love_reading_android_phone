package com.londonx.lmp3recorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Created by london on 15/11/16.
 * async decoder
 */
public class Mp3Decoder2 {
    private File wavFile;

    private FileInputStream fis;
    private Bitstream bitstream;
    private RandomAccessFile raf;
    private Decoder decoder;

    private long currentFrame;
    private int audioLength;

    private boolean isDecoding;
    private long stepDuration = 1000;

    public Mp3Decoder2(File mp3File) {
        wavFile = new File(mp3File.getAbsolutePath() + ".wav");
        if (wavFile.exists()) {
            if (wavFile.delete()) {
                System.out.println("wav deleted !!!");
            }
        }
        try {
            fis = new FileInputStream(mp3File);
            raf = new RandomAccessFile(this.wavFile, "rw");
            bitstream = new Bitstream(fis);
            decoder = new Decoder();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            close();
        }
    }

    public File getWavFile() {
        return wavFile;
    }

    public boolean isDecoding() {
        return isDecoding;
    }

    public void setStepDuration(long stepDuration) {
        this.stepDuration = stepDuration;
    }

    public void close() {
        isDecoding = false;
        try {
            if (fis != null) {
                fis.close();
            }
            if (bitstream != null) {
                bitstream.close();
            }
            if (raf != null) {
                raf.close();
            }
        } catch (IOException |
                BitstreamException ignore) {
        }
    }

    public long nextStep() {
        while (isDecoding) {
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDecoding = true;
        int readTime = 0;
        boolean seeking = true;
        byte[] bufferData = new byte[20480];
//        if (currentFrame != 0) {
//            WaveHeader.changeLength(raf, audioLength + 1000);
//        }
        if (raf != null) {
            try {
                raf.seek(wavFile.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            long startTime = System.currentTimeMillis();
            while (readTime < stepDuration) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    break;
                }
                if (audioLength >= 0) {
                    seeking = false;
                }
                if (!seeking) {
                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                    if (currentFrame == 0) {
                        int numChannel = output.getChannelCount();
                        int sampleRate = output.getSampleFrequency() * numChannel;
                        int bit = 16;
                        long byteRate = bit * sampleRate * numChannel;
                        long totalLen = 3600000 * byteRate / (bit * 1000);//3600000s==1h

                        WaveHeader.writeWaveFileHeader(raf, totalLen,
                                sampleRate, output.getChannelCount(), byteRate);
                    }
                    short[] shorts = output.getBuffer();
                    int read = ByteConverter.shorts2bytes(shorts, bufferData);
                    raf.write(bufferData, 0, read);
                }
                bitstream.closeFrame();
                readTime += frameHeader.ms_per_frame();
                currentFrame++;
            }
            audioLength += readTime;
            isDecoding = false;
            return System.currentTimeMillis() - startTime;
        } catch (IOException | BitstreamException | DecoderException ignore) {
        }
        return 0;
    }
}
