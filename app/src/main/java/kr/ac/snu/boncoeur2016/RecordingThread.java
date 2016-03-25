package kr.ac.snu.boncoeur2016;

import android.content.ContentValues;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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
        timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        fileName = "aaaaa"+timestamp.format(new Date()).toString() + "REC.mp4";
        ContentValues values = new ContentValues(10);

        values.put(MediaStore.MediaColumns.TITLE, Define.RECORDED_FILEPATH + fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp4");
        values.put(MediaStore.Audio.Media.DATA, Define.RECORDED_FILEPATH + fileName);

        Uri audioUri = context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

    }

    private void record() {
        Log.d("test", "Record Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

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

        Log.v("test", "Start recording");

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;

            // Notify waveform
            mListener.onAudioDataReceived(audioBuffer);
        }

        record.stop();
        record.release();

        Log.v("test", String.format("Recording stopped. Samples read: %d", shortsRead));
    }


}
