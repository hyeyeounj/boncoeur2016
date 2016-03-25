package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


/**
 * Created by hyes on 2016. 3. 23..
 */
public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    private WaveFormView waveformView;
    private RecordingThread recordingThread;
    Context context;

    TextView record_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        context = this;
        record_btn = (TextView)findViewById(R.id.record_btn);
        record_btn.setOnClickListener(this);
        waveformView = (WaveFormView)findViewById(R.id.waveformView);

        recordingThread =new RecordingThread(context, new AudioDataReceivedListener() {
            @Override
            public void onAudioDataReceived(short[] data) {
                waveformView.setSamples(data);
            }
        });
    }

    private void startAudioRecordingSafe() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            recordingThread.startRecording();
        } else {
            Log.d("test", "qweqweq");
            //requestMicrophonePermission();
        }
    }
    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.record_btn:
                Log.d("test", "record onclick");
                if (!recordingThread.recording()) {
                    startAudioRecordingSafe();
                } else {
                    recordingThread.stopRecording();
                }
                break;
        }

    }
}

//    private static final String LOG_TAG = "AudioRecordTest";
//    private static String mFileName = null;
//
//    private RecordButton mRecordButton = null;
//    private MediaRecorder mRecorder = null;
//
//    private PlayButton   mPlayButton = null;
//    private MediaPlayer mPlayer = null;
//
//    protected void onRecord(boolean start) {
//        if (start) {
//            startRecording();
//        } else {
//            stopRecording();
//        }
//    }
//
//    protected void onPlay(boolean start) {
//        if (start) {
//            startPlaying();
//        } else {
//            stopPlaying();
//        }
//    }
//
//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }
//
//    private void stopPlaying() {
//        mPlayer.release();
//        mPlayer = null;
//    }
//
//    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
//        Log.d("test", "MaxAmplitude: " + mRecorder.getMaxAmplitude());
//    }
//
//    private void stopRecording() {
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
//    }
//
//    public RecordingActivity() {
//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
//        mFileName += "/audiorecordtest.3gp";
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//       // setContentView(R.layout.activity_recording);
//
//        LinearLayout ll = new LinearLayout(this);
//        mRecordButton = new RecordButton(this);
//        ll.addView(mRecordButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        mPlayButton = new PlayButton(this);
//        ll.addView(mPlayButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
//        setContentView(ll);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mRecorder != null) {
//            mRecorder.release();
//            mRecorder = null;
//        }
//
//        if (mPlayer != null) {
//            mPlayer.release();
//            mPlayer = null;
//        }
//    }
//
//}
