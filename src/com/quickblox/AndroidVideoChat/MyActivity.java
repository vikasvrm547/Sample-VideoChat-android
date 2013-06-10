package com.quickblox.AndroidVideoChat;

import android.media.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.quickblox.AndroidVideoChat.camera.CameraSurfaceView;
import com.quickblox.AndroidVideoChat.stun.TURNClient;

public class MyActivity extends FragmentActivity {


    private CameraSurfaceView cameraPreview;
    private ImageView pictureFromCameraImageView;

    private AudioTrack speaker;
    private AudioRecord recorder;


    //Audio Configuration.
    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private boolean status = true;

    AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
//        ServerConnect.runUdpClient();


        TURNClient turnClient = new TURNClient(new TURNClient.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        });


        pictureFromCameraImageView = (ImageView) findViewById(R.id.pictureFromCameraImageView);
        cameraPreview = (CameraSurfaceView) findViewById(R.id.camera_preview);
        cameraPreview.setPictureFromCameraImageView(pictureFromCameraImageView);
        addContentView(cameraPreview.getDrawOnTop(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        startStreaming();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        recorder.release();
        speaker.release();
    }


    public void startStreaming() {
        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {

                int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                byte[] buffer = new byte[minBufSize];
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
                recorder.startRecording();


                speaker = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, 2048, AudioTrack.MODE_STREAM);
                speaker.play();

                while (status == true) {
                    minBufSize = recorder.read(buffer, 0, buffer.length);
                    speaker.write(buffer, 0, minBufSize);
                }
            }
        });
        streamThread.start();
    }


}
