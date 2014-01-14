package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.core.service.ServiceInteractor;
import com.quickblox.module.videochat.model.listeners.OnCameraViewListener;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;
import com.quickblox.module.videochat.views.CameraView;
import com.quickblox.videochatsample.R;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.VideoChatViewsLoader;

public class ActivityVideoChat extends Activity {

//    private GLSurfaceView cameraView;
    private CameraView cameraView;
    private GLSurfaceView opponentView;
    private ProgressBar opponentImageLoadingPb;
    private VideoChatConfig videoChatConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chat_layout);
        initViews();
    }
//    private VideoChatViewsLoader videoChatViewsLoader;

    private void initViews() {
        Debugger.logConnection("initViews");

        // Setup UI
        //
        opponentView = (GLSurfaceView) findViewById(R.id.opponentView);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

        // VideoChat settings
        videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
        QBVideoChatService.getService().startVideoChat(videoChatConfig);

        QBVideoChatService.getService().setQbVideoChatListener(qbVideoChatListener);
        QBVideoChatService.getService().setCameraView(opponentView, cameraViewListener, this);
        cameraView.setQBVideoChatListener(qbVideoChatListener);
        cameraView.setOnCameraViewListener(cameraViewListener);
//        videoChatViewsLoader = new VideoChatViewsLoader(opponentView, this, cameraViewListener, qbVideoChatListener);
    }

    private OnCameraViewListener cameraViewListener = new OnCameraViewListener() {
        @Override
        public void onCameraInit(List<Camera.Size> supportedPreviewSizes, List<Integer> supportedPreviewFpsRates) {
//            QBVideoChatService.getService().updateCameraViewSettings(findMinimalSize(supportedPreviewSizes), findMinimalFPS(supportedPreviewFpsRates), ActivityVideoChat.this);
//            cameraView.setCameraPreviewSizeImageQualityCameraFps(findMinimalSize(supportedPreviewSizes), findMinimalFPS(supportedPreviewFpsRates), 25);
        }
    };


    private int findMinimalFPS(List<Integer> fpSs) {
        return (fpSs.get(fpSs.size() - 1) > fpSs.get(0)) ? fpSs.get(0) : fpSs.get(fpSs.size() - 1);
    }

    private Camera.Size findMinimalSize(List<Camera.Size> previewSizes) {
        return (previewSizes.get(previewSizes.size() - 1).width > previewSizes.get(0).width) ?
                previewSizes.get(0) :
                previewSizes.get(previewSizes.size() - 1);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        QBVideoChatService.getService().finishVideoChat(videoChatConfig.getSessionId());
        super.onDestroy();
    }

    OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
        @Override
        public void onCameraDataReceive(byte[] videoData, byte value) {
            if (videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
                return;
            }
            Log.d("onCameraDataReceive", "onCameraDataReceive" + videoData.length);
            ServiceInteractor.INSTANCE.sendVideoData(ActivityVideoChat.this, videoData, value);
//            videoChatViewsLoader.loadOpponentImage(videoData);
        }

        @Override
        public void onMicrophoneDataReceive(byte[] audioData) {
            ServiceInteractor.INSTANCE.sendAudioData(ActivityVideoChat.this, audioData);
        }

        @Override
        public void onOpponentVideoDataReceive(byte[] videoData) {

            Log.d("onOpponentVideoDataReceive", "onOpponentVideoDataReceive" + videoData.length);
            QBVideoChatService.getService().loadOpponentImage(videoData);
//            videoChatViewsLoader.loadOpponentImage(videoData);
        }

        @Override
        public void onOpponentAudioDataReceive(byte[] audioData) {
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
