package com.londonx.lmp3recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.londonx.lmp3recorder.listener.AmplitudeListener;
import com.londonx.lmp3recorder.util.ByteConverter;
import com.londonx.lmp3recorder.util.WaveHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LRecorder {
    public static int SAMPLE_RATE = 11025;
    public static final int NUM_CHANNELS = 1;

    private AudioRecord recorder;
    private boolean isRecording = false;
    private boolean isPaused = false;
    private File rawFile;
    private File encodedFile;
    private int minBufferSize;

    private long lastReportTime;
    private AmplitudeListener amplitudeListener;

    public LRecorder() {
        initRecorder();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setAmplitudeListener(AmplitudeListener amplitudeListener) {
        this.amplitudeListener = amplitudeListener;
    }

    /**
     * Start recording
     *
     * @param recFile MP3 file.
     */
    public void start(File recFile) {
        if (!recFile.getAbsolutePath().endsWith(".wav")) {
            throw new IllegalStateException("the file must be *.wav");
        }
        if (isRecording) {
            throw new IllegalStateException("the recorder is recording");
        }
        isRecording = true;
        isPaused = false;
        String rawPath = recFile.getAbsolutePath() + ".pcm";
        rawFile = new File(rawPath);
//        if (rawFile.exists()) {
//            if (rawFile.delete()) {
//                System.out.println("rawFile deleted !!!");
//            }
//            try {
//                if (rawFile.createNewFile()) {
//                    System.out.println("new rawFile created !!!");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        encodedFile = recFile;
        startWriting();
    }

    /**
     * pause recorder, use resume to resume
     */
    public void pause() {
        isRecording = false;
        isPaused = true;
        recorder.stop();
    }

    public void resume() {
        if (isRecording) {
            throw new IllegalStateException("the recorder is recording");
        }
        isRecording = true;
        isPaused = false;
        startWriting();
    }

    /**
     * stop recording and convert RAW file into MP3 File.
     *
     * @return the MP3 file
     */
    public File stop() {
        isRecording = false;
        isPaused = false;
        recorder.stop();
        encodeWaveFile();
        return encodedFile;
    }

    public void destroy() {
        recorder.release();
        recorder = null;
    }

    private void initRecorder() {
        minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, NUM_CHANNELS,
                AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, SAMPLE_RATE, NUM_CHANNELS,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
    }

    private void startWriting() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(rawFile, "rw");
                    if (rawFile.exists()) {
                        randomAccessFile.seek(rawFile.length());
                    } else {
                        randomAccessFile.seek(0);
                    }
                    recorder.startRecording();
                    short[] buffer = new short[128];//new short[minBufferSize / 128]
                    byte[] bytes = new byte[buffer.length * 2];
                    while (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING
                            && isRecording) {

                        int readSize = recorder.read(buffer, 0, buffer.length);
                        if (readSize <= 0) {
                            break;
                        }
                        if (System.currentTimeMillis() - lastReportTime > 16) {
                            float amplitude = ByteConverter.getAmplitude(buffer);
                            amplitudeListener.onAmplitude(amplitude / 100f);
                            lastReportTime = System.currentTimeMillis();
                        }
                        ByteConverter.shorts2bytes(buffer, bytes);
                        randomAccessFile.write(bytes, 0, readSize * 2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    // make RAW(pcm) file to WAV file
    private void encodeWaveFile() {
        if (!rawFile.exists()) {
            return;
        }
        FileInputStream in;
        RandomAccessFile out;
        long byteRate = 16 * SAMPLE_RATE * NUM_CHANNELS / 8;
        try {
            in = new FileInputStream(rawFile);
            out = new RandomAccessFile(encodedFile, "rw");
            long totalAudioLen = in.getChannel().size();
            WaveHeader.writeWaveFileHeader(out, totalAudioLen,
                    SAMPLE_RATE, NUM_CHANNELS, byteRate);
            byte[] buffer = new byte[minBufferSize * 2];
            while (true) {
                int readSize = in.read(buffer);
                if (readSize < 0) {
                    break;
                }
                out.write(buffer, 0, readSize);
            }
            in.close();
            out.close();
            rawFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
