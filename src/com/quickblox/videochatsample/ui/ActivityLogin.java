package com.quickblox.videochatsample.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.videochatsample.R;
import com.quickblox.videochatsample.model.DataHolder;

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

        // Create QuickBlox session with user
        //
        QBAuth.createSession(login, password, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    // save current user
                    DataHolder.getInstance().setCurrentQbUser(((QBSessionResult) result).getSession().getUserId(), password);

                    // show next activity
                    showCallUserActivity();
                }
            }
        });
    }

    private void showCallUserActivity() {
        Intent intent = new Intent(this, ActivityCallUser.class);
        intent.putExtra("userId", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserId : firstUserId);
        intent.putExtra("userName", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserName : firstUserName);
        intent.putExtra("myId", DataHolder.getInstance().getCurrentQbUser().getId() != firstUserId ? secondUserId : firstUserId);
        intent.putExtra("myName", DataHolder.getInstance().getCurrentQbUser().getId() != firstUserId ? secondUserName : firstUserName);
        startActivity(intent);
        finish();
    }
}