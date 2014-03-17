package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.SessionListener;
import com.quickblox.module.videochat.core.QBVideoChatController;
import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.model.DataHolder;

import org.jivesoftware.smack.XMPPException;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 9:55 AM
 */
public class ActivityLogin extends Activity {

    private final String FIRST_USER_PASSWORD = "videoChatUser1";
    private final String FIRST_USER_LOGIN = "videoChatUser1";
    private final String SECOND_USER_PASSWORD = "videoChatUser2";
    private final String SECOND_USER_LOGIN = "videoChatUser2";

    private final int firstUserId = 217738;
    private final String firstUserName = "first user";
    private final String secondUserName = "second user";
    private final int secondUserId = 217740;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup UI
        //
        setContentView(R.layout.login_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        findViewById(R.id.loginByFirstUserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                createSession(FIRST_USER_LOGIN, FIRST_USER_PASSWORD);
            }
        });

        findViewById(R.id.loginBySecondUserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                createSession(SECOND_USER_LOGIN, SECOND_USER_PASSWORD);
            }
        });

        // Set QuickBlox credentials here
        //
        QBSettings.getInstance().fastConfigInit("2944", "RfghS97xYA29Mha", "L2EXV6ut-vD8Zus");
    }

    @Override
    public void onResume() {
        progressDialog.dismiss();
        super.onResume();
    }

    private void createSession(String login, final String password) {
        QBAuth.createSession(login, password, new QBCreateSessionCallback(password));
    }

    class QBCreateSessionCallback implements QBCallback {
        private final String password;

        QBCreateSessionCallback(String password) {
            this.password = password;
        }

        @Override
        public void onComplete(Result result) {
            if (result.isSuccess()) {
                // save current user
                DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);
                QBChatService.getInstance().loginWithUser(DataHolder.getInstance().getCurrentQbUser(), loginListener);
            }

        }

        @Override
        public void onComplete(Result result, Object context) {

        }
    }

    private SessionListener loginListener = new SessionListener() {
        @Override
        public void onLoginSuccess() {
            try {
                QBVideoChatController.getInstance().initQBVideoChatMessageListener();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            // show next activity
            showCallUserActivity();
        }

        @Override
        public void onLoginError() {
        }

        @Override
        public void onDisconnect() {
        }

        @Override
        public void onDisconnectOnError(Exception exc) {
        }
    };

    private void showCallUserActivity() {
        Intent intent = new Intent(this, ActivityCallUser.class);
        intent.putExtra("userId", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserId : firstUserId);
//        intent.putExtra("userName", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserName : firstUserName);
        startActivity(intent);
        finish();
    }
}