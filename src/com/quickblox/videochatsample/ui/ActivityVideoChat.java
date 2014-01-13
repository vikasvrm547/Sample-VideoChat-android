package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
import com.quickblox.module.videochat.core.service.ServiceInteractor;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;
import com.quickblox.module.videochat.views.QBCameraLoader;
import com.quickblox.videochatsample.R;

import java.util.List;

public class ActivityVideoChat extends Activity {

//    private CameraView cameraView;
//    private OpponentGLSurfaceView opponentSurfaceView;
//    private ProgressBar opponentImageLoadingPb;
//    private VideoChatConfig videoChatConfig;
//    private OpponentGLSurfaceViewRenderer opponentGLSurfaceViewRenderer;

    QBCameraLoader qbCameraLoader;

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
//        opponentSurfaceView = (OpponentGLSurfaceView) findViewById(R.id.opponentSurfaceView);
//        opponentGLSurfaceViewRenderer = new OpponentGLSurfaceViewRenderer();
//        opponentSurfaceView.setRenderer(opponentGLSurfaceViewRenderer);

//        cameraView = (CameraView) findViewById(R.id.camera_preview);
//        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

        // VideoChat settings
//        videoChatConfig = getIntent().getParcelableExtra(VideoChatConfig.class.getCanonicalName());
//        QBVideoChatService.getService().startVideoChat(videoChatConfig);

//        cameraView.setQBVideoChatListener(qbVideoChatListener);


    }

    @Override
    public void onResume() {
//        QBVideoChatService.getService().setQbVideoChatListener(qbVideoChatListener);
//        cameraView.setQBVideoChatListener(qbVideoChatListener);
//        cameraView.setOnCameraViewListener(new OnCameraViewListener() {
//            @Override
//            public void onCameraInit(List<Camera.Size> cameraPreviewSizes, List<Integer> listFps) {
//                cameraView.setCameraPreviewSizeImageQualityCameraFps(findMinimalSize(cameraPreviewSizes), 100, findMinimalFPS(listFps));
//            }
//        });

        GLSurfaceView cameraGlSurfaceView = (GLSurfaceView) findViewById(R.id.cameraGlSurfaceView);
        qbCameraLoader = new QBCameraLoader(cameraGlSurfaceView, this);
//        QBCameraLoader.setCameraView(cameraGlSurfaceView, this);
//        Ap
        super.onResume();
    }

    private int findMinimalFPS(List<Integer> fpSs) {
        return (fpSs.get(fpSs.size() - 1) > fpSs.get(0)) ? fpSs.get(0) : fpSs.get(fpSs.size() - 1);
    }

    private Camera.Size findMinimalSize(List<Camera.Size> previewSizes) {
        Log.d("CAMERAPREVIEWSIZE", "size=" + previewSizes.get(0).width + " and " + previewSizes.get(0).height);
        Log.d("CAMERAPREVIEWSIZE", "size=" + previewSizes.get(previewSizes.size() - 1).width + " and " + previewSizes.get(previewSizes.size() - 1).height);
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
//        try {
//            QBVideoChatService.getService().finishVideoChat(videoChatConfig.getSessionId());
//        } catch (Exception e) {
//            /*IGNORE*/
//        }
        super.onDestroy();
    }

    OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
        @Override
        public void onCameraDataReceive(byte[] videoData, byte value) {
//            opponentSurfaceView.getOpponentGLSurfaceViewRenderer().setData(videoData);
//            if (videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
//                return;
//            }
//            ServiceInteractor.INSTANCE.sendVideoData(ActivityVideoChat.this, videoData, value);
        }

        @Override
        public void onMicrophoneDataReceive(byte[] audioData) {
            ServiceInteractor.INSTANCE.sendAudioData(ActivityVideoChat.this, audioData);
        }

        @Override
        public void onOpponentVideoDataReceive(byte[] videoData) {
//            opponentGLSurfaceViewRenderer.setData(videoData);
        }

        @Override
        public void onOpponentAudioDataReceive(byte[] audioData) {
            QBVideoChatService.getService().playAudio(audioData);
        }

        @Override
        public void onProgress(boolean progress) {
//            opponentImageLoadingPb.setVisibility(progress ? View.VISIBLE : View.GONE);
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
