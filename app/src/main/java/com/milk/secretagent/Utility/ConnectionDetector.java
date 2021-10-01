package com.milk.secretagent.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Milk on 2015/8/7.
 */
public class ConnectionDetector {
    private static ConnectionDetector ourInstance = new ConnectionDetector();
    private Context m_Context;

    public static ConnectionDetector getInstance() {
        return ourInstance;
    }

    private ConnectionDetector() {
    }

    public void initialize(Context context) {
        this.m_Context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.m_Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivityManager) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (null != networkInfos) {
                for (NetworkInfo info : networkInfos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
