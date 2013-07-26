package com.quickblox.AndroidVideoChat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.quickblox.module.chat.videochat.QBVideoChat;
import com.quickblox.module.chat.videochat.QBVideoChatSettings;
import com.quickblox.module.chat.videochat.listeners.QBVideoCallListener;
import com.quickblox.module.chat.videochat.listeners.QBVideoChatListener;
import com.quickblox.module.users.model.QBUser;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 10:06 AM
 */
public class UserListActivity extends Activity {

    private ProgressDialog progressDialog;
    private Button callUserBtn;
    private QBUser qbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_user_list);

        callUserBtn = (Button) findViewById(R.id.callUserBtn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        QBVideoChatSettings.getInstance().setContext(this);
        QBVideoChatSettings.getInstance().setCurrentUser(DataHolder.getInstance().getCurrentQbUser());
        QBVideoChatSettings.getInstance().setOpponentSurfaceViewWidth(300);
        QBVideoChatSettings.getInstance().setOpponentSurfaceViewHeight(200);

        int userId = getIntent().getIntExtra("userId", 0);
        String userName = getIntent().getStringExtra("userName");

        callUserBtn.setText(callUserBtn.getText().toString() + " " + userName);
        qbUser = new QBUser(userId);

        callUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                QBVideoChat.callUser(qbUser, new QBVideoCallListener() {
                    @Override
                    public void didNotAnswer() {
                        Toast.makeText(getBaseContext(), "didNotAnswer", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void rejectByUser() {
                        Toast.makeText(getBaseContext(), "rejectByUser", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void acceptByUser() {
                        Log.d("acceptByUser", "acceptByUser");
                        progressDialog.dismiss();
                        startVideoChatActivity();
                    }

                });
            }
        });


        QBVideoChat.setVideoChatListener(new QBVideoChatListener() {
            @Override
            public void onCallStart() {
                progressDialog.dismiss();
                startVideoChatActivity();
            }

            @Override
            public void onCallEnd() {

            }
        });
    }

    @Override
    public void onResume() {
        QBVideoChatSettings.getInstance().setContext(this);
        super.onResume();
    }


    private void startVideoChatActivity() {
        Intent intent = new Intent(getBaseContext(), VideoChatActivity.class);
        startActivity(intent);
    }
}
