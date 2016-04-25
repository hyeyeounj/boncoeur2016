package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 25..
 */
public class RecordingThread {
    private static final int SAMPLE_RATE = 44100;
    private String fileName = null;
    SimpleDateFormat timestamp;
    Context context;

    public RecordingThread(Context context, AudioDataReceivedListener listener) {
        mListener = listener;
        this.context = context;
    }

    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;

        //fileName = "aaa"+timestamp.format(new Date()).toString() + "REC.mp4";

    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        byte audioData[] = new byte[bufferSize];
        short[] audioBuffer = new short[bufferSize / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);


        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("test", "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = Define.RECORDED_FILEPATH + "_" + timestamp.format(new Date()).toString() + "REC.wav";
        //사용할 수 없는 파일 형식 ;; 확인

        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            Log.d("test", "File not found for recording ", e);
        }

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;
            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
            byte bData[] = short2byte(audioBuffer);
            try {
                os.write(bData, 0, audioBuffer.length * 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        record.stop();
        record.release();


        Log.v("test", String.format("Recording stopped. Samples read: %d", shortsRead));
    }


}
