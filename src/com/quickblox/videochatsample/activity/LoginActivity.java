package com.quickblox.videochatsample.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserResult;
import com.quickblox.videochatsample.DataHolder;
import com.quickblox.videochatsample.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 9:55 AM
 */
public class LoginActivity extends Activity {

    // Set test users' credentials
    private final String FIRST_USER_PASSWORD = "videoChatUser1";
    private final String FIRST_USER_LOGIN = "videoChatUser1";
    private final String SECOND_USER_PASSWORD = "videoChatUser2";
    private final String SECOND_USER_LOGIN = "videoChatUser2";
    private final int firstUserId = 217738;
    private final int secondUserId = 217740;

    private final String firstUserName = "first user";
    private final String secondUserName = "second user";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup UI
        setContentView(R.layout.login_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        findViewById(R.id.loginByFirstUserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                authorize(FIRST_USER_LOGIN, FIRST_USER_PASSWORD);
            }
        });

        findViewById(R.id.loginBySecondUserBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                authorize(SECOND_USER_LOGIN, SECOND_USER_PASSWORD);
            }
        });


        // 1. Set QuickBlox credentials here
        //
        QBSettings.getInstance().fastConfigInit("2944", "RfghS97xYA29Mha", "L2EXV6ut-vD8Zus");
    }

    @Override
    public void onResume() {
        progressDialog.dismiss();
        super.onResume();
    }

    private void authorize(final String login, final String password) {

        // 2. Create QuickBlox session with user here
        //
        QBUser user = new QBUser(login, password);
        QBAuth.createSession(user, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    // Save current User
                    QBUser user = ((QBUserResult) result).getUser();
                    user.setPassword(password);
                    DataHolder.getInstance().setCurrentQbUser(((QBUserResult) result).getUser());

                    // open next activity
                    Intent intent = new Intent(LoginActivity.this, CallUserActivity.class);
                    intent.putExtra("userId", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserId : firstUserId);
                    intent.putExtra("userName", DataHolder.getInstance().getCurrentQbUser().getId() == firstUserId ? secondUserName : firstUserName);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}