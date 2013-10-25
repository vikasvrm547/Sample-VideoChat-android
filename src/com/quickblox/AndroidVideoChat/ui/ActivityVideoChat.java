package com.quickblox.AndroidVideoChat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.AndroidVideoChat.R;
import com.quickblox.module.videochat.core.QBVideoChatService;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.views.CameraView;
import com.quickblox.module.videochat.views.OpponentView;

public class ActivityVideoChat extends Activity {

    private CameraView cameraView;
    private OpponentView opponentSurfaceView;
    private ProgressBar opponentImageLoadingPb;
    private VideoChatConfig videoChatConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_layout);
        initViews();
    }

    private void initViews() {

        opponentSurfaceView = (OpponentView) findViewById(R.id.opponentSurfaceView);
        cameraView = (CameraView) findViewById(R.id.camera_preview);
        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

        // VideoChat
        videoChatConfig = (VideoChatConfig) getIntent().getParcelableExtra(
                VideoChatConfig.class.getCanonicalName());
        QBVideoChatService.getService().setQbVideoChatListener(qbVideoChatListener);
        cameraView.setCameraViewListener(qbVideoChatListener);
        cameraView.setCameraStickyMode(false);
        QBVideoChatService.getService().startVideoChat(videoChatConfig);
    }

    @Override
    public void onStop() {
        QBVideoChatService.getService().finishVideoChat(videoChatConfig.getSessionId());
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
        @Override
        public void onCameraDataReceive(byte[] videoData) {
            QBVideoChatService.getService().sendVideoData(videoData);
        }

        @Override
        public void onMicrophoneDataReceive(byte[] audioData) {
            QBVideoChatService.getService().sendAudioData(audioData);
        }

        @Override
        public void onOpponentVideoDataReceive(byte[] videoData) {
            opponentSurfaceView.setData(videoData);
        }

        @Override
        public void onOpponentAudiDataReceive(byte[] audioData) {
            QBVideoChatService.getService().playAudio(audioData);
        }

        @Override
        public void onProgress(boolean progress) {
            opponentImageLoadingPb.setVisibility(progress ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onVideoChatStateChange(CallState callState, VideoChatConfig chat) {
            switch (callState) {
                case ON_CALL_START:
                    Toast.makeText(getBaseContext(), getString(R.string.call_start_txt), Toast.LENGTH_SHORT).show();
                    break;
                case ON_CANCELED_CALL:
                    Toast.makeText(getBaseContext(), getString(R.string.call_canceled_txt), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ON_CALL_END:
                    finish();
                    break;
            }
        }
    };


}
