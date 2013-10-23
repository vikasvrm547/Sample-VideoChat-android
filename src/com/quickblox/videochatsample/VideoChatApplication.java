package com.quickblox.videochatsample;

import android.app.Application;
import android.content.Intent;
import com.quickblox.module.chat.videochat.VideoChatService;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 8/20/13
 * Time: 12:03 PM
 */
public class VideoChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Start videoChat service
        startService(new Intent(this, VideoChatService.class));
    }

    @Override
    public void onTerminate() {

        // Stop videoChat service
        stopService(new Intent(getApplicationContext(), VideoChatService.class));

        super.onTerminate();
    }
}