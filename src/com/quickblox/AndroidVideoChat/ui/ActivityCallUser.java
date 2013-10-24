package com.quickblox.AndroidVideoChat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.quickblox.AndroidVideoChat.R;
import com.quickblox.AndroidVideoChat.model.DataHolder;
import com.quickblox.AndroidVideoChat.model.listener.OnCallDialogListener;
import com.quickblox.AndroidVideoChat.model.utils.DialogHelper;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.QBVideoChatService;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 10:06 AM
 */
public class ActivityCallUser extends Activity {

    private ProgressDialog progressDialog;
    private Button callUserBtn;
    private QBUser qbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_layout);

        callUserBtn = (Button) findViewById(R.id.callUserBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        int userId = getIntent().getIntExtra("userId", 0);
        String userName = getIntent().getStringExtra("userName");

        callUserBtn.setText(callUserBtn.getText().toString() + " " + userName);
        qbUser = new QBUser(userId);

        try {
            QBVideoChatService.getService().setQBVideoChatListener(DataHolder.getInstance().getCurrentQbUser(), qbVideoChatListener);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


        callUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                QBVideoChatService.getService().callUser(qbUser, CallType.VIDEO_AUDIO, null);
            }
        });

    }

    private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
        @Override
        public void onCameraDataReceive(byte[] videoData) {
        }

        @Override
        public void onMicrophoneDataReceive(byte[] audioData) {
        }

        @Override
        public void onOpponentVideoDataReceive(byte[] videoData) {
        }

        @Override
        public void onOpponentAudiDataReceive(byte[] audioData) {
        }

        @Override
        public void onProgress(boolean progress) {
        }

        @Override
        public void onVideoChatStateChange(CallState state, VideoChatConfig videoChatConfig) {
            switch (state) {
                case ON_ACCEPT_BY_USER:
                    Log.d("acceptByUser", "acceptByUser");
                    progressDialog.dismiss();
                    startVideoChatActivity(videoChatConfig);
                    break;
                case ON_REJECTED_BY_USER:
                    Toast.makeText(getBaseContext(), "rejectByUser", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case ON_DID_NOT_ANSWERED:
                    Toast.makeText(getBaseContext(), "didNotAnswer", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    break;
                case ON_CHAT_INITIALIZED:
                    progressDialog.dismiss();
                    startVideoChatActivity(videoChatConfig);
                    break;
                case ON_CALLING:
                    Toast.makeText(getBaseContext(), "OnCalling", Toast.LENGTH_SHORT).show();
                    showCallDialog(videoChatConfig);
                    break;
            }
        }
    };


    private void showCallDialog(final VideoChatConfig videoChatConfig) {
        DialogHelper.showCallDialog(this, new OnCallDialogListener() {
            @Override
            public void onAcceptCallClick() {
                QBVideoChatService.getService().acceptCall(videoChatConfig);
            }

            @Override
            public void onRejectCallClick() {
                QBVideoChatService.getService().rejectCall(videoChatConfig);
            }
        });
    }


    @Override
    public void onResume() {
        try {
            QBVideoChatService.getService().setQbVideoChatListener(qbVideoChatListener);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }


    private void startVideoChatActivity(VideoChatConfig videoChatConfig) {
        Intent intent = new Intent(getBaseContext(), ActivityVideoChat.class);
        intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        stopService(new Intent(getApplicationContext(), QBVideoChatService.class));
        super.onDestroy();
    }
}
