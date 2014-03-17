package com.quickblox.videochatsample.ui;

import android.app.Application;

import com.quickblox.module.chat.smack.SmackAndroid;
import com.quickblox.module.users.model.QBUser;

/**
 * Created by piryatenets on 17.03.14.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SmackAndroid.init(this);
    }
}
