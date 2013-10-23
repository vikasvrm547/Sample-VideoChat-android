package com.quickblox.videochatsample.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.quickblox.module.chat.videochat.listeners.OnVideoChatServiceListener;
import com.quickblox.module.chat.videochat.model.VideoChatConfig;
import com.quickblox.module.chat.videochat.VideoChatService;
import com.quickblox.module.chat.videochat.model.CallState;
import com.quickblox.module.chat.videochat.model.CallType;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.videochatsample.DataHolder;
import com.quickblox.videochatsample.DialogHelper;
import com.quickblox.videochatsample.OnCallDialogListener;
import com.quickblox.videochatsample.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 10:06 AM
 */
public class CallUserActivity extends Activity {

    private ProgressDialog progressDialog;
    private Button callUserBtn;
    private QBUser qbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup UI
        setContentView(R.layout.call_layout);

        callUserBtn = (Button) findViewById(R.id.callUserBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        int userId = getIntent().getIntExtra("userId", 0);
        String userName = getIntent().getStringExtra("userName");
        callUserBtn.setText(callUserBtn.getText().toString() + " " + userName);
        callUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                // Call to opponent
                //
                VideoChatService.getService().callUser(qbUser, CallType.VIDEO_AUDIO, null);
            }
        });

        qbUser = new QBUser(userId);
    }

    @Override
    public void onResume() {
        if(VideoChatService.getService() != null){
            VideoChatService.getService().setVideoChatListener(DataHolder.getInstance().getCurrentQbUser(), videoChatListener);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
//        // Stop videoChat service
//        stopService(new Intent(getApplicationContext(), VideoChatService.class));

        super.onDestroy();
    }

    private OnVideoChatServiceListener videoChatListener = new OnVideoChatServiceListener() {
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
                VideoChatService.getService().acceptCall(videoChatConfig);
            }

            @Override
            public void onRejectCallClick() {
                VideoChatService.getService().rejectCall(videoChatConfig);
            }
        });
    }

    private void startVideoChatActivity(VideoChatConfig videoChatConfig) {
        Intent intent = new Intent(getBaseContext(), VideoChatActivity.class);
        intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
        startActivity(intent);
    }
}
