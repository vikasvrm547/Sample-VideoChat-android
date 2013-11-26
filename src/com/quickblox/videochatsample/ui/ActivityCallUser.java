package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.quickblox.module.videochat.model.utils.Debugger;
import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.model.DataHolder;
import com.quickblox.videochatsample.model.listener.OnCallDialogListener;
import com.quickblox.videochatsample.model.utils.DialogHelper;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.service.QBVideoChatService;
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

    private static final String TAG = ActivityCallUser.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Button audioCallBtn;
    private Button videoCallBtn;
    private QBUser qbUser;
    private boolean isCanceledVideoCall;
    private VideoChatConfig videoChatConfig;
    private TextView txtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_layout);
        initViews();
    }

    private void initViews() {
        int userId = getIntent().getIntExtra("userId", 0);
        String userName = getIntent().getStringExtra("userName");
        String myName = getIntent().getStringExtra("myName");
        qbUser = new QBUser(userId);
        isCanceledVideoCall = true;

        // Setup UI
        //
        txtName = (TextView) findViewById(R.id.txtName);
        audioCallBtn = (Button) findViewById(R.id.audioCallBtn);
        videoCallBtn = (Button) findViewById(R.id.videoCallBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        txtName.setText("You logged in as " + myName);

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isCanceledVideoCall) {
                    QBVideoChatService.getService().stopCalling(videoChatConfig);
                }
            }
        });

        videoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressDialog != null && !progressDialog.isShowing()){
                    progressDialog.show();
                }
                videoChatConfig = QBVideoChatService.getService().callUser(qbUser, CallType.VIDEO_AUDIO, null);
            }
        });

        audioCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog != null && !progressDialog.isShowing()){
                    progressDialog.show();
                }
                videoChatConfig = QBVideoChatService.getService().callUser(qbUser, CallType.AUDIO, null);
            }
        });
//        String userName = getIntent().getStringExtra("userName");
//        audioCallBtn.setText(audioCallBtn.getText().toString() + " " + userName);

        // Set VideoCHat listener
        //
        QBUser currentQbUser = DataHolder.getInstance().getCurrentQbUser();
        Debugger.logConnection("setQBVideoChatListener: " + (currentQbUser == null));
        try {
            QBVideoChatService.getService().setQBVideoChatListener(currentQbUser, qbVideoChatListener);
        } catch (Exception e){
            /*IGNORE*/
        }
    }

    private OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {

        @Override
        public void onVideoChatStateChange(CallState state, VideoChatConfig receivedVideoChatConfig) {
            videoChatConfig = receivedVideoChatConfig;
            isCanceledVideoCall = false;
            switch (state) {
                case ON_CALLING:
                    showCallDialog();
                    break;
                case ON_ACCEPT_BY_USER:
                    progressDialog.dismiss();
                    startVideoChatActivity();
                    break;
                case ON_REJECTED_BY_USER:
                    progressDialog.dismiss();
                    break;
                case ON_DID_NOT_ANSWERED:
                    progressDialog.dismiss();
                    break;
                case ON_CANCELED_CALL:
                    isCanceledVideoCall = true;
                    videoChatConfig = null;
                    break;
                case ON_START_CONNECTING:
                    progressDialog.dismiss();
                    startVideoChatActivity();
                    break;
            }
        }
    };


    private void showCallDialog() {
        DialogHelper.showCallDialog(this, new OnCallDialogListener() {
            @Override
            public void onAcceptCallClick() {
                if (videoChatConfig == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.call_canceled_txt), Toast.LENGTH_SHORT).show();
                    return;
                }
                QBVideoChatService.getService().acceptCall(videoChatConfig);
            }

            @Override
            public void onRejectCallClick() {
                if (videoChatConfig == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.call_canceled_txt), Toast.LENGTH_SHORT).show();
                    return;
                }
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


    private void startVideoChatActivity() {
        Intent intent = new Intent(getBaseContext(), ActivityVideoChat.class);
        intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        stopService(new Intent(getApplicationContext(), QBVideoChatService.class));
        super.onDestroy();
    }
}
