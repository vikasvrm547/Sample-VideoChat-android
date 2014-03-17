package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.quickblox.module.videochat.core.QBVideoChatController;
import com.quickblox.module.videochat.model.listeners.OnCameraViewListener;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;
import com.quickblox.module.videochat.views.CameraView;
import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.model.DataHolder;

import org.jivesoftware.smack.XMPPException;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.OpponentGlSurfaceView;

public class ActivityVideoChat extends Activity {

    private CameraView cameraView;
    private OpponentGlSurfaceView opponentView;
    private ProgressBar opponentImageLoadingPb;
    private VideoChatConfig videoChatConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_layout);
        initViews();
    }

    private void initViews() {
        Debugger.logConnection("initViews");

        // Setup UI
        //
        opponentView = (OpponentGlSurfaceView) findViewById(R.id.opponentView);

        cameraView = (CameraView) findViewById(R.id.cameraView);
        cameraView.setCameraFrameProcess(true);
        // Set VideoChat listener
        cameraView.setQBVideoChatListener(qbVideoChatListener);

        // Set Camera init callback
        cameraView.setFPS(6);
        cameraView.setOnCameraViewListener(new OnCameraViewListener() {
            @Override
            public void onCameraSupportedPreviewSizes(List<Camera.Size> supportedPreviewSizes) {
//                cameraView.setFrameSize(supportedPreviewSizes.get(5));
                Camera.Size firstFrameSize = supportedPreviewSizes.get(0);
                Camera.Size lastFrameSize = supportedPreviewSizes.get(supportedPreviewSizes.size() - 1);
                cameraView.setFrameSize(firstFrameSize.width > lastFrameSize.width ? lastFrameSize : firstFrameSize);
            }
        });

        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

        // VideoChat settings
        videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());

        try {
            QBVideoChatController.getInstance().setQBVideoChatListener(DataHolder.getInstance().getCurrentQbUser(), qbVideoChatListener);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.reuseCameraView();
    }

    @Override
    protected void onPause() {
        cameraView.closeCamera();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
        super.onDestroy();
    }

    OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
        @Override
        public void onCameraDataReceive(byte[] videoData) {
            if (videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
                return;
            }
            QBVideoChatController.getInstance().sendVideo(videoData);
        }

        @Override
        public void onMicrophoneDataReceive(byte[] audioData) {
            QBVideoChatController.getInstance().sendAudio(audioData);
        }

        @Override
        public void onOpponentVideoDataReceive(byte[] videoData) {
            opponentView.loadOpponentImage(videoData);
        }

        @Override
        public void onOpponentAudioDataReceive(byte[] audioData) {
            QBVideoChatController.getInstance().playAudio(audioData);
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
