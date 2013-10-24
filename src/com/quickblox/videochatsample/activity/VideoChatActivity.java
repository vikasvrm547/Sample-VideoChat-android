package com.quickblox.videochatsample.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.quickblox.module.chat.videochat.VideoChatService;
import com.quickblox.module.chat.videochat.listeners.OnCameraViewListener;
import com.quickblox.module.chat.videochat.listeners.OnVideoChatServiceListener;
import com.quickblox.module.chat.videochat.listeners.VideoChatViewListener;
import com.quickblox.module.chat.videochat.model.CallState;
import com.quickblox.module.chat.videochat.model.VideoChatConfig;
import com.quickblox.module.chat.videochat.views.CameraView;
import com.quickblox.module.chat.videochat.views.OpponentView;
import com.quickblox.videochatsample.R;

public class VideoChatActivity extends FragmentActivity {

    private CameraView cameraView;
    private OpponentView opponentSurfaceView;
    private ProgressBar opponentImageLoadingPb;
    private VideoChatConfig videoChatConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoChatConfig = (VideoChatConfig) getIntent().getParcelableExtra(
                VideoChatConfig.class.getCanonicalName());
        VideoChatService.getService().startVideoChat(videoChatConfig);

        // Setup UI
        setContentView(R.layout.main_layout);
        opponentSurfaceView = (OpponentView) findViewById(R.id.opponentSurfaceView);
        cameraView = (CameraView) findViewById(R.id.camera_preview);


        VideoChatService.getService().setVideoChatViewListener(videoChatViewListener);

        cameraView.setCameraViewListener(new OnCameraViewListener() {
            @Override
            public void onReceiveFrame(byte[] cameraData) {
                VideoChatService service = VideoChatService.getService();
                if(service != null){
                    service.sendVideoData(cameraData);
                }
            }
        });
//        cameraView.switchCamera();
        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);


        // Start VideoChat
        //

        VideoChatService.getService().setVideoChatServiceListener(videoChatServiceListener);

        VideoChatService.getService().startVideoChatWith(videoChatConfig.getSessionId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.releaseCamera();
    }

    @Override
    public void onStop() {
        VideoChatService.getService().finishVideoChat(videoChatConfig.getSessionId());
        super.onStop();
    }

    private void finishCall() {
        finish();
    }

    VideoChatViewListener videoChatViewListener = new VideoChatViewListener() {
        @Override
        public void onReceiveData(byte[] data) {
            opponentSurfaceView.setData(data);
        }

        @Override
        public void onProgress(boolean progress) {
            opponentImageLoadingPb.setVisibility(progress ? View.VISIBLE : View.GONE);
        }
    };

    OnVideoChatServiceListener videoChatServiceListener = new OnVideoChatServiceListener() {
        @Override
        public void onVideoChatStateChange(CallState callState, VideoChatConfig videoChatConfig) {
            if (callState == CallState.ON_CALL_END) {
                Log.d("finishVideoCall", "finishVideoCall");
                finishCall();
            }
        }
    };
}
