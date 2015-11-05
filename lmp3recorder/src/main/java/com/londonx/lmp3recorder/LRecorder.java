package com.londonx.lmp3recorder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.londonx.lmp3recorder.listener.AmplitudeListener;
import com.londonx.lmp3recorder.util.ByteConverter;
import com.londonx.lmp3recorder.util.WaveHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LRecorder {
    public static int SAMPLE_RATE = 11025;
    public static final int NUM_CHANNELS = 1;
    public boolean isEchoEnabled;

    private AudioTrack echoPlayer;
    private AudioRecord recorder;
    private boolean isRecording = false;
    private File rawFile;
    private File encodedFile;
    private int minBufferSize;

    private long lastReportTime;
    private AmplitudeListener amplitudeListener;
    private double amplitude;

    public LRecorder() {
        initRecorder();
    }

    public boolean isRecording() {
        return isRecording;
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
        String rawPath = recFile.getAbsolutePath() + ".pcm";
        rawFile = new File(rawPath);
        encodedFile = recFile;
        startWriting();
    }

    /**
     * pause recorder, use resume to resume
     */
    public void pause() {
        isRecording = false;
        echoPlayer.stop();
        recorder.stop();
    }

    public void resume() {
        if (isRecording) {
            throw new IllegalStateException("the recorder is recording");
        }
        isRecording = true;
        startWriting();
    }

    /**
     * stop recording and convert RAW file into MP3 File.
     *
     * @return the MP3 file
     */
    public File stop() {
        isRecording = false;
        echoPlayer.stop();
        recorder.stop();
        encodeWaveFile();
        return encodedFile;
    }

    public void destroy() {
        recorder.release();
        echoPlayer.release();
    }

    private void initRecorder() {
        minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, NUM_CHANNELS,
                AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, NUM_CHANNELS,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

        echoPlayer = new AudioTrack(AudioManager.STREAM_MUSIC,
                recorder.getSampleRate(),
                recorder.getChannelCount(),
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize,
                AudioTrack.MODE_STREAM);
    }

    private void startWriting() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                DataOutputStream output = null;
                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(rawFile, "w");
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
                        if (isEchoEnabled) {
                            if (echoPlayer.getState() != AudioTrack.PLAYSTATE_PLAYING) {
                                echoPlayer.play();
                            }
                            echoPlayer.write(buffer, 0, readSize);
                        }
                        if (System.currentTimeMillis() - lastReportTime > 16) {
                            amplitudeListener.onAmplitude(((float) amplitude) / 100f);
                            lastReportTime = System.currentTimeMillis();
                        }
                        amplitude = ByteConverter.shorts2bytes(buffer, bytes);
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
        FileInputStream in;
        FileOutputStream out;
        long byteRate = 16 * SAMPLE_RATE * NUM_CHANNELS / 8;
        try {
            in = new FileInputStream(rawFile);
            out = new FileOutputStream(encodedFile);
            long totalAudioLen = in.getChannel().size();
            long totalDataLen = totalAudioLen + 36;
            WaveHeader.writeWaveFileHeader(out, totalAudioLen, totalDataLen,
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
            boolean isDeleted = rawFile.delete();
            if (!isDeleted) {
                throw new IOException("raw file cannot be deleted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
