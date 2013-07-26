package com.quickblox.AndroidVideoChat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.chat.videochat.QBVideoChat;
import com.quickblox.module.chat.videochat.QBVideoChatSettings;
import com.quickblox.module.chat.videochat.listeners.QBVideoCallListener;
import com.quickblox.module.chat.videochat.listeners.QBVideoChatListener;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserPagedResult;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 6/17/13
 * Time: 10:06 AM
 */
public class UserListActivity extends Activity {

    private ListView userListView;
    private UserListAdapter userListAdapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_user_list);
        userListView = (ListView) findViewById(R.id.userListView);
        getUserList();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        QBVideoChatSettings.getInstance().setContext(this);
        QBVideoChatSettings.getInstance().setCurrentUser(DataHolder.getInstance().getCurrentQbUser());
        QBVideoChatSettings.getInstance().setOpponentSurfaceViewWidth(300);
        QBVideoChatSettings.getInstance().setOpponentSurfaceViewHeight(200);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.show();
                DataHolder.getInstance().setUserForVideoChat((QBUser) userListAdapter.getItem(i));
                QBVideoChat.callUser((QBUser) userListAdapter.getItem(i), new QBVideoCallListener() {
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


    private void getUserList() {
        QBUsers.getUsers(new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    applyUserListAdapter(((QBUserPagedResult) result).getUsers());
                }
            }
        });
    }


    private void applyUserListAdapter(List<QBUser> userList) {
        userListAdapter = new UserListAdapter(this, userList);
        userListView.setAdapter(userListAdapter);
        progressDialog.dismiss();
    }
}
