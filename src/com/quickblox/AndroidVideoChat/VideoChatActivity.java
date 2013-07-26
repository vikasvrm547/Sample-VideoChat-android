package com.quickblox.AndroidVideoChat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import com.quickblox.module.chat.videochat.QBVideoChat;
import com.quickblox.module.chat.videochat.QBVideoChatSettings;
import com.quickblox.module.chat.videochat.listeners.QBVideoChatListener;
import com.quickblox.module.chat.videochat.views.CameraSurfaceView;
import com.quickblox.module.chat.videochat.views.OpponentSurfaceView;

public class VideoChatActivity extends FragmentActivity {

    private CameraSurfaceView videoRecorder;
    private OpponentSurfaceView opponentSurfaceView;
    private ProgressBar opponentImageLoadingPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
        QBVideoChatSettings.getInstance().setContext(this);
        QBVideoChatSettings.getInstance().setOpponentSurfaceView(opponentSurfaceView);
        QBVideoChatSettings.getInstance().setVideoRecorder(videoRecorder);
        QBVideoChatSettings.getInstance().setOpponentImageLoadingPb(opponentImageLoadingPb);
        QBVideoChat.startVideoChat(new QBVideoChatListener() {
            @Override
            public void onCallStart() {

            }

            @Override
            public void onCallEnd() {
                finishCall();
            }
        });
    }

    private void finishCall() {
        QBVideoChat.finishVideoChat();
        finish();
    }


    private void init() {
        opponentSurfaceView = (OpponentSurfaceView) findViewById(R.id.opponentSurfaceView);
        videoRecorder = (CameraSurfaceView) findViewById(R.id.camera_preview);
        opponentImageLoadingPb = (ProgressBar) findViewById(R.id.opponentImageLoading);

    }

    @Override
    public void onStop() {
        QBVideoChat.finishVideoChat();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
